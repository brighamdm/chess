package service;

import model.*;

import static dataaccess.AuthDAO.createAuth;
import static dataaccess.AuthDAO.deleteAuth;
import static dataaccess.UserDAO.createUser;
import static dataaccess.UserDAO.getUser;

public class UserService implements Service {

    public RegisterResult register(RegisterRequest registerRequest) {
        if (getUser(registerRequest.username()) == null) {
            UserData user = new UserData(registerRequest.username(),
                    registerRequest.password(),
                    registerRequest.email());
            createUser(user);

            String authToken = generateToken();
            AuthData auth = new AuthData(authToken, registerRequest.username());
            createAuth(auth);

            return new RegisterResult(null,
                    registerRequest.username(),
                    authToken);
        } else {
            return new RegisterResult("Error: ",
                    null, null);
        }
    }

    public LoginResult login(LoginRequest loginRequest) {
        UserData user = getUser(loginRequest.username());
        if (user != null) {
            String authToken = generateToken();
            AuthData auth = new AuthData(authToken, loginRequest.username());
            createAuth(auth);

            return new LoginResult(null,
                    loginRequest.username(),
                    authToken);
        } else {
            return new LoginResult("Error: ",
                    null, null);
        }
    }

    public LogoutResult logout(LogoutRequest logoutRequest) {
        deleteAuth(logoutRequest.authToken());
        return new LogoutResult(null);
    }
}
