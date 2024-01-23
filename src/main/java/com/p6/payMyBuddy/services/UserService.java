package com.p6.payMyBuddy.services;

import com.p6.payMyBuddy.model.User;
import com.p6.payMyBuddy.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

@Service
public class UserService {

    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }
    @Transactional
    public User getUser(String email) {
        if (existsInDB(email)) {
            Optional<User> optionalUser = userRepository.findById(email);
            return optionalUser.get();
        } else return null;
    }
    @Transactional
    public Boolean existsInDB(String email){
        return userRepository.existsById(email);
    }

    @Transactional
    public void saveUser(User user){
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }
    public Boolean checkPassword(String input, String dataBase){
        return bCryptPasswordEncoder.matches(input, dataBase);
    }
}
