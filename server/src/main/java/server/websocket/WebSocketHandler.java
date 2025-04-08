package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.GameData;
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
                case LEAVE -> leave(command.getAuthToken(), command.getGameID(), session);
                case RESIGN -> resign(command.getAuthToken(), command.getGameID(), session);
            }
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    private void connect(String authToken, int gameID, Session session) throws IOException {
        try {
            connections.add(authToken, gameID, session);
            var message = String.format("%s has joined as .", userService.getUsername(authToken));
            var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
            connections.broadcast(authToken, gameID, notification);
        } catch (Exception e) {
            var notification = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Unable to Connect.");
            connections.message(authToken, gameID, notification);
        }
    }

    private void makeMove(String authToken, int gameID, ChessMove move, Session session) throws IOException {
        try {
            GameData updatedGame = gameService.move(authToken, gameID, move);
            var message = String.format("%s moved: %s", userService.getUsername(authToken), move.toString());
            var notification = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, message, updatedGame.game());
            connections.broadcast(authToken, gameID, notification);
            ChessGame.TeamColor team = updatedGame.game().getTeamTurn()
            if (updatedGame.game().isInCheckmate(team)) {
                message = String.format("%s is in checkmate!", (team == ChessGame.TeamColor.WHITE) ? updatedGame.whiteUsername() : updatedGame.blackUsername());
            } else if (updatedGame.game().isInCheck(team)) {
                message = String.format("%s is in check!", (team == ChessGame.TeamColor.WHITE) ? updatedGame.whiteUsername() : updatedGame.blackUsername());
            } else if (updatedGame.game().isInStalemate(team)) {
                message = "Stalemate!";
            }
            var gameNotification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
            connections.broadcast(authToken, gameID, gameNotification);
            connections.message(authToken, gameID, gameNotification);
        } catch (Exception e) {
            var notification = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Invalid Move.");
            connections.message(authToken, gameID, notification);
        }
    }

    private void leave(String authToken, int gameID, Session session) throws IOException {
        try {
            connections.remove(authToken);
            var message = String.format("%s has left.", userService.getUsername(authToken));
            var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
            connections.broadcast(authToken, gameID, notification);
        } catch (Exception e) {
            var notification = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Unable to Disconnect.");
            connections.message(authToken, gameID, notification);
        }
    }

    private void resign(String authToken, int gameID, Session session) {

    }
}
