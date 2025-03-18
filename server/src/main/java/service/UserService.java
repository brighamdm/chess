package service;

import dataaccess.DataAccessException;
import model.*;
import org.mindrot.jbcrypt.BCrypt;

import static dataaccess.AuthDAO.*;
import static dataaccess.UserDAO.createUser;
import static dataaccess.UserDAO.getUser;

public class UserService implements Service {

    public RegisterResult register(RegisterRequest registerRequest)
            throws UnavailableException, BadRequestException, DataAccessException {
        if (registerRequest.username() == null ||
                registerRequest.email() == null ||
                registerRequest.password() == null) {
            throw new BadRequestException("Bad Request");
        }
        System.out.println("hashing password:" + registerRequest.password());
        if (getUser(registerRequest.username()) == null) {
            System.out.println("hashing password");
            String hashedPassword = BCrypt.hashpw(registerRequest.password(),
                    BCrypt.gensalt());
            System.out.println("password hashed");
            UserData user = new UserData(registerRequest.username(),
                    hashedPassword,
                    registerRequest.email());
            System.out.println("user created");
            createUser(user);
            System.out.println("generating token");
            String authToken = generateToken();
            AuthData auth = new AuthData(authToken, registerRequest.username());
            createAuth(auth);
            System.out.println("returning register result " + registerRequest.username() + " " + authToken);
            return new RegisterResult(registerRequest.username(),
                    authToken);
        } else {
            throw new UnavailableException("Already Taken");
        }
    }

    public LoginResult login(LoginRequest loginRequest)
            throws UnauthorizedException, BadRequestException, DataAccessException {
        if (loginRequest.username() == null ||
                loginRequest.password() == null) {
            throw new BadRequestException("Bad Request");
        }

        UserData user = getUser(loginRequest.username());
        if (user != null) {
            if (BCrypt.checkpw(loginRequest.password(), user.password())) {
                String authToken = generateToken();
                AuthData auth = new AuthData(authToken, loginRequest.username());
                createAuth(auth);

                return new LoginResult(loginRequest.username(), authToken);
            } else {
                throw new UnauthorizedException("Unauthorized");
            }
        } else {
            throw new UnauthorizedException("Unauthorized");
        }
    }

    public LogoutResult logout(LogoutRequest logoutRequest)
            throws UnauthorizedException, BadRequestException, DataAccessException {
        System.out.println("logging out " + logoutRequest.authToken());
        if (logoutRequest == null || logoutRequest.authToken() == null) {
            throw new BadRequestException("Bad Request");
        }

        AuthData auth = getAuth(logoutRequest.authToken());
        if (auth != null) {
            deleteAuth(logoutRequest.authToken());
            System.out.println("returning logout request");
            return new LogoutResult();
        } else {
            System.out.println("unauthorized");
            throw new UnauthorizedException("Unauthorized");
        }
    }
}
