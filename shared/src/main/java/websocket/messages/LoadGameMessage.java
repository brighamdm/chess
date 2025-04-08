package websocket.messages;

import chess.ChessMove;

public class LoadGameMessage extends ServerMessage {
    String username;
    ChessMove move;

    public LoadGameMessage(ServerMessageType type, ChessMove move) {
        super(type);
        this.move = move;
    }

    public ChessMove getMove() {
        return move;
    }

    public String getUsername() {
        return username;
    }
}
