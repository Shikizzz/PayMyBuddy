package com.p6.payMyBuddy.servicesTests;

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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {
    @Spy
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Mock
    UserRepository userRepository;
    @InjectMocks
    UserService userService;

    @Test
    void saveUserTest() {
        User testUser = new User();
        testUser.setPassword("test");
        userService.saveUser(testUser);
        Assertions.assertNotEquals("test", testUser.getPassword());
    }

    @Test
    void sendMoneyTest() {
        User sourceUser = new User();
        sourceUser.setBalance(1000);
        User targetUser = new User();
        Optional<User> optionalTargetUser = Optional.of(targetUser);
        Mockito.when(userRepository.findById("targetEmail")).thenReturn(optionalTargetUser);
        Mockito.when(userRepository.existsById("targetEmail")).thenReturn(true);
        userService.sendMoney(sourceUser, "targetEmail", 1000, "testDescription");
        Assertions.assertEquals(0, sourceUser.getBalance());
        Assertions.assertEquals(995, targetUser.getBalance());
        Assertions.assertEquals(1, sourceUser.getTransactionsSource().size());
    }
}
