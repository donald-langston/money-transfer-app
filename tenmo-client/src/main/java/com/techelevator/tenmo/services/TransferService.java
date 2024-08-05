package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferDto;
import com.techelevator.util.BasicLogger;
import org.springframework.http.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public class TransferService {

    public static final String API_BASE_URL = "http://localhost:8080/tenmo/transfers/";
    private RestTemplate restTemplate = new RestTemplate();

    private String authToken = null;

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    private AuthenticatedUser currentUser;

    public TransferService(AuthenticatedUser currentUser) {
        this.currentUser = currentUser;
    }

    public Transfer send(TransferDto newTransferDto) {
        HttpEntity<TransferDto> entity = makeTransferDtoEntity(newTransferDto);
        ResponseEntity<Transfer> returnedEntity = null;

        try {
            returnedEntity = restTemplate.exchange(API_BASE_URL + "/send", HttpMethod.POST, entity, Transfer.class);
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }

        return returnedEntity.getBody();
    }

    public Transfer request(TransferDto newTransferDto) {
        HttpEntity<TransferDto> entity = makeTransferDtoEntity(newTransferDto);
        ResponseEntity<Transfer> returnedEntity = null;

        try {
            returnedEntity = restTemplate.exchange(API_BASE_URL + "/request", HttpMethod.POST, entity, Transfer.class);
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }

        return returnedEntity.getBody();
    }

    public Transfer[] getTransfersForUser(int userId) {
        Transfer[] transfers = null;
        try {
            ResponseEntity<Transfer[]> response =
                    restTemplate.exchange(API_BASE_URL + userId, HttpMethod.GET, makeAuthEntity(), Transfer[].class);
            transfers = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return transfers;
    }

    public Transfer getTransferDetails(int transferId) {
        Transfer transfer = null;

        try {
            ResponseEntity<Transfer> response = restTemplate.exchange(API_BASE_URL + transferId + "/details",
                    HttpMethod.GET, makeAuthEntity(), Transfer.class);
            transfer = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {

            System.out.println(e.getCause());
            BasicLogger.log(e.getMessage());

        }

        return transfer;
    }

    public Transfer updateAcceptTransfer(int transferId) {
        //boolean success = false;
        ResponseEntity<Transfer> returnedTransfer = null;

        try {
            returnedTransfer = restTemplate.exchange(
                    API_BASE_URL + "request/" + transferId + "/accept", HttpMethod.PUT, makeAuthEntity(), Transfer.class);
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return returnedTransfer.getBody();
    }

    public Transfer updateRejectTransfer(int transferId) {
        ResponseEntity<Transfer> transfer = null;
        try {
            transfer = restTemplate.exchange(
                    API_BASE_URL + "request/" + transferId + "/reject", HttpMethod.PUT, makeAuthEntity(), Transfer.class);
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }

        return transfer.getBody();
    }

    private HttpEntity<Transfer> makeTransferEntity(Transfer transfer) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(currentUser.getToken());
        return new HttpEntity<>(transfer, headers);
    }

    private HttpEntity<TransferDto> makeTransferDtoEntity(TransferDto transferDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(currentUser.getToken());
        return new HttpEntity<>(transferDto, headers);
    }

    private HttpEntity<Void> makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(currentUser.getToken());
        return new HttpEntity<>(headers);
    }
}
