package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferDto;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.services.AccountService;
import com.techelevator.tenmo.services.TransferService;
import com.techelevator.tenmo.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.AccountNotFoundException;
import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/tenmo/transfers")
@PreAuthorize("hasRole('USER')")
public class TransferController {

    @Autowired
    private TransferService transferService;
    @Autowired
    private UserService userService;

    @PostMapping("/send")
    public ResponseEntity<Transfer> createSendTransfer(@Valid @RequestBody TransferDto transferDto, Principal principal) throws AccountNotFoundException {
        User currentUser = userService.getUserByUsername(principal.getName());

        Transfer newTransfer = null;

        if (currentUser.getId() == transferDto.getAccountToId()) {
            throw new IllegalArgumentException("Cannot transfer to same account");
        }

        newTransfer = transferService.initiateSendTransfer(transferDto.getAccountFromId(), transferDto.getAccountToId(), transferDto.getAmount());

        return new ResponseEntity<>(newTransfer, HttpStatus.CREATED);
    }

    @PostMapping("/request")
    public ResponseEntity<Transfer> createRequestTransfer(@Valid @RequestBody TransferDto transferDto, Principal principal) {
        User currentUser = userService.getUserByUsername(principal.getName());

        Transfer newTransfer = null;

        if (currentUser.getId() == transferDto.getAccountToId()) {
            throw new IllegalArgumentException("Cannot transfer to same account");
        }

        newTransfer = transferService.initiateRequestTransfer(transferDto.getAccountFromId(), transferDto.getAccountToId(), transferDto.getAmount());

        return new ResponseEntity<>(newTransfer, HttpStatus.CREATED);
    }

    @PutMapping("/request/{transferId}/accept")
    public ResponseEntity<Transfer> updateAcceptTransfer(@PathVariable int transferId) throws AccountNotFoundException {
        Transfer newTransfer = transferService.acceptRequestTransfer(transferId);

        return new ResponseEntity<>(newTransfer, HttpStatus.ACCEPTED);
    }

    @PutMapping("/request/{transferId}/reject")
    public ResponseEntity<Transfer> updateRejectTransfer(@PathVariable int transferId) {
        Transfer newTransfer = transferService.rejectRequestTransfer(transferId);

        return new ResponseEntity<>(newTransfer, HttpStatus.ACCEPTED);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<Transfer>> getTransfersForUser(@PathVariable int userId) {
        List<Transfer> transfers = transferService.getTransfersByUserId(userId);

        return ResponseEntity.ok(transfers);
    }

    @GetMapping("/{transferId}/details")
    public ResponseEntity<Transfer> getTransferDetails(@PathVariable int transferId) {
        Transfer transfer = transferService.getTransferById(transferId);

        return ResponseEntity.ok(transfer);
    }
}
