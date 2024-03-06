package com.p6.payMyBuddy.integrationTests;

import com.p6.payMyBuddy.model.User;
import com.p6.payMyBuddy.services.UserService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class LoginControllerIT {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    UserService userService;
    private static Logger logger = LoggerFactory.getLogger(LoginControllerIT.class);


    @Test
    public void testGetLoginPage() throws Exception {
        MockHttpSession session = new MockHttpSession();
        Cookie mockCookie = Mockito.mock(Cookie.class);
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/login")
                .session(session)
                .cookie(mockCookie);
        this.mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect((ResultMatcher) view().name("login.html"))
                .andExpect(content().string(containsString("Login")));
    }

    @Test
    public void testPostLoginError() throws Exception {
        mockMvc.perform(post("/login").param("email", "test@test.com").param("password", "wrongpassword"))
                .andExpect(status().isOk())
                .andExpect((ResultMatcher) view().name("login.html"))
                .andExpect(content().string(containsString("Wrong Email or Password")));
    }

    @Test
    public void testPostLoginSuccess() throws Exception {
        User user = new User();
        user.setEmail("test@test.com");
        user.setPassword("testtest");
        user.setLastName("Doe");
        user.setFirstName("John");
        userService.saveUser(user);
        mockMvc.perform(post("/login").param("email", "test@test.com").param("password", "testtest"))
                .andExpect(status().is3xxRedirection())
                .andExpect((ResultMatcher) view().name("redirect:home"));
        mockMvc.perform(post("/logout"));
        userService.removeUser(user);
    }

    @Test
    public void testPostLoginWithRemember() throws Exception {
        User user = new User();
        user.setEmail("test@test.com");
        user.setPassword("testtest");
        user.setLastName("Doe");
        user.setFirstName("John");
        userService.saveUser(user);
        mockMvc.perform(post("/login").param("email", "test@test.com").param("password", "testtest").param("remember", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect((ResultMatcher) view().name("redirect:home"))
                .andExpect(cookie().exists("email"))
                .andExpect(cookie().exists("password"));
        mockMvc.perform(post("/logout"))
                .andExpect(cookie().doesNotExist("email"))
                .andExpect(cookie().doesNotExist("password"));
        userService.removeUser(user);
    }

    @Test
    public void testGetLoginWithCookies() throws Exception {
        MockHttpSession session = new MockHttpSession();
        User user = new User();
        user.setEmail("test@test.com");
        user.setPassword("testtest");
        user.setLastName("Doe");
        user.setFirstName("John");
        userService.saveUser(user);
        Cookie emailCookie = new Cookie("email", "test@test.com");
        Cookie passwordCookie = new Cookie("password", user.getPassword()); //PasswordCookie is Hashed
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/login")
                .session(session)
                .cookie(emailCookie, passwordCookie);
        this.mockMvc.perform(builder)
                .andExpect(status().is3xxRedirection())
                .andExpect((ResultMatcher) view().name("redirect:home"));
        userService.removeUser(user);
    }

    @Test
    public void testGetHome() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", new User());
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/home")
                .session(session);
        this.mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect((ResultMatcher) view().name("home.html"))
                .andExpect(content().string(containsString("<a class=\"logout\" href=\"/logout\"> Log off </a>")));
    }

    @Test
    public void testGetLogout() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", new User());
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/logout")
                .session(session);
        this.mockMvc.perform(builder)
                .andExpect(status().is3xxRedirection())
                .andExpect((ResultMatcher) view().name("redirect:login"));
        Assertions.assertThrows(IllegalStateException.class, () -> {
            session.getAttribute("user");
        }); //Session is invalided so .getAttributes throws exception
    }

    @Test
    public void testGetRegister() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect((ResultMatcher) view().name("register.html"))
                .andExpect(content().string(containsString("Register")))
                .andExpect(content().string(containsString("Confirm password")));
    }

    @Test
    public void testPostRegisterSuccess() throws Exception {
        mockMvc.perform(post("/register")
                        .param("email", "newemail@test.com")
                        .param("firstName", "newfirstname")
                        .param("lastName", "newlastname")
                        .param("password", "newpassword")
                        .param("confirm", "newpassword"))
                .andExpect(status().is3xxRedirection())
                .andExpect((ResultMatcher) view().name("redirect:home"));
        Assertions.assertTrue(userService.existsInDB("newemail@test.com"));
        userService.removeUser(userService.getUser("newemail@test.com"));
    }

    @Test
    public void testPostRegisterEmailError() throws Exception {
        User user = new User();
        user.setEmail("test@test.com");
        user.setPassword("testtest");
        user.setLastName("Doe");
        user.setFirstName("John");
        userService.saveUser(user);
        mockMvc.perform(post("/register")
                        .param("email", "test@test.com") //Email already taken
                        .param("firstName", "newfirstname")
                        .param("lastName", "newlastname")
                        .param("password", "newpassword")
                        .param("confirm", "newpassword"))
                .andExpect(status().isOk())
                .andExpect((ResultMatcher) view().name("register"))
                .andExpect(content().string(containsString("Email already taken. Use another or go to Login page.")));
        userService.removeUser(user);
    }

    @Test
    public void testPostRegisterPasswordError() throws Exception {
        mockMvc.perform(post("/register")
                        .param("email", "newemail@test.com")
                        .param("firstName", "newfirstname")
                        .param("lastName", "newlastname")
                        .param("password", "abc") //too short
                        .param("confirm", "abc"))
                .andExpect(status().isOk())
                .andExpect((ResultMatcher) view().name("register"))
                .andExpect(content().string(containsString("Password must have at least 6 characters.")));
    }

    @Test
    public void testPostRegisterConfirmError() throws Exception {
        mockMvc.perform(post("/register")
                        .param("email", "newemail@test.com")
                        .param("firstName", "newfirstname")
                        .param("lastName", "newlastname")
                        .param("password", "newpassword")
                        .param("confirm", "anotherpassword"))
                .andExpect(status().isOk())
                .andExpect((ResultMatcher) view().name("register"))
                .andExpect(content().string(containsString("Your password and confirmation doesn't match.")));
    }

}
