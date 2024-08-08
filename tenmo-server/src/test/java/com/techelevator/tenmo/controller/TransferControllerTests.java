package com.techelevator.tenmo.controller;


import com.techelevator.tenmo.model.*;
import com.techelevator.tenmo.services.AccountService;
import com.techelevator.tenmo.services.TransferService;
import com.techelevator.tenmo.services.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureJsonTesters
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TransferControllerTests {

    @LocalServerPort
    private int port;

    private URL base;

    private Account account;
    private Account accountTwo;
    private BigDecimal balance;
    private BigDecimal balanceTwo;

    private User userOne;

    private User userTwo;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    @MockBean
    private TransferService transferService;
    @MockBean
    private UserService userService;
    @MockBean
    private AccountService accountService;

    @Autowired
    private JacksonTester<TransferDto> jsonTransferDto;

    @Autowired
    private JacksonTester<Transfer> jsonTransfer;

    @Before
    public void setup() throws MalformedURLException {
        this.base = new URL("http://localhost:" + port + "/tenmo/transfers");

        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        userOne = new User(1, "test user", "password", "USER");
        userTwo = new User(2, "test user1", "password", "USER");

        balance = new BigDecimal("1000.00");
        balanceTwo = new BigDecimal("1000.00");
        account = new Account();
        account.setId(1000);
        account.setUserId(1);
        account.setBalance(balance);

        accountTwo = new Account();
        accountTwo.setId(1001);
        accountTwo.setUserId(2);
        accountTwo.setBalance(balanceTwo);
    }

    @Test
    @WithMockUser(username = "test user", password = "password", roles = "USER")
    public void listTransfers_Should_Return_ListOFTransfers() throws Exception {
        List<Transfer> transfers = new ArrayList<>();
        transfers.add(new Transfer(TransferType.SEND, TransferStatus.APPROVED,
                1000, 1001, new BigDecimal(10.00)));
        transfers.add(new Transfer(TransferType.SEND, TransferStatus.APPROVED,
                1001, 1000, new BigDecimal(100.00)));
        transfers.add(new Transfer(TransferType.REQUEST, TransferStatus.PENDING,
                1000, 1001, new BigDecimal(50.00)));

        when(transferService.getTransfersByUserId(1000)).thenReturn(transfers);

        mvc.perform(get(base.toString() + "/1000")).andExpect(status().isOk()).andDo(print());
    }

    @Test
    @WithMockUser(username = "test user", password = "password", roles = "USER")
    public void should_Send_Transfer() throws Exception {
        TransferDto transferDto = new TransferDto();
        transferDto.setAccountFromId(1001);
        transferDto.setAccountToId(1000);
        transferDto.setAmount(new BigDecimal(100.00));

        when(userService.getUserByUsername(anyString())).thenReturn(userOne);

        ArgumentCaptor<Integer> arg1 = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<BigDecimal> arg2 = ArgumentCaptor.forClass(BigDecimal.class);

        doNothing().when(accountService).withdraw(arg1.capture(), arg2.capture());

        accountService.withdraw(1001, new BigDecimal(100.00));

        account.setBalance(account.getBalance().subtract(arg2.getValue()));

        doNothing().when(accountService).deposit(arg1.capture(), arg2.capture());

        accountService.deposit(1000, new BigDecimal(100.00));

        accountTwo.setBalance(accountTwo.getBalance().add(arg2.getValue()));

        when(accountService.findAccountById(1000)).thenReturn(account);
        when(accountService.findAccountById(1001)).thenReturn(accountTwo);

        Transfer newTransfer = new
                Transfer(TransferType.SEND, TransferStatus.APPROVED, 1001, 1000, new BigDecimal(100.00));

        when(transferService.initiateSendTransfer(1001, 1000, new BigDecimal(100.00)))
                .thenReturn(newTransfer);

        MockHttpServletResponse response = mvc.perform(
                post(base.toString() + "/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonTransferDto.write(transferDto).getJson())
        ).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.getContentAsString()).isEqualTo(jsonTransfer.write(newTransfer).getJson());
        assertThat(accountService.findAccountById(1000).getBalance().equals(new BigDecimal(900.00)));
        assertThat(accountService.findAccountById(1001).getBalance().equals(new BigDecimal(1100.00)));
    }

    @Test
    @WithMockUser(username = "test user", password = "password", roles = "USER")
    public void should_Request_Transfer() throws Exception {
        TransferDto transferDto = new TransferDto();
        transferDto.setAccountFromId(1001);
        transferDto.setAccountToId(1000);
        transferDto.setAmount(new BigDecimal(100.00));

        when(userService.getUserByUsername(anyString())).thenReturn(userOne);

        Transfer newTransfer = new
                Transfer(TransferType.REQUEST, TransferStatus.PENDING, 1001, 1000, new BigDecimal(100.00));

        when(transferService.initiateRequestTransfer(1001, 1000, new BigDecimal(100.00)))
                .thenReturn(newTransfer);

        MockHttpServletResponse response = mvc.perform(
                post(base.toString() + "/request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonTransferDto.write(transferDto).getJson())
        ).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.getContentAsString()).isEqualTo(jsonTransfer.write(newTransfer).getJson());
    }

    @Test
    @WithMockUser(username = "test user", password = "password", roles = "USER")
    public void should_Approve_Transfer() throws Exception {
        when(userService.getUserByUsername(anyString())).thenReturn(userOne);

        ArgumentCaptor<Integer> arg1 = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<BigDecimal> arg2 = ArgumentCaptor.forClass(BigDecimal.class);

        doNothing().when(accountService).withdraw(arg1.capture(), arg2.capture());

        accountService.withdraw(1001, new BigDecimal(100.00));

        account.setBalance(account.getBalance().subtract(arg2.getValue()));

        doNothing().when(accountService).deposit(arg1.capture(), arg2.capture());

        accountService.deposit(1000, new BigDecimal(100.00));

        accountTwo.setBalance(accountTwo.getBalance().add(arg2.getValue()));

        when(accountService.findAccountById(1000)).thenReturn(account);
        when(accountService.findAccountById(1001)).thenReturn(accountTwo);


        Transfer newTransfer = new
                Transfer(TransferType.REQUEST, TransferStatus.APPROVED, 1001, 1000, new BigDecimal(100.00));

        when(transferService.acceptRequestTransfer(newTransfer.getId())).thenReturn(newTransfer);

        MockHttpServletResponse response = mvc.perform(
                put(base.toString() + "/request/" + newTransfer.getId() + "/accept")
        ).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.ACCEPTED.value());
        assertThat(response.getContentAsString()).isEqualTo(jsonTransfer.write(newTransfer).getJson());
        assertThat(accountService.findAccountById(1000).getBalance().equals(new BigDecimal(900.00)));
        assertThat(accountService.findAccountById(1001).getBalance().equals(new BigDecimal(1100.00)));
    }

    @Test
    @WithMockUser(username = "test user", password = "password", roles = "USER")
    public void should_Reject_Transfer() throws Exception {
        when(userService.getUserByUsername(anyString())).thenReturn(userOne);

        when(accountService.findAccountById(1000)).thenReturn(account);
        when(accountService.findAccountById(1001)).thenReturn(accountTwo);


        Transfer newTransfer = new
                Transfer(TransferType.REQUEST, TransferStatus.REJECTED, 1001, 1000, new BigDecimal(100.00));

        when(transferService.rejectRequestTransfer(newTransfer.getId())).thenReturn(newTransfer);

        MockHttpServletResponse response = mvc.perform(
                put(base.toString() + "/request/" + newTransfer.getId() + "/reject")
        ).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.ACCEPTED.value());
        assertThat(response.getContentAsString()).isEqualTo(jsonTransfer.write(newTransfer).getJson());
        assertThat(accountService.findAccountById(1000).getBalance().equals(new BigDecimal(1000.00)));
        assertThat(accountService.findAccountById(1001).getBalance().equals(new BigDecimal(1000.00)));
    }

    @Test
    @WithMockUser(username = "test user", password = "password", roles = "USER")
    public void should_Return_Transfer() throws Exception {
        when(userService.getUserByUsername(anyString())).thenReturn(userOne);

        Transfer newTransfer = new
                Transfer(TransferType.SEND, TransferStatus.APPROVED, 1001, 1000, new BigDecimal(100.00));

        when(transferService.getTransferById(newTransfer.getId()))
                .thenReturn(newTransfer);

        MockHttpServletResponse response = mvc.perform(
                get(base.toString() + "/" + newTransfer.getId() + "/details")
        ).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(jsonTransfer.write(newTransfer).getJson());
    }
}
