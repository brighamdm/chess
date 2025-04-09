package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import service.BadRequestException;
import service.GameService;
import service.UserService;
import websocket.commands.ConnectCommand;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.Objects;

@WebSocket
public class WebSocketHandler {
    private final ConnectionManager connections = new ConnectionManager();
    private final UserService userService = new UserService();
    private final GameService gameService = new GameService();

    @OnWebSocketError
    public void onError(Session session, Throwable error) throws IOException {
        var notification = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, error.getMessage());
        session.getRemote().sendString(connections.notificationToJson(notification));
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws BadRequestException {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        MakeMoveCommand moveCommand = new Gson().fromJson(message, MakeMoveCommand.class);
        ConnectCommand connectCommand = new Gson().fromJson(message, ConnectCommand.class);
        try {
            switch (command.getCommandType()) {
                case CONNECT ->
                        connect(command.getAuthToken(), command.getGameID(), session, connectCommand.getState());
                case MAKE_MOVE -> makeMove(command.getAuthToken(), command.getGameID(), moveCommand.getMove(), session);
                case LEAVE -> leave(command.getAuthToken(), command.getGameID(), session);
                case RESIGN -> resign(command.getAuthToken(), command.getGameID(), session);
            }
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    private void connect(String authToken, int gameID, Session session, int state) throws IOException {
        try {
            String perspective;
            if (state == 0) {
                perspective = "white";
            } else if (state == 1) {
                perspective = "black";
            } else {
                perspective = "observer";
            }
            if (!gameService.validGameID(gameID)) {
                throw new IOException("Invalid Game ID");
            }
            connections.add(authToken, gameID, session);
            var message = String.format("%s has joined as %s", userService.getUsername(authToken), perspective);
            var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
            connections.broadcast(authToken, gameID, notification);
            connections.message(authToken, gameID, new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME,
                    gameService.getGame(authToken, gameID)));
        } catch (Exception e) {
            var notification = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Unable to Connect: " + e.getMessage());
            // connections.message(authToken, gameID, notification);
            session.getRemote().sendString(connections.notificationToJson(notification));
        }
    }

    private void makeMove(String authToken, int gameID, ChessMove move, Session session) throws IOException {
        try {
            GameData updatedGame = gameService.move(authToken, gameID, move);
            if (updatedGame == null) {
                System.out.println("game is null");
            }
            var message = String.format("%s moved: %s", userService.getUsername(authToken), move.toString());
            var messageNotification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
            var loadNotification = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, updatedGame.game());
            connections.broadcast(authToken, gameID, loadNotification);
            connections.broadcast(authToken, gameID, messageNotification);
            connections.message(authToken, gameID, loadNotification);
            boolean secondMessage = false;
            ChessGame.TeamColor team = updatedGame.game().getTeamTurn();
            if (updatedGame.game().isInCheckmate(team)) {
                secondMessage = true;
                message = String.format("%s is in checkmate!", (team == ChessGame.TeamColor.WHITE) ? updatedGame.whiteUsername() :
                        updatedGame.blackUsername());
            } else if (updatedGame.game().isInCheck(team)) {
                secondMessage = true;
                message = String.format("%s is in check!", (team == ChessGame.TeamColor.WHITE) ? updatedGame.whiteUsername() :
                        updatedGame.blackUsername());
            } else if (updatedGame.game().isInStalemate(team)) {
                secondMessage = true;
                message = "Stalemate!";
            }
            if (secondMessage) {
                var gameNotification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
                connections.broadcast(authToken, gameID, gameNotification);
                connections.message(authToken, gameID, gameNotification);
            }
        } catch (Exception e) {
            var notification = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, e.getMessage());
            session.getRemote().sendString(connections.notificationToJson(notification));
        }
    }

    private void leave(String authToken, int gameID, Session session) throws IOException {
        try {
            gameService.leave(authToken, gameID);
            var message = String.format("%s has left", userService.getUsername(authToken));
            var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
            connections.broadcast(authToken, gameID, notification);
            connections.remove(authToken);
        } catch (Exception e) {
            var notification = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Unable to Disconnect");
            connections.message(authToken, gameID, notification);
        }
    }

    private void resign(String authToken, int gameID, Session session) throws IOException {
        try {
            gameService.resign(authToken, gameID);
            var message = String.format("%s has resigned", userService.getUsername(authToken));
            var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
            connections.broadcast(authToken, gameID, notification);
            connections.message(authToken, gameID, notification);
        } catch (Exception e) {
            var notification = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Unable to Resign");
            connections.message(authToken, gameID, notification);
        }
    }
}
