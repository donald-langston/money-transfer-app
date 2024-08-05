package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.util.BasicLogger;
import org.springframework.http.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

public class AccountService {

    public static final String API_BASE_URL = "http://localhost:8080/tenmo/accounts/";
    private RestTemplate restTemplate = new RestTemplate();


    private String authToken = null;

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    private AuthenticatedUser currentUser;

    public AccountService(AuthenticatedUser currentUser) {
        this.currentUser = currentUser;
    }

    public Account[] getAllAccounts() {
        Account[] accounts = null;
        try {
            ResponseEntity<Account[]> response =
                    restTemplate.exchange(API_BASE_URL, HttpMethod.GET, makeAuthEntity(), Account[].class);
            accounts = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return accounts;
    }

    public Account findAccountById(int id) {
        Account account = null;

        try {
            ResponseEntity<Account> response = restTemplate.exchange(API_BASE_URL + "accountId/" + id,
                    HttpMethod.GET, makeAuthEntity(), Account.class);
            account = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }

        return account;
    }

    public Account findAccountByUserId(int id) {
        Account account = null;

        try {
            ResponseEntity<Account> response = restTemplate.exchange(API_BASE_URL + "userId/" + id,
                    HttpMethod.GET, makeAuthEntity(), Account.class);
            account = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }

        return account;
    }


    public BigDecimal getBalance(int userId) {
        BigDecimal amount = null;

        try {
            ResponseEntity<BigDecimal> response =
                    restTemplate.exchange(API_BASE_URL + "userId/" + userId + "/balance", HttpMethod.GET, makeAuthEntity(), BigDecimal.class);
            amount = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }

        return amount;
    }


    private HttpEntity<Account> makeAuctionEntity(Account account) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(currentUser.getToken());
        return new HttpEntity<>(account, headers);
    }


    private HttpEntity<Void> makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(currentUser.getToken());
        return new HttpEntity<>(headers);
    }
}
