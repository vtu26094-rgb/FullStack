package com.library.userservice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.library.userservice.entity.User;
import com.library.userservice.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService service;

    @PostMapping
    public User addUser(@RequestBody User user) {
        return service.saveUser(user);
    }

    @GetMapping
    public List<User> getUsers() {
        return service.getAllUsers();
    }
}