package handler;

import model.*;
import service.BadRequestException;
import service.UnauthorizedException;
import service.UnavailableException;
import service.UserService;

public class UserHandler implements Handler {

    public String registerHandler(String data) throws UnavailableException, BadRequestException {

        RegisterRequest request = fromJson(data, RegisterRequest.class);

        UserService service = new UserService();
        RegisterResult result = service.register(request);

        return toJson(result);
    }

    public String loginHandler(String data)
            throws UnauthorizedException, BadRequestException {

        LoginRequest request = fromJson(data, LoginRequest.class);

        UserService service = new UserService();
        LoginResult result = service.login(request);

        return toJson(result);
    }

    public String logoutHandler(String data)
            throws UnauthorizedException, BadRequestException {

        LogoutRequest request = fromJson(data, LogoutRequest.class);

        UserService service = new UserService();
        LogoutResult result = service.logout(request);

        return toJson(result);
    }
}
