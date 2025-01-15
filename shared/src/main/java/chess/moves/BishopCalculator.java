package chess.moves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class BishopCalculator implements PieceMovesCalculator {

    private Collection<ChessMove> validMoves;
    private ChessPosition startPosition;
    private ChessPiece movingPiece;

    private void upLeft(ChessBoard board, ChessPosition myPosition) {

        int nextRow = myPosition.getRow() + 1;
        int nextCol = myPosition.getColumn() - 1;
        if ((nextRow <= 8) && (nextCol > 0)) {
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

        int nextRow = myPosition.getRow() + 1;
        int nextCol = myPosition.getColumn() + 1;
        if ((nextRow <= 8) && (nextCol <= 8)) {
            ChessPosition nextPosition = new ChessPosition(nextRow, nextCol);
            ChessPiece nextSquarePiece = board.getPiece(nextPosition);
            if (nextSquarePiece == null) {
                ChessMove move = new ChessMove(startPosition, nextPosition, null);
                validMoves.add(move);
                upRight(board, nextPosition);
            } else if (nextSquarePiece.getTeamColor() != movingPiece.getTeamColor()) {
                ChessMove move = new ChessMove(startPosition, nextPosition, null);
                validMoves.add(move);
            }
        }
    }

    private void downLeft(ChessBoard board, ChessPosition myPosition) {

        int nextRow = myPosition.getRow() - 1;
        int nextCol = myPosition.getColumn() - 1;
        if ((nextRow > 0) && (nextCol > 0)) {
            ChessPosition nextPosition = new ChessPosition(nextRow, nextCol);
            ChessPiece nextSquarePiece = board.getPiece(nextPosition);
            if (nextSquarePiece == null) {
                ChessMove move = new ChessMove(startPosition, nextPosition, null);
                validMoves.add(move);
                downLeft(board, nextPosition);
            } else if (nextSquarePiece.getTeamColor() != movingPiece.getTeamColor()) {
                ChessMove move = new ChessMove(startPosition, nextPosition, null);
                validMoves.add(move);
            }
        }
    }

    private void downRight(ChessBoard board, ChessPosition myPosition) {

        int nextRow = myPosition.getRow() - 1;
        int nextCol = myPosition.getColumn() + 1;
        if ((nextRow > 0) && (nextCol <= 8)) {
            ChessPosition nextPosition = new ChessPosition(nextRow, nextCol);
            ChessPiece nextSquarePiece = board.getPiece(nextPosition);
            if (nextSquarePiece == null) {
                ChessMove move = new ChessMove(startPosition, nextPosition, null);
                validMoves.add(move);
                downRight(board, nextPosition);
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
        validMoves = new ArrayList<ChessMove>();
        upLeft(board, myPosition);
        upRight(board, myPosition);
        downLeft(board, myPosition);
        downRight(board, myPosition);
        return validMoves;
    }
}
