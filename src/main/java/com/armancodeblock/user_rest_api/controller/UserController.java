package com.armancodeblock.user_rest_api.controller;

import com.armancodeblock.user_rest_api.enity.User;
import com.armancodeblock.user_rest_api.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/users")
public class UserController {
    // diff between @Controller and @RestController
    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        User createdUser = userService.createUser(user);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    //client send a request(get) at path localhost:8080/api/v1/users
    // controller will handle this request and send a response({users,200})
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable Long userId) {
        User user = userService.getUserById(userId);

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUserById(@PathVariable Long userId) {
        userService.deleteUserById(userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

// update api/v1`/users/server/?prefix="A"
    //path variable -> part of the URL , sepcify a resource

    @PutMapping("/{userId}")
    public ResponseEntity<User> updateUser(@PathVariable Long userId, @RequestBody User user) {
        //User updatedUser = userService.updateUser(userId,user);
        return new ResponseEntity<>(userService.updateUser(userId, user), HttpStatus.OK);
    }
// localhost:8080/api/v1/users/search?prefix="A"
    @GetMapping("/search")
    public ResponseEntity<List<User>> getAllUserByNamePrefix(@RequestParam String prefix) {
        return new ResponseEntity<>(userService.getAllUsersByNamePrefix(prefix), HttpStatus.OK);
    }

}




