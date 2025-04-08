package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import service.BadRequestException;
import service.GameService;
import service.UnauthorizedException;
import service.UserService;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {
    private final ConnectionManager connections = new ConnectionManager();
    private final UserService userService = new UserService();
    private final GameService gameService = new GameService();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws BadRequestException {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        MakeMoveCommand moveCommand = (MakeMoveCommand) command;
        try {
            switch (command.getCommandType()) {
                case CONNECT -> connect(command.getAuthToken(), command.getGameID(), session);
                case MAKE_MOVE -> makeMove(command.getAuthToken(), command.getGameID(), moveCommand.getMove(), session);
                case LEAVE -> ;
                case RESIGN -> ;
            }
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    private void connect(String authToken, int gameID, Session session) throws IOException {
        try {
            connections.add(authToken, gameID, session);
            var message = String.format("%s has joined as ", userService.getUsername(authToken));
            var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
            connections.broadcast(authToken, gameID, notification);
        } catch (Exception e) {
            var notification = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Unable to Connect.");
            connections.message(authToken, notification);
        }
    }

    private void makeMove(String authToken, int gameID, ChessMove move, Session session) throws IOException {
        try {
            ChessGame updatedGame = gameService.move(authToken, gameID, move);
            var notification = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, userService.getUsername(authToken), move);
            connections.broadcast(authToken, gameID, notification);
        } catch (Exception e) {
            var notification = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Invalid Move.");
            connections.message(authToken, notification);
        }
    }
}
