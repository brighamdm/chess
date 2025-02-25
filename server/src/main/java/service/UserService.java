package service;

import model.*;

import static dataaccess.AuthDAO.*;
import static dataaccess.UserDAO.createUser;
import static dataaccess.UserDAO.getUser;

public class UserService implements Service {

    public RegisterResult register(RegisterRequest registerRequest)
            throws UnavailableException, BadRequestException {
        if (registerRequest.username() == null ||
                registerRequest.email() == null ||
                registerRequest.password() == null) {
            throw new BadRequestException("Error: bad request");
        }

        if (getUser(registerRequest.username()) == null) {
            UserData user = new UserData(registerRequest.username(),
                    registerRequest.password(),
                    registerRequest.email());
            createUser(user);

            String authToken = generateToken();
            AuthData auth = new AuthData(authToken, registerRequest.username());
            createAuth(auth);

            return new RegisterResult(registerRequest.username(),
                    authToken);
        } else {
            throw new UnavailableException("Error: already taken");
        }
    }

    public LoginResult login(LoginRequest loginRequest)
            throws UnauthorizedException, BadRequestException {
        if (loginRequest.username() == null ||
                loginRequest.password() == null) {
            throw new BadRequestException("Error: bad request");
        }

        UserData user = getUser(loginRequest.username());
        if (user != null) {
            if (user.password().equals(loginRequest.password())) {
                String authToken = generateToken();
                AuthData auth = new AuthData(authToken, loginRequest.username());
                createAuth(auth);

                return new LoginResult(loginRequest.username(), authToken);
            } else {
                throw new UnauthorizedException("Error: unauthorized");
            }
        } else {
            throw new BadRequestException("Error: bad request");
        }
    }

    public LogoutResult logout(LogoutRequest logoutRequest)
            throws UnauthorizedException, BadRequestException {

        if (logoutRequest.authToken() == null) {
            throw new BadRequestException("Error: bad request");
        }

        AuthData auth = getAuth(logoutRequest.authToken());
        if (auth != null) {
            deleteAuth(logoutRequest.authToken());
            return new LogoutResult();
        } else {
            throw new UnauthorizedException("Error: unauthorized");
        }
    }
}
