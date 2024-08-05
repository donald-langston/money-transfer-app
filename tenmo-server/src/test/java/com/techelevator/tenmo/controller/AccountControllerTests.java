package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.controller.AccountController;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.services.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import javax.security.auth.login.AccountNotFoundException;
import java.math.BigDecimal;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class AccountControllerTests {
    @Mock
    private AccountService accountService;
    @InjectMocks
    private AccountController accountController;
    private Account account;
    private Account accountTwo;
    private BigDecimal balance;
    private BigDecimal balanceTwo;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        balance = new BigDecimal("1000.00");
        balanceTwo = new BigDecimal("2000.00");
        account = new Account();
        account.setId(1);
        account.setUserId(1);
        account.setBalance(balance);

        accountTwo = new Account();
        accountTwo.setId(2);
        accountTwo.setUserId(2);
        accountTwo.setBalance(balanceTwo);
    }

    @Test
    public void test_findAccountById_withValidId_is_successful() {
        when(accountService.findAccountById(2)).thenReturn(accountTwo);
        ResponseEntity<Account> response = accountController.findAccountById(2);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(accountTwo, response.getBody());
    }

    @Test
    public void test_findAccountByUserId_withValidId_is_successful() {
        when(accountService.findAccountByUserId(2)).thenReturn(accountTwo);
        ResponseEntity<Account> response = accountController.findAccountByUserId(2);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(accountTwo, response.getBody());
    }

    @Test
    public void test_getAccountBalance_withValidUserId_is_successful() throws Exception {
        when(accountService.retrieveAccountBalance(2)).thenReturn(balance);
        ResponseEntity<BigDecimal> response = accountController.getAccountBalance(2);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(balance, response.getBody());
    }

    @Test
    public void test_findAccountById_withInvalidId_return_correct_error_message() {
        when(accountService.findAccountById(-1)).thenThrow(new RuntimeException("Account not found with provided Id"));
        RuntimeException exception = assertThrows(RuntimeException.class, () -> accountController.findAccountById(-1));
        assertEquals("Account not found with provided Id", exception.getMessage());
    }

    @Test
    public void test_findAccountByUserId_withInvalidId_returns_correct_error_message() {
        when(accountService.findAccountByUserId(-1)).thenThrow(new RuntimeException("Account not found with associated Id"));
        RuntimeException exception = assertThrows(RuntimeException.class, () -> accountController.findAccountByUserId(-1));
        assertEquals("Account not found with associated Id", exception.getMessage());
    }

    @Test
    public void test_retrieveAccountBalance_withInvalidUserId_returns_correct_error_message() throws AccountNotFoundException {
        when(accountService.retrieveAccountBalance(-1)).thenThrow(new AccountNotFoundException("Not able to view balance of requested account"));
        AccountNotFoundException exception = assertThrows(AccountNotFoundException.class, () -> accountController.getAccountBalance(-1));
        assertEquals("Not able to view balance of requested account", exception.getMessage());
    }

    @Test
    public void test_updateAccountBalance_functionality() {
        account.setBalance(new BigDecimal("1500.00"));
        assertEquals(new BigDecimal("1500.00"), account.getBalance());
    }

    @Test
    public void test_accounts_are_notNull() {
        assertNotNull(account);
        assertNotNull(accountTwo);
    }

    @Test
    public void test_accounts_instances_are_notEqual() {
        assertNotEquals(account, accountTwo);
    }
}
