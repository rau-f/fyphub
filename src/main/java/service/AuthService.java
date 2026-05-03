package service;

import model.User;
import repository.DataStore;

public class AuthService {

    public static User login(String email, String password) {
        User user = DataStore.getUserByEmail(email);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }
}
