package com.p6.payMyBuddy.services;

import com.p6.payMyBuddy.model.DTO.Connection;
import com.p6.payMyBuddy.model.User;
import com.p6.payMyBuddy.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.List;
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
    @Transactional
    public void updateUser(User user){
        userRepository.save(user);
    }
    public Boolean checkPassword(String input, String dataBase){
        return bCryptPasswordEncoder.matches(input, dataBase);
    }

    public ArrayList<String> userToEmailList(ArrayList<User> users){
        ArrayList<String> usersEmail = new ArrayList<>();
        for(int i=0; i<users.size(); i++){
            usersEmail.add(users.get(i).getEmail());
        }
        return usersEmail;
    }

}
