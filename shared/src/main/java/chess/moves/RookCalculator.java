package chess.moves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class RookCalculator implements PieceMovesCalculator {

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        getValidMoves(board, myPosition, myPosition, 1, 0, true, validMoves);
        getValidMoves(board, myPosition, myPosition, -1, 0, true, validMoves);
        getValidMoves(board, myPosition, myPosition, 0, 1, true, validMoves);
        getValidMoves(board, myPosition, myPosition, 0, -1, true, validMoves);
        return validMoves;
    }
}
