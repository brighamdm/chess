package chess.moves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class KnightCalculator implements PieceMovesCalculator {

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        getValidMoves(board, myPosition, myPosition, 1, -2, false, validMoves);
        getValidMoves(board, myPosition, myPosition, 2, -1, false, validMoves);
        getValidMoves(board, myPosition, myPosition, 2, 1, false, validMoves);
        getValidMoves(board, myPosition, myPosition, 1, 2, false, validMoves);
        getValidMoves(board, myPosition, myPosition, -1, 2, false, validMoves);
        getValidMoves(board, myPosition, myPosition, -2, 1, false, validMoves);
        getValidMoves(board, myPosition, myPosition, -2, -1, false, validMoves);
        getValidMoves(board, myPosition, myPosition, -1, -2, false, validMoves);
        return validMoves;
    }
}
