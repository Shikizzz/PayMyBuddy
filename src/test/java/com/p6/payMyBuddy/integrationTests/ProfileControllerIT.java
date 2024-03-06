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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class ProfileControllerIT {
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
        userService.saveUser(user);
    }

    @AfterEach
    void teardown() {
        userService.removeUser(userService.getUser("test@test.com"));
    }

    @Test
    public void testGetProfile() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", userService.getUser("test@test.com"));
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/profile")
                .session(session);
        this.mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect((ResultMatcher) view().name("profile.html"))
                .andExpect(content().string(containsString("John"))) //First and Last name
                .andExpect(content().string(containsString("Your balance")));
    }

    @Test
    public void testGetEditProfile() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", userService.getUser("test@test.com"));
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/editProfile")
                .session(session);
        this.mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect((ResultMatcher) view().name("editProfile.html"))
                .andExpect(content().string(containsString("Modify Profile")))
                .andExpect(content().string(containsString("test@test.com")));
    }

    @Test
    public void testGetEditPassword() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", userService.getUser("test@test.com"));
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/editPassword")
                .session(session);
        this.mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect((ResultMatcher) view().name("editPassword.html"))
                .andExpect(content().string(containsString("Modify Password")))
                .andExpect(content().string(containsString("Current Password")));
    }

    @Test
    public void testPostEditPasswordSuccess() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", userService.getUser("test@test.com"));
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/editPassword")
                .session(session)
                .param("oldPassword", "testtest")
                .param("newPassword", "otherpassword")
                .param("confirmNewPassword", "otherpassword");
        this.mockMvc.perform(builder)
                .andExpect(status().is3xxRedirection())
                .andExpect((ResultMatcher) view().name("redirect:logout"));
        Assertions.assertTrue(userService.checkPassword("otherpassword", userService.getUser("test@test.com").getPassword()));
    }

    @Test
    public void testPostEditPasswordWrongPasswordError() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", userService.getUser("test@test.com"));
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/editPassword")
                .session(session)
                .param("oldPassword", "wrongPassword")
                .param("newPassword", "otherpassword")
                .param("confirmNewPassword", "otherpassword");
        this.mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect((ResultMatcher) view().name("editPassword.html"))
                .andExpect(content().string(containsString("Incorrect Password")));
    }

    @Test
    public void testPostEditPasswordNewPasswordLengthError() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", userService.getUser("test@test.com"));
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/editPassword")
                .session(session)
                .param("oldPassword", "testtest")
                .param("newPassword", "abc")
                .param("confirmNewPassword", "abc");
        this.mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect((ResultMatcher) view().name("editPassword.html"))
                .andExpect(content().string(containsString("New passwords must be at least 6 characters")));
    }

    @Test
    public void testPostEditPasswordConfirmPasswordError() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", userService.getUser("test@test.com"));
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/editPassword")
                .session(session)
                .param("oldPassword", "testtest")
                .param("newPassword", "newPassword")
                .param("confirmNewPassword", "notMatchingPassword");
        this.mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect((ResultMatcher) view().name("editPassword.html"))
                .andExpect(content().string(containsString("New passwords don't match")));
    }

    @Test
    public void testPostEditProfileSuccess() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", userService.getUser("test@test.com"));
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/editProfile")
                .session(session)
                .param("email", "test@test.com") //we don't change email, to delete user automatically
                .param("firstName", "otherFirstName")
                .param("lastName", "otherLastName")
                .param("password", "testtest");
        this.mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect((ResultMatcher) view().name("home.html"));
        Assertions.assertEquals("otherFirstName", userService.getUser("test@test.com").getFirstName());
        Assertions.assertEquals("otherLastName", userService.getUser("test@test.com").getLastName());
    }

    @Test
    public void testPostEditProfileError() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", userService.getUser("test@test.com"));
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/editProfile")
                .session(session)
                .param("email", "test@test.com")
                .param("firstName", "otherFirstName")
                .param("lastName", "otherLastName")
                .param("password", "wrongPassword");
        this.mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect((ResultMatcher) view().name("editProfile.html"))
                .andExpect(content().string(containsString("Incorrect Password")));
    }

    @Test
    public void testPostAddBalance() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", userService.getUser("test@test.com"));
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/profile-addBalance")
                .session(session)
                .param("amount", "19.99");
        this.mockMvc.perform(builder)
                .andExpect(status().is3xxRedirection())
                .andExpect((ResultMatcher) view().name("redirect:profile"));
        Assertions.assertEquals(19.99, userService.getUser("test@test.com").getBalance());
    }

    @Test
    public void testPostAddBalanceWrongAmount() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", userService.getUser("test@test.com"));
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/profile-addBalance")
                .session(session)
                .param("amount", "-1");
        this.mockMvc.perform(builder)
                .andExpect(status().is3xxRedirection())
                .andExpect((ResultMatcher) view().name("redirect:profile"));
        Assertions.assertEquals(0, userService.getUser("test@test.com").getBalance());
    }

    @Test
    public void testPostRetrieveBalance() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", userService.getUser("test@test.com"));
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/profile-addBalance")
                .session(session)
                .param("amount", "20");
        this.mockMvc.perform(builder);                 //Added Balance
        MockHttpServletRequestBuilder otherBuilder = MockMvcRequestBuilders.post("/profile-retrieveBalance")
                .session(session)
                .param("amount", "10");
        this.mockMvc.perform(otherBuilder)                  //Retrieving Balance
                .andExpect(status().is3xxRedirection())
                .andExpect((ResultMatcher) view().name("redirect:profile"));
        Assertions.assertEquals(10, userService.getUser("test@test.com").getBalance());
    }

    @Test
    public void WrongAmount() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", userService.getUser("test@test.com"));
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/profile-retrieveBalance")
                .session(session)
                .param("amount", "10");
        this.mockMvc.perform(builder)
                .andExpect(status().is3xxRedirection())
                .andExpect((ResultMatcher) view().name("redirect:profile?error=balanceError"));
        Assertions.assertEquals(0, userService.getUser("test@test.com").getBalance()); //balance didn't change
    }


}
