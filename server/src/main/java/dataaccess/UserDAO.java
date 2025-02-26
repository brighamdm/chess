package dataaccess;

import model.UserData;

import java.util.ArrayList;

public interface UserDAO {

    ArrayList<UserData> USERS = new ArrayList<>();

    static void clear() {
        USERS.clear();
    }

    static void createUser(UserData newUser) {
        USERS.add(newUser);
    }

    static UserData getUser(String username) {
        UserData found = null;
        for (UserData u : USERS) {
            if (u.username().equals(username)) {
                found = u;
                break;
            }
        }
        return found;
    }
}
