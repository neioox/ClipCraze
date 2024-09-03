package de.neiox.services;
import de.neiox.models.User;

import java.util.ArrayList;
import java.util.List;

public class UserService {



   public static List<User> users = new ArrayList<>();

    public static void  addUser(User user) {
        users.add(user);
    }

    public static void removeUser(User user) {
        users.remove(user);
    }

    public static User getUserByID(String id) {
        for (User user : users) {
            if (user.getUserid().equals(id)) {
                return user;
            }

        }
        return null;
    }




}
