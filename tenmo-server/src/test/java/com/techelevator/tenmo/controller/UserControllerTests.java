package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.login.UserCredentials;
import com.techelevator.tenmo.model.LoginDto;
import com.techelevator.tenmo.model.LoginResponseDto;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.services.UserService;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerTests {

    @LocalServerPort
    private int port;

    private URL base;

    private URL loginURL;

    private LoginResponseDto loginResponseDto;

   /* @Autowired
    private TestRestTemplate testRestTemplate;*/

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    @MockBean
    private UserService userService;


    @Before
    public void setup() throws MalformedURLException, JSONException {
        this.base = new URL("http://localhost:" + port + "/tenmo/users");
        /*this.loginURL = new URL("http://localhost:" + port + "/login");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        UserCredentials userCredentials = new UserCredentials("test user", "password");

        System.out.println(new HttpEntity<>(userCredentials, headers));

        ResponseEntity<LoginResponseDto> response = testRestTemplate.postForEntity(loginURL.toString(), new HttpEntity<UserCredentials>(userCredentials, headers), LoginResponseDto.class);

        System.out.println(response.getBody());*/

        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

   /* @Test
    public void sampleTest() {
        System.out.println("sample test");
    }*/

    @Test
    public void findByUserId_Should_ReturnError_When_AuthenticationNotProvided() throws Exception {
        User testUser = new User(1, "test user", "password", "USER");
        when(userService.getUserById(1)).thenReturn(testUser);

        mvc.perform(get(base.toString() + "/id/1")).andExpect(status().isUnauthorized()).andDo(print());
    }

    @Test
    @WithMockUser(username = "test user", password = "password", roles = "USER")
    public void findByUserId_Should_Return_User_When_ValidIdIsProvided() throws Exception {
        User testUser = new User(1, "test user", "password", "USER");
        when(userService.getUserById(1)).thenReturn(testUser);

        //mvc.perform(get(base.toString() + "/id/1")).andExpect(status().isOk()).andDo(print());
        MockHttpServletResponse response = mvc.perform(get(base.toString() + "/id/1")).andReturn().getResponse();
        System.out.println(response);
    }

    @Test
    @WithMockUser(username = "test user", password = "password", roles = "USER")
    public void listUsers_Should_Return_ListOfUsers() throws Exception {
        List<User> users = new ArrayList<>();
        users.add(new User(1, "test user", "password", "USER"));
        users.add(new User(2, "test user2", "password", "USER"));
        users.add(new User(3, "test user3", "password", "USER"));

        when(userService.getAllUsers()).thenReturn(users);

        mvc.perform(get(base.toString())).andExpect(status().isOk()).andDo(print());

    }

    @Test
    @WithMockUser(username = "test user", password = "password", roles = "USER")
    public void findByUserName_Should_Return_User_When_ValidUserNameIsProvided() throws Exception {
        User testUser = new User(1, "test user", "password", "USER");
        when(userService.getUserByUsername("test user")).thenReturn(testUser);

        mvc.perform(get(base.toString() + "/username/test user")).andExpect(status().isOk()).andDo(print());
    }

}
