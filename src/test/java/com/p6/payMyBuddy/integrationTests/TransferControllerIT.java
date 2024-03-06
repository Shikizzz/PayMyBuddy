package com.p6.payMyBuddy.integrationTests;

import com.p6.payMyBuddy.model.User;
import com.p6.payMyBuddy.services.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
@AutoConfigureMockMvc
public class TransferControllerIT {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    UserService userService;

    @BeforeEach
    void init() {
        User user = new User();
        user.setEmail("test@test.com");
        user.setPassword("testtest");
        user.setLastName("Doe");
        user.setFirstName("John");
        user.setBalance(200.0);
        userService.saveUser(user);
    }

    @AfterEach
    void teardown() {
        userService.removeUser(userService.getUser("test@test.com"));
    }

    @Test
    public void testGetTransfer() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", userService.getUser("test@test.com"));
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/transfer")
                .session(session);
        this.mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect((ResultMatcher) view().name("transfer.html"))
                .andExpect(content().string(containsString("Add Connection")))
                .andExpect(content().string(containsString("My Transactions")));
    }

    @Test
    public void testPostTransfer() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", userService.getUser("test@test.com"));
        User friendUser = new User();
        friendUser.setEmail("friend@test.com");
        friendUser.setPassword("testtest");
        friendUser.setLastName("Dobby");
        friendUser.setFirstName("Johnny");
        userService.saveUser(friendUser);
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/transfer")
                .session(session)
                .param("email", "friend@test.com")
                .param("description", "TestDescription")
                .param("amount", "100");
        this.mockMvc.perform(builder)
                .andExpect(status().is3xxRedirection())
                .andExpect((ResultMatcher) view().name("redirect:transfer"));
        Assertions.assertEquals(100, userService.getUser("test@test.com").getBalance());
        Assertions.assertEquals(100 * 0.995, userService.getUser("friend@test.com").getBalance());
    }

    @Test
    public void testPostTransferError() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", userService.getUser("test@test.com"));
        User friendUser = new User();
        friendUser.setEmail("friend@test.com");
        friendUser.setPassword("testtest");
        friendUser.setLastName("Dobby");
        friendUser.setFirstName("Johnny");
        userService.saveUser(friendUser);
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/transfer")
                .session(session)
                .param("email", "friend@test.com")
                .param("description", "TestDescription")
                .param("amount", "999"); //Too much, not enough balance
        this.mockMvc.perform(builder)
                .andExpect(status().is3xxRedirection())
                .andExpect((ResultMatcher) view().name("redirect:transfer?error=balanceError"));
        Assertions.assertEquals(200, userService.getUser("test@test.com").getBalance());
        Assertions.assertEquals(0, userService.getUser("friend@test.com").getBalance());
    }

}
