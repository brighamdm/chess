package websocket.messages;

import chess.ChessMove;

public class LoadGameMessage extends ServerMessage {
    String username;
    ChessMove move;

    public LoadGameMessage(ServerMessageType type, String username, ChessMove move) {
        super(type);
        this.username = username;
        this.move = move;
    }

    public ChessMove getMove() {
        return move;
    }

    public String getUsername() {
        return username;
    }
}
