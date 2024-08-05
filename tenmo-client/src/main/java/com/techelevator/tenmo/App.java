package com.techelevator.tenmo;

import com.techelevator.tenmo.model.*;
import com.techelevator.tenmo.services.*;

import java.math.BigDecimal;
import java.util.Scanner;

public class App {

    private static final String API_BASE_URL = "http://localhost:8080/";

    private final ConsoleService consoleService = new ConsoleService();
    private final AuthenticationService authenticationService = new AuthenticationService(API_BASE_URL);

    private AuthenticatedUser currentUser;

    private AccountService accountService;

    private TransferService transferService;

    private UserService userService;



    public static void main(String[] args) {
        App app = new App();
        app.run();
    }

    private void run() {
        consoleService.printGreeting();
        loginMenu();
        if (currentUser != null) {
            mainMenu();
        }
    }
    private void loginMenu() {
        int menuSelection = -1;
        while (menuSelection != 0 && currentUser == null) {
            consoleService.printLoginMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                handleRegister();
            } else if (menuSelection == 2) {
                handleLogin();
            } else if (menuSelection != 0) {
                System.out.println("Invalid Selection");
                consoleService.pause();
            }
        }
    }

    private void handleRegister() {
        System.out.println("Please register a new user account");
        UserCredentials credentials = consoleService.promptForCredentials();
        if (authenticationService.register(credentials)) {
            System.out.println("Registration successful. You can now login.");
        } else {
            consoleService.printErrorMessage();
        }
    }

    private void handleLogin() {
        UserCredentials credentials = consoleService.promptForCredentials();
        currentUser = authenticationService.login(credentials);
        userService = new UserService(currentUser);
        accountService = new AccountService(currentUser);
        transferService = new TransferService(currentUser);
        if (currentUser == null) {
            consoleService.printErrorMessage();
        }
    }

    private void mainMenu() {
        int menuSelection = -1;
        while (menuSelection != 0) {
            consoleService.printMainMenu();

            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                viewCurrentBalance();
            } else if (menuSelection == 2) {
                viewTransferHistory();
            } else if (menuSelection == 3) {
                viewPendingRequests();
                continue;
            } else if (menuSelection == 4) {
                sendBucks();
                continue;
            } else if (menuSelection == 5) {
                requestBucks();
                continue;
            } else if (menuSelection == 0) {
                continue;
            } else {
                System.out.println("Invalid Selection");
            }

            consoleService.pause();
        }
    }

    private void viewCurrentBalance() {
        // TODO Auto-generated method stub
        System.out.println("Your current account balance is: $" +
                accountService.findAccountByUserId(currentUser.getUser().getId()).getBalance());
    }

    private void viewTransferHistory() {
        // TODO Auto-generated method stub
        User user = userService.getUserByUserName();

        Transfer[] userTransfers = transferService.getTransfersForUser(user.getId());

        System.out.println("-------------------------------------------\n" +
                "Transfers\n" +
                "ID          From/To                 Amount\n" +
                "-------------------------------------------");

        for(Transfer transfer : userTransfers) {
            System.out.print(transfer.getId() + " ");
            Account account;

            if(transfer.getTransferType().equals("REQUEST")) {
                int accountFromId = transfer.getAccountTo();
                account = accountService.findAccountById(accountFromId);
            } else {
                int accountToId = transfer.getAccountTo();
                account = accountService.findAccountById(accountToId);
            }

            User userFromTo = userService.findUserbyUserId(account.getUserId());
            System.out.print(String.format("%6s", ""));
            System.out.print(transfer.getTransferType().equals("REQUEST") ? " From: " : " To: " );
            System.out.print(userFromTo.getUsername() + " ");
            System.out.print(String.format("%8s", ""));
            System.out.print("$ " + transfer.getAmount());
            System.out.println();
        }
        System.out.println();
        String decision = consoleService.promptForString("Retrieve details for a transfer (Y)es or (N)o: ").toLowerCase();

        if(decision.equals("y") || decision.equals(("yes"))) {
            viewTransferDetails();
        }

    }

    private void viewTransferDetails() {
        System.out.println();
        int transferId = consoleService.promptForInt("Enter transfer Id: ");
        System.out.println();
        Transfer transferDetails = transferService.getTransferDetails(transferId);
        System.out.println("Transfer Details:");
        System.out.println(transferDetails);
    }

    private void viewPendingRequests() {
        // TODO Auto-generated method stub
        System.out.println("-------------------------------------------\n" +
                "Pending Transfers\n" +
                "ID          To                     Amount\n" +
                "-------------------------------------------");

        Transfer[] transfers = transferService.getTransfersForUser(currentUser.getUser().getId());

        for(Transfer transfer : transfers) {
            if(transfer.getTransferStatus().equals("PENDING")) {
                System.out.print(transfer.getId());
                System.out.print(String.format("%8s", ""));
                User user = userService.findUserbyUserId(accountService.findAccountById(transfer.getAccountTo()).getUserId());
                System.out.print(user.getUsername());
                System.out.print(String.format("%13s", ""));
                System.out.print("$ " + transfer.getAmount());
                System.out.println();
            }
        }

        System.out.println("---------");
        System.out.print("Please enter transfer ID to approve/reject (0 to cancel): ");

        Scanner scanner = new Scanner(System.in);
        int transferId = scanner.nextInt();

        if(transferId == 0) {
            return;
        }

        System.out.println("1: Approve\n" +
                "2: Reject\n" +
                "0: Don't approve or reject\n" +
                "---------\n");
        System.out.print("Please choose an option: ");

        Transfer transfer = transferService.getTransferDetails(transferId);
        int option = scanner.nextInt();

        if(option == 1) {
            if(transfer.getAmount().compareTo(accountService.getBalance(currentUser.getUser().getId())) >= 0) {
                System.out.println(transfer.getAmount().compareTo(accountService.getBalance(currentUser.getUser().getId())));
                System.out.println("Cannot transfer amount greater than balance.");
                transferService.updateRejectTransfer(transferId);
            } else {
                transferService.updateAcceptTransfer(transferId);
            }
        } else if(option == 2) {
            transferService.updateRejectTransfer(transferId);
        }
    }

    private void sendBucks() {
        // TODO Auto-generated method stub
        consoleService.printUsers(userService, currentUser);

        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter ID of user you are sending to (0 to cancel): ");
        int userId = scanner.nextInt();

        if(userId == 0) {
            return;
        }

        if(userId == currentUser.getUser().getId()) {
            System.out.println("Cannot send funds to the same account. Please enter a different user id.");
            System.out.println();
            System.out.print("Enter ID of user you are sending to (0 to cancel): ");
            userId = scanner.nextInt();
        }

        Account accountFrom = accountService.findAccountByUserId(currentUser.getUser().getId());
        Account accountTo = accountService.findAccountByUserId(userId);

        System.out.print("Enter amount: ");
        Double amount = scanner.nextDouble();

        while(amount <= 0 || amount > accountService.getBalance(currentUser.getUser().getId()).doubleValue()) {
            if(amount <= 0) {
                System.out.println();
                System.out.println("Cannot send an amount of 0 or negative value. Please try a different amount");
                System.out.println();
            }

            if(amount > accountService.getBalance(currentUser.getUser().getId()).doubleValue()) {
                System.out.println();
                viewCurrentBalance();
                System.out.println("Please try sending a lesser amount.");
                System.out.println();
            }

            System.out.print("Enter amount: ");
            amount = scanner.nextDouble();
        }

        transferService.send(new TransferDto(accountFrom.getId(), accountTo.getId(), new BigDecimal(amount)));
    }

    private void requestBucks() {
        // TODO Auto-generated method stub
        consoleService.printUsers(userService, currentUser);

        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter ID of user you are requesting from (0 to cancel): ");
        int userId = scanner.nextInt();

        if(userId == 0) {
            return;
        }

        if(userId == currentUser.getUser().getId()) {
            System.out.println("Cannot request funds from the same account. Please enter a different user id.");
            System.out.println();
            System.out.print("Enter ID of user you are requesting from (0 to cancel): ");
            userId = scanner.nextInt();
        }

        System.out.print("Enter amount: ");
        Double amount = scanner.nextDouble();

        while(amount <= 0) {
            System.out.println();
            System.out.println("Cannot request an amount of 0 or negative value. Please try a different amount");
            System.out.print("Enter amount: ");
            amount = scanner.nextDouble();
        }

        Account accountTo = accountService.findAccountByUserId(currentUser.getUser().getId());
        Account accountFrom = accountService.findAccountByUserId(userId);

        transferService.request(new TransferDto(accountFrom.getId(), accountTo.getId(), new BigDecimal(amount)));

    }

}