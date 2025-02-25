package server;

import dataaccess.DataAccessException;
import handler.ClearHandler;
import handler.GameHandler;
import handler.UserHandler;
import service.BadRequestException;
import service.UnauthorizedException;
import service.UnavailableException;
import spark.*;

public class Server {

    private final ClearHandler clearHandler;
    private final UserHandler userHandler;
    private final GameHandler gameHandler;

    public Server() {
        this.clearHandler = new ClearHandler();
        this.userHandler = new UserHandler();
        this.gameHandler = new GameHandler();
    }

    public Server(ClearHandler clearHandler, UserHandler userHandler, GameHandler gameHandler) {
        this.clearHandler = clearHandler;
        this.userHandler = userHandler;
        this.gameHandler = gameHandler;
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.delete("/db", clearHandler::clearHandler);
        Spark.post("/user", userHandler::registerHandler);
        Spark.post("/session", userHandler::loginHandler);
        Spark.delete("/session", userHandler::logoutHandler);
        Spark.get("/game", gameHandler::listHandler);
        Spark.post("/game", gameHandler::createHandler);
        Spark.put("/game", gameHandler::joinHandler);

        Spark.exception(Exception.class, this::errorHandler);

        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    @SuppressWarnings("if")
    public void errorHandler(Exception e, Request req, Response res) {
        if (e instanceof DataAccessException) {
            res.status(500);
        } else if (e instanceof BadRequestException) {
            res.status(400);
        } else if (e instanceof UnauthorizedException) {
            res.status(401);
        } else if (e instanceof UnavailableException) {
            res.status(403);
        } else {
            res.status(666);
        }
        res.body(e.getMessage());
    }
}
