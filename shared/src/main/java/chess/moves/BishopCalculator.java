package chess.moves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class BishopCalculator implements PieceMovesCalculator {

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        getValidMoves(board, myPosition, myPosition, 1, 1, true, validMoves);
        getValidMoves(board, myPosition, myPosition, -1, 1, true, validMoves);
        getValidMoves(board, myPosition, myPosition, 1, -1, true, validMoves);
        getValidMoves(board, myPosition, myPosition, -1, -1, true, validMoves);
        return validMoves;
    }
}
