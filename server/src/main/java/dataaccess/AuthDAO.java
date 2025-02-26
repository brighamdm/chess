package dataaccess;

import model.AuthData;

import java.util.ArrayList;

public interface AuthDAO {

    ArrayList<AuthData> AUTHS = new ArrayList<>();

    static void clear() {
        AUTHS.clear();
    }

    static void createAuth(AuthData newAuth) {
        AUTHS.add(newAuth);
    }

    static AuthData getAuth(String authToken) {
        AuthData found = null;
        for (AuthData a : AUTHS) {
            if (a.authToken().equals(authToken)) {
                found = a;
                break;
            }
        }
        return found;
    }

    static void deleteAuth(String authToken) {
        AuthData found = null;
        for (AuthData a : AUTHS) {
            if (a.authToken().equals(authToken)) {
                found = a;
                break;
            }
        }
        AUTHS.remove(found);
    }
}
