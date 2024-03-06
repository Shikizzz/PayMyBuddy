package com.p6.payMyBuddy.integrationTests;

import com.p6.payMyBuddy.model.User;
import com.p6.payMyBuddy.services.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class ContactControllerIT {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    UserService userService;

    @Test
    public void testGetContact() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", new User());
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/contact")
                .session(session);
        this.mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect((ResultMatcher) view().name("contact.html"))
                .andExpect(content().string(containsString("Enter person's email")));
    }

    @Test
    public void testPostContact() throws Exception {
        MockHttpSession session = new MockHttpSession();
        User user = new User();
        user.setEmail("test@test.com");
        user.setPassword("testtest");
        user.setLastName("Doe");
        user.setFirstName("John");
        userService.saveUser(user); //setting the user for the test
        User friend = new User();
        friend.setEmail("friend@email.com");
        friend.setPassword("password");
        friend.setLastName("Doe");
        friend.setFirstName("Johnny");
        userService.saveUser(friend);//setting another user to add in contacts for the test
        session.setAttribute("user", user);
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/contact")
                .session(session)
                .param("email", "friend@email.com");
        this.mockMvc.perform(builder)
                .andExpect(status().is3xxRedirection())
                .andExpect((ResultMatcher) view().name("redirect:contact"));
        MockHttpServletRequestBuilder anotherBuilder = MockMvcRequestBuilders.get("/contact")
                .session(session);
        this.mockMvc.perform(anotherBuilder)
                .andExpect(status().isOk())
                .andExpect((ResultMatcher) view().name("contact.html"))
                .andExpect(content().string(containsString("Johnny")));
        Assertions.assertTrue(userService.getUser("test@test.com").getConnections().size() == 1);
        userService.removeUser(user);
        userService.removeUser(friend);
    }

    @Test
    public void testPostContactDoesntExist() throws Exception {
        MockHttpSession session = new MockHttpSession();
        User user = new User();
        user.setEmail("test@test.com");
        user.setPassword("testtest");
        user.setLastName("Doe");
        user.setFirstName("John");
        userService.saveUser(user); //setting the user for the test
        session.setAttribute("user", user);
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/contact")
                .session(session)
                .param("email", "doesnotexist@email.com");
        this.mockMvc.perform(builder)
                .andExpect(status().is3xxRedirection())
                .andExpect((ResultMatcher) view().name("redirect:contact?error=notFoundError"));
        userService.removeUser(user);
    }

    @Test
    public void testPostContactAlreadyFriend() throws Exception {
        MockHttpSession session = new MockHttpSession();
        User user = new User();
        user.setEmail("test@test.com");
        user.setPassword("testtest");
        user.setLastName("Doe");
        user.setFirstName("John");
        userService.saveUser(user); //setting the user for the test
        User friend = new User();
        friend.setEmail("friend@email.com");
        friend.setPassword("password");
        friend.setLastName("Doe");
        friend.setFirstName("Johnny");
        userService.saveUser(friend);//setting another user to add in contacts for the test
        session.setAttribute("user", user);
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/contact")
                .session(session)
                .param("email", "friend@email.com");
        this.mockMvc.perform(builder);
        this.mockMvc.perform(builder)
                .andExpect(status().is3xxRedirection())
                .andExpect((ResultMatcher) view().name("redirect:contact?error=alreadyFriendError"));
        Assertions.assertTrue(userService.getUser("test@test.com").getConnections().size() == 1);
        userService.removeUser(user);
        userService.removeUser(friend);
    }
}
