package chess.moves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.Collection;

public interface PieceMovesCalculator {

    private boolean inBoard(int row, int col) {
        boolean result = true;
        if ((row < 1) || (row > 8)) {
            result = false;
        } else if ((col < 1) || (col > 8)) {
            result = false;
        }
        return result;
    }

    default void getValidMoves(ChessBoard board, ChessPosition startPosition, ChessPosition myPosition, int rowChange, int colChange, boolean recursive, Collection<ChessMove> validMoves) {

        int nextRow = myPosition.getRow() + rowChange;
        int nextCol = myPosition.getColumn() + colChange;
        if (inBoard(nextRow, nextCol)) {
            ChessPosition nextPosition = new ChessPosition(nextRow, nextCol);
            ChessPiece nextSquarePiece = board.getPiece(nextPosition);
            if (nextSquarePiece == null) {
                ChessMove move = new ChessMove(startPosition, nextPosition, null);
                validMoves.add(move);
                if (recursive) {
                    getValidMoves(board, startPosition, nextPosition, rowChange, colChange, recursive, validMoves);
                }
            } else if (nextSquarePiece.getTeamColor() != (board.getPiece(startPosition)).getTeamColor()) {
                ChessMove move = new ChessMove(startPosition, nextPosition, null);
                validMoves.add(move);
            }
        }
    }

    default Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        throw new RuntimeException("Not implemented");
    }
}

