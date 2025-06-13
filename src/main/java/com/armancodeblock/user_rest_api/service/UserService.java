package com.armancodeblock.user_rest_api.service;

import com.armancodeblock.user_rest_api.enity.User;
import com.armancodeblock.user_rest_api.exception.ResourceNotFoundException;
import com.armancodeblock.user_rest_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private  UserRepository userRepository;

    public User createUser(User user){
      return   userRepository.save(user);
    }

    public List<User> getAllUsers(){
     return    userRepository.findAll();
    }


public User getUserById(Long userId){
  Optional<User> opUser =  userRepository.findById(userId);
  if(opUser.isPresent()){
        return opUser.get();
  }  else {
      throw  new ResourceNotFoundException("User not found with userId:"+ userId);
  }
// find All users name start with "A"
}

public void deleteUserById(Long userId){
        userRepository.deleteById(userId);
}

// update set name = "newName", email = "newEmail" where userId = 4;

    public User updateUser(Long userId,User user){
        Optional<User> opUser = userRepository.findById(userId);
        if(opUser.isPresent()){
            opUser.get().setName(user.getName());
            opUser.get().setEmail(user.getEmail());
          return   userRepository.save(opUser.get());
        } else {
            return  userRepository.save(user);
           // throw new RuntimeException("User not found with userId: " + userId);
        }

    }


}
