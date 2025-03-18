package handler;

import dataaccess.DataAccessException;
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
            throws UnauthorizedException, BadRequestException, DataAccessException {

        String authToken = req.headers("Authorization");

        CreateRequest request;
        if (authToken == null) {
            request = GSON.fromJson(req.body(), CreateRequest.class);
        } else {
            CreateRequest requestTemp = GSON.fromJson(req.body(), CreateRequest.class);
            request = new CreateRequest(requestTemp.gameName(), authToken);
        }

        CreateResult result = gameService.create(request);

        res.status(200);
        return GSON.toJson(result);
    }

    public Object joinHandler(Request req, Response res)
            throws UnauthorizedException, UnavailableException, BadRequestException, DataAccessException {

        String authToken = req.headers("Authorization");

        JoinRequest request;
        if (authToken == null) {
            request = GSON.fromJson(req.body(), JoinRequest.class);
        } else {
            JoinRequest requestTemp = GSON.fromJson(req.body(), JoinRequest.class);
            request = new JoinRequest(requestTemp.playerColor(), requestTemp.gameID(), authToken);
        }

        JoinResult result = gameService.join(request);

        res.status(200);
        return GSON.toJson(result);
    }

    public Object listHandler(Request req, Response res)
            throws UnauthorizedException, DataAccessException {
        String authToken = req.headers("Authorization");
        ListRequest request;
        if (authToken == null) {
            request = GSON.fromJson(req.body(), ListRequest.class);
        } else {
            request = new ListRequest(authToken);
        }

        ListResult result = gameService.list(request);
        res.status(200);
        return GSON.toJson(result);
    }
}
