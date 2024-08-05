package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.RegisterUserDto;
import com.techelevator.tenmo.model.User;

import java.util.List;

public interface UserDao {

    List<User> listUsers();

    User findUserById(int id);

    User findUserByUsername(String username);

    User saveUser(RegisterUserDto user);
}
