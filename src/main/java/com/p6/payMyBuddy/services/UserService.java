package com.p6.payMyBuddy.services;

import com.p6.payMyBuddy.model.Transaction;
import com.p6.payMyBuddy.model.User;
import com.p6.payMyBuddy.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

    @Transactional
    public void removeUser(User user){
        userRepository.delete(user);
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

    public void addBalance(User user, double amount){ //For real app, call to bank API here
        user.setBalance(user.getBalance()+amount);
        updateUser(user);
    }

    public void removeBalance(User user, double amount){ //For real app, call to bank API here
        user.setBalance(user.getBalance()-amount);
        updateUser(user);
    }
    @Transactional
    public void sendMoney(User sourceUser, String targetEmail, double amount, String description){
        User targetUser = getUser(targetEmail);
        sourceUser.setBalance(sourceUser.getBalance()-amount);
        targetUser.setBalance(targetUser.getBalance()+(amount*0.995));//For real app, call to bank API here
        //take (0.005)*amount for PayMyBuddy account here, with calls to bank API
        Transaction transaction = new Transaction();
        transaction.setDescription(description);
        transaction.setAmount(amount);
        transaction.setDate(LocalDateTime.now());
        transaction.setSource(sourceUser);
        transaction.setTarget(targetUser);
        sourceUser.addTransaction(transaction);
        updateUser(sourceUser);
        updateUser(targetUser);
    }

}
