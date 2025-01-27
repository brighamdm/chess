package chess.moves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class KingCalculator implements PieceMovesCalculator {

    private void checkCastle(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> validMoves) {
        if (!board.getPiece(myPosition).getMoved() && myPosition.getColumn() == 5) {
            ChessPosition rightRook = new ChessPosition(myPosition.getRow(), myPosition.getColumn()+3);
            ChessPosition leftRook = new ChessPosition(myPosition.getRow(), myPosition.getColumn()-4);
            if (board.getPiece(rightRook) != null && !board.getPiece(rightRook).getMoved() &&
                    board.getPiece(new ChessPosition(myPosition.getRow(), myPosition.getColumn()+1)) == null &&
                    board.getPiece(new ChessPosition(myPosition.getRow(), myPosition.getColumn()+2)) == null) {
                ChessMove move = new ChessMove(myPosition, new ChessPosition(myPosition.getRow(), myPosition.getColumn()+2), null);
                move.setCastle();
                validMoves.add(move);
            }

            if (board.getPiece(leftRook) != null && !board.getPiece(leftRook).getMoved() &&
                    board.getPiece(new ChessPosition(myPosition.getRow(), myPosition.getColumn()-1)) == null &&
                    board.getPiece(new ChessPosition(myPosition.getRow(), myPosition.getColumn()-2)) == null &&
                    board.getPiece(new ChessPosition(myPosition.getRow(), myPosition.getColumn()-3)) == null) {
                ChessMove move = new ChessMove(myPosition, new ChessPosition(myPosition.getRow(), myPosition.getColumn()-2), null);
                move.setCastle();
                validMoves.add(move);
            }
        }
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        getValidMoves(board, myPosition, myPosition, 1, 1, false, validMoves);
        getValidMoves(board, myPosition, myPosition, -1, 1, false, validMoves);
        getValidMoves(board, myPosition, myPosition, 1, -1, false, validMoves);
        getValidMoves(board, myPosition, myPosition, -1, -1, false, validMoves);
        getValidMoves(board, myPosition, myPosition, 1, 0, false, validMoves);
        getValidMoves(board, myPosition, myPosition, -1, 0, false, validMoves);
        getValidMoves(board, myPosition, myPosition, 0, 1, false, validMoves);
        getValidMoves(board, myPosition, myPosition, 0, -1, false, validMoves);
        checkCastle(board, myPosition, validMoves);
        return validMoves;
    }
}
