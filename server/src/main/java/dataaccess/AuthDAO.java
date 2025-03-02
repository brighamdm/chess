package dataaccess;

import model.AuthData;

import java.util.ArrayList;

public interface AuthDAO {

    static void clear() throws DataAccessException {
        var statement = "";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("SELECT 1+1")) {
                var rs = preparedStatement.executeQuery();
                rs.next();
                System.out.println(rs.getInt(1));
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
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
