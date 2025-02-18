package dataaccess;

import model.UserData;

import java.util.ArrayList;

public interface UserDAO {

    ArrayList<UserData> users = new ArrayList<>();

    static void clear() {
        users.clear();
    }

    static void createUser(UserData newUser) {
        users.add(newUser);
    }

    static UserData getUser(String username) {
        UserData found = null;
        for (UserData u : users) {
            if (u.username().equals(username)) {
                found = u;
                break;
            }
        }
        return found;
    }
}
