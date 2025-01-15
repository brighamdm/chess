package chess.moves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.Collection;

public class BishopCalculator implements PieceMovesCalculator {

    private Collection<ChessMove> validMoves;
    private ChessPosition startPosition;
    private ChessPiece movingPiece;

    private void upLeft(ChessBoard board, ChessPosition myPosition) {

        int nextRow = myPosition.getRow();
        int nextCol = myPosition.getColumn();
        if ((nextRow + 1 <= 8) && (nextCol - 1 > 0)) {
            ChessPosition nextPosition = new ChessPosition(nextRow, nextCol);
            ChessPiece nextSquarePiece = board.getPiece(nextPosition);
            if (nextSquarePiece == null) {
                ChessMove move = new ChessMove(startPosition, nextPosition, null);
                validMoves.add(move);
                upLeft(board, nextPosition);
            } else if (nextSquarePiece.getTeamColor() != movingPiece.getTeamColor()) {
                ChessMove move = new ChessMove(startPosition, nextPosition, null);
                validMoves.add(move);
            }
        }
    }

    private void upRight(ChessBoard board, ChessPosition myPosition) {

        int nextRow = myPosition.getRow();
        int nextCol = myPosition.getColumn();
        if ((nextRow + 1 <= 8) && (nextCol + 1 <= 8)) {
            ChessPosition nextPosition = new ChessPosition(nextRow, nextCol);
            ChessPiece nextSquarePiece = board.getPiece(nextPosition);
            if (nextSquarePiece == null) {
                ChessMove move = new ChessMove(startPosition, nextPosition, null);
                validMoves.add(move);
                upLeft(board, nextPosition);
            } else if (nextSquarePiece.getTeamColor() != movingPiece.getTeamColor()) {
                ChessMove move = new ChessMove(startPosition, nextPosition, null);
                validMoves.add(move);
            }
        }
    }

    private void downLeft(ChessBoard board, ChessPosition myPosition) {

        int nextRow = myPosition.getRow();
        int nextCol = myPosition.getColumn();
        if ((nextRow - 1 > 0) && (nextCol - 1 > 0)) {
            ChessPosition nextPosition = new ChessPosition(nextRow, nextCol);
            ChessPiece nextSquarePiece = board.getPiece(nextPosition);
            if (nextSquarePiece == null) {
                ChessMove move = new ChessMove(startPosition, nextPosition, null);
                validMoves.add(move);
                upLeft(board, nextPosition);
            } else if (nextSquarePiece.getTeamColor() != movingPiece.getTeamColor()) {
                ChessMove move = new ChessMove(startPosition, nextPosition, null);
                validMoves.add(move);
            }
        }
    }

    private void downRight(ChessBoard board, ChessPosition myPosition) {

        int nextRow = myPosition.getRow();
        int nextCol = myPosition.getColumn();
        if ((nextRow - 1 > 0) && (nextCol + 1 <= 8)) {
            ChessPosition nextPosition = new ChessPosition(nextRow, nextCol);
            ChessPiece nextSquarePiece = board.getPiece(nextPosition);
            if (nextSquarePiece == null) {
                ChessMove move = new ChessMove(startPosition, nextPosition, null);
                validMoves.add(move);
                upLeft(board, nextPosition);
            } else if (nextSquarePiece.getTeamColor() != movingPiece.getTeamColor()) {
                ChessMove move = new ChessMove(startPosition, nextPosition, null);
                validMoves.add(move);
            }
        }
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        startPosition = myPosition;
        movingPiece = board.getPiece(myPosition);
        upLeft(board, myPosition);
        upRight(board, myPosition);
        downLeft(board, myPosition);
        downRight(board, myPosition);
        return validMoves;
    }
}
