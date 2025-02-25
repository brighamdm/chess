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

        CreateRequest requestTemp = gson.fromJson(req.body(), CreateRequest.class);
        CreateRequest request = new CreateRequest(requestTemp.gameName(), req.headers("Authorization"));

        CreateResult result = gameService.create(request);

        res.status(200);
        return gson.toJson(result);
    }

    public Object joinHandler(Request req, Response res)
            throws UnauthorizedException, UnavailableException, BadRequestException {

        JoinRequest requestTemp = gson.fromJson(req.body(), JoinRequest.class);
        JoinRequest request = new JoinRequest(requestTemp.playerColor(), requestTemp.gameID(), req.headers("Authorization"));

        JoinResult result = gameService.join(request);

        res.status(200);
        return gson.toJson(result);
    }

    public Object listHandler(Request req, Response res)
            throws UnauthorizedException {

        ListRequest request = new ListRequest(req.headers("Authorization"));

        ListResult result = gameService.list(request);

        res.status(200);
        return gson.toJson(result);
    }
}
