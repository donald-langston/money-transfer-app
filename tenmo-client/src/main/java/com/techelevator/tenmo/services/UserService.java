package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.User;
import com.techelevator.util.BasicLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

public class UserService {

    public static final String API_BASE_URL = "http://localhost:8080/tenmo/users/";
    private RestTemplate restTemplate = new RestTemplate();

    private AuthenticatedUser currentUser;

    public UserService(AuthenticatedUser currentUser) {
        this.currentUser = currentUser;
    }
    private String authToken = null;

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public User[] getAllUsers() {
        User[] users = null;
        try {
            ResponseEntity<User[]> response =
                    restTemplate.exchange(API_BASE_URL, HttpMethod.GET, makeAuthEntity(), User[].class);
            users = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return users;
    }
    public User findUserbyUserId(int id) {
        User user = null;

        try {
            ResponseEntity<User> response = restTemplate.exchange(API_BASE_URL + "id/" + id,
                    HttpMethod.GET, makeAuthEntity(), User.class);
            user = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }

        return user;
    }

    public User findUserbyUsername(String username) {
        User user = null;

        try {
            ResponseEntity<User> response = restTemplate.exchange(API_BASE_URL + "username/" + username,
                    HttpMethod.GET, makeAuthEntity(), User.class);
            user = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }

        return user;
    }

    public User getUserByUserName() {
        User user = null;

        try {
            ResponseEntity<User> response = restTemplate.exchange(API_BASE_URL + "username/" + currentUser.getUser().getUsername(),
                    HttpMethod.GET, makeAuthEntity(), User.class);
            user = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }

        return user;
    }

    private HttpEntity<Void> makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(currentUser.getToken());
        return new HttpEntity<>(headers);
    }
}
