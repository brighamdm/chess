package service;

import model.AuthData;

import java.util.UUID;

import static dataaccess.AuthDAO.getAuth;

public interface Service {

    default String generateToken() {
        return UUID.randomUUID().toString();
    }

    default boolean authExists(String authToken) {
        AuthData auth = getAuth(authToken);
        return auth != null;
    }
}
