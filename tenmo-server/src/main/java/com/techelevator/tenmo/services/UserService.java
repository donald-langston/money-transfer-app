package com.techelevator.tenmo.services;

import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.User;
import org.springframework.stereotype.Service;
import javax.security.auth.login.AccountNotFoundException;
import java.util.List;

@Service
public class UserService {
    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public User getUserById(int userId) {
        try {
            User user = userDao.findUserById(userId);
            if (user == null) {
                throw new AccountNotFoundException("User not found with provided Id");
            }
            return user;
        } catch (DaoException e) {
            throw new DaoException("Server error occurred", e);
        } catch (AccountNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public User getUserByUsername(String username) {
        try {
            User user = userDao.findUserByUsername(username);
            if (user == null) {
                throw new AccountNotFoundException("User not found with provided username");
            }
            return user;
        } catch (DaoException e) {
            throw new DaoException("Server error occurred", e);
        } catch (AccountNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public List<User> getAllUsers() {
        try {
            List<User> users = userDao.listUsers();
            if (users == null) {
                throw new AccountNotFoundException("Users not found");
            }
            return users;
        } catch (DaoException e) {
            throw new DaoException("Server error occurred", e);
        } catch (AccountNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
