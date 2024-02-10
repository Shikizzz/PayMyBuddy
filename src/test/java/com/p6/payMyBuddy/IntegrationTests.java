package com.p6.payMyBuddy;

import com.p6.payMyBuddy.model.User;
import com.p6.payMyBuddy.services.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
public class IntegrationTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    UserService userService;

    private static Logger logger = LoggerFactory.getLogger(IntegrationTests.class);
                                                //Check if interceptor is called in tests
    @Test
    public void testLoginPage() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect((ResultMatcher)view().name("login.html"))
                .andExpect(content().string(containsString("Login")));
    }

    @Test
    public void testLoginError() throws Exception {
        mockMvc.perform(post("/login").param("email", "test@test.com").param("password", "wrongpassword"))
                .andExpect(status().isOk())
                .andExpect((ResultMatcher)view().name("login.html"))
                .andExpect(content().string(containsString("Wrong Email or Password")));
    }

    @Test
    public void testLoginSuccess() throws Exception {
        mockMvc.perform(post("/login").param("email", "test@test.com").param("password", "testtest"))
                .andExpect(status().is3xxRedirection())
                .andExpect((ResultMatcher)view().name("redirect:hubPage"));
        mockMvc.perform(post("/logout"));
    }

    @Test
    public void testContact() throws Exception {
        MockHttpSession session = new MockHttpSession();
        User user = userService.getUser("test@test.com");
        session.setAttribute("user", user);
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/contact")
                .session(session);
        this.mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect((ResultMatcher)view().name("contact.html"))
                .andExpect(content().string(containsString("Enter person's Email")));
    }

    @Test
    public void testHubPage() throws Exception {
        MockHttpSession session = new MockHttpSession();
        User user = userService.getUser("test@test.com");
        session.setAttribute("user", user);
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/hubPage")
                .session(session);
        this.mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect((ResultMatcher)view().name("hubPage.html"))
                .andExpect(content().string(containsString("<a class=\"logout\" href=\"/logout\"> Log off </a>")));
    }

    @Test
    public void testLogout() throws Exception {
        MockHttpSession session = new MockHttpSession();
        User user = userService.getUser("test@test.com");
        session.setAttribute("user", user);
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/logout")
                .session(session);
        this.mockMvc.perform(builder)
                .andExpect(status().is3xxRedirection())
                .andExpect((ResultMatcher)view().name("redirect:login"));
        Assertions.assertThrows(IllegalStateException.class, () -> {session.getAttribute("user");}); //Session is invalided so .getAttributes throws exception
    }

    //TODO tests : Profile+editions, Transfer
}
