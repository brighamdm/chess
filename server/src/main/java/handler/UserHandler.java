package handler;

import model.*;
import service.UserService;

public class UserHandler implements Handler {

    public String registerHandler(String data) {

        RegisterRequest request = fromJson(data, RegisterRequest.class);

        UserService service = new UserService();
        RegisterResult result = service.register(request);

        return toJson(result);
    }

    public String loginHandler(String data) {

        LoginRequest request = fromJson(data, LoginRequest.class);

        UserService service = new UserService();
        LoginResult result = service.login(request);

        return toJson(result);
    }

    public String logoutHandler(String data) {

        LogoutRequest request = fromJson(data, LogoutRequest.class);

        UserService service = new UserService();
        LogoutResult result = service.logout(request);

        return toJson(result);
    }
}
