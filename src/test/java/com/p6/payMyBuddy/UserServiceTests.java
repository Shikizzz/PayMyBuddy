package com.p6.payMyBuddy;

import com.p6.payMyBuddy.model.User;
import com.p6.payMyBuddy.repository.UserRepository;
import com.p6.payMyBuddy.services.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.hamcrest.Matchers.any;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {
    @Spy
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Mock
    UserRepository userRepository;
    @InjectMocks
    UserService userService;

    @Test
    void saveUserTest(){
        User testUser = new User();
        testUser.setPassword("test");
        userService.saveUser(testUser);
        Assertions.assertNotEquals("test", testUser.getPassword());
    }


}
