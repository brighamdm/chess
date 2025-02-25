package handler;

import spark.*;
import model.*;
import service.BadRequestException;
import service.UnauthorizedException;
import service.UnavailableException;
import service.UserService;

public class UserHandler implements Handler {

    public Object registerHandler(Request req, Response res)
            throws UnavailableException, BadRequestException {

        RegisterRequest request = fromJson(req.toString(), RegisterRequest.class);

        UserService service = new UserService();
        RegisterResult result = service.register(request);

        res.status(200);
        res.body(toJson(result));

        return toJson(result);
    }

    public Object loginHandler(Request req, Response res)
            throws UnauthorizedException, BadRequestException {

        LoginRequest request = fromJson(req.toString(), LoginRequest.class);

        UserService service = new UserService();
        LoginResult result = service.login(request);

        res.status(200);
        res.body(toJson(result));

        return toJson(result);
    }

    public Object logoutHandler(Request req, Response res)
            throws UnauthorizedException, BadRequestException {

        LogoutRequest request = fromJson(req.toString(), LogoutRequest.class);

        UserService service = new UserService();
        LogoutResult result = service.logout(request);

        res.status(200);
        res.body(toJson(result));

        return toJson(result);
    }
}
