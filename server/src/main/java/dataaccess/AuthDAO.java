package dataaccess;

import model.AuthData;

import java.util.ArrayList;

public interface AuthDAO {

    ArrayList<AuthData> auths = new ArrayList<AuthData>();

    static void clear() {
        auths.clear();
    }

    static void createAuth(AuthData newAuth) {
        auths.add(newAuth);
    }

    static AuthData getAuth(String authToken) {
        AuthData found = null;
        for (AuthData a : auths) {
            if (a.authToken().equals(authToken)) {
                found = a;
                break;
            }
        }
        return found;
    }
}
