package handler;

import model.*;
import service.BadRequestException;
import service.GameService;
import service.UnauthorizedException;
import service.UnavailableException;
import spark.Request;
import spark.Response;

public class GameHandler implements Handler {

    private final GameService gameService;

    public GameHandler() {
        gameService = new GameService();
    }

    public Object createHandler(Request req, Response res)
            throws UnauthorizedException, BadRequestException {

        CreateRequest request = gson.fromJson(req.body(), CreateRequest.class);

        CreateResult result = gameService.create(request);

        res.status(200);
        return gson.toJson(result);
    }

    public Object joinHandler(Request req, Response res)
            throws UnauthorizedException, UnavailableException, BadRequestException {

        JoinRequest request = gson.fromJson(req.body(), JoinRequest.class);

        JoinResult result = gameService.join(request);

        res.status(200);
        return gson.toJson(result);
    }

    public Object listHandler(Request req, Response res)
            throws UnauthorizedException {

        ListRequest request = gson.fromJson(req.body(), ListRequest.class);

        ListResult result = gameService.list(request);

        res.status(200);
        return gson.toJson(result);
    }
}
