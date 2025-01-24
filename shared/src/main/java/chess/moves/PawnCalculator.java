package chess.moves;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

import static chess.ChessGame.TeamColor.WHITE;

public class PawnCalculator implements PieceMovesCalculator {

    private void checkEnPassant(ChessBoard board, ChessPosition startPosition, ChessPosition myPosition,
                                int rowChange, int colChange, boolean recursive, Collection<ChessMove> validMoves) {
        int pawnRow = myPosition.getRow();
        int pawnCol = myPosition.getColumn() + colChange;
        int nullRow = myPosition.getRow() + rowChange;
        int nullCol = myPosition.getColumn() + colChange;
        if (inBoard(pawnRow, pawnCol)) {
            ChessPosition pawnPosition = new ChessPosition(pawnRow, pawnCol);
            ChessPiece pawnPiece = board.getPiece(pawnPosition);
            ChessPosition nullPosition = new ChessPosition(nullRow, nullCol);
            ChessPiece nullPiece = board.getPiece(nullPosition);
            if (pawnPiece != null && pawnPiece.getSubjectToEnPassant() && nullPiece == null) {
                ChessMove move = new ChessMove(startPosition, nullPosition, null);
                move.setEnPassant();
                validMoves.add(move);
            }
        }
        if (recursive) {
            checkEnPassant(board, startPosition, myPosition, rowChange, -1, false, validMoves);
        }
    }

    private void addMoves(ChessGame.TeamColor team, ChessPosition startPosition, ChessPosition endPosition, Collection<ChessMove> validMoves) {
        if ((team == WHITE && endPosition.getRow() == 8) || (team == ChessGame.TeamColor.BLACK && endPosition.getRow() == 1)) {
            ChessMove move = new ChessMove(startPosition, endPosition, ChessPiece.PieceType.QUEEN);
            validMoves.add(move);
            move = new ChessMove(startPosition, endPosition, ChessPiece.PieceType.BISHOP);
            validMoves.add(move);
            move = new ChessMove(startPosition, endPosition, ChessPiece.PieceType.KNIGHT);
            validMoves.add(move);
            move = new ChessMove(startPosition, endPosition, ChessPiece.PieceType.ROOK);
            validMoves.add(move);
        } else {
            ChessMove move = new ChessMove(startPosition, endPosition, null);
            validMoves.add(move);
        }
    }

    private void getValidPawnForward(ChessBoard board, ChessPosition startPosition, ChessPosition myPosition,
                                     int rowChange, Collection<ChessMove> validMoves) {

        int nextRow = myPosition.getRow() + rowChange;
        int nextCol = myPosition.getColumn();
        if (inBoard(nextRow, nextCol)) {
            ChessPosition nextPosition = new ChessPosition(nextRow, nextCol);
            ChessPiece nextSquarePiece = board.getPiece(nextPosition);
            if (nextSquarePiece == null) {
                addMoves(board.getPiece(startPosition).getTeamColor(), startPosition, nextPosition, validMoves);
            }
        }
    }

    private void getValidPawnCaptures(ChessBoard board, ChessPosition startPosition, ChessPosition myPosition,
                                      int rowChange, int colChange, boolean recursive, Collection<ChessMove> validMoves) {

        int nextRow = myPosition.getRow() + rowChange;
        int nextCol = myPosition.getColumn() + colChange;
        if (inBoard(nextRow, nextCol)) {
            ChessPosition nextPosition = new ChessPosition(nextRow, nextCol);
            ChessPiece nextSquarePiece = board.getPiece(nextPosition);
            if ((nextSquarePiece != null) && (nextSquarePiece.getTeamColor() != (board.getPiece(startPosition)).getTeamColor())) {
                addMoves(board.getPiece(startPosition).getTeamColor(), startPosition, nextPosition, validMoves);
            }
        }
        if (recursive) {
            getValidPawnCaptures(board, startPosition, myPosition, rowChange, -1, false, validMoves);
        }
    }

    private void whitePawn(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> validMoves) {
        getValidPawnForward(board, myPosition, myPosition, 1, validMoves);
        ChessPosition nextForward = new ChessPosition(myPosition.getRow()+1, myPosition.getColumn());
        if (myPosition.getRow() == 2 && inBoard(nextForward.getRow(), nextForward.getColumn()) && board.getPiece(nextForward) == null) {
            getValidPawnForward(board, myPosition, myPosition, 2, validMoves);
        }
        if (myPosition.getRow() == 5) {
            checkEnPassant(board, myPosition, myPosition, 1, 1, true, validMoves);
        }
        getValidPawnCaptures(board, myPosition, myPosition, 1, 1, true, validMoves);
    }

    private void blackPawn(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> validMoves) {
        getValidPawnForward(board, myPosition, myPosition, -1, validMoves);
        ChessPosition nextForward = new ChessPosition(myPosition.getRow()-1, myPosition.getColumn());
        if (myPosition.getRow() == 7 && inBoard(nextForward.getRow(), nextForward.getColumn()) && board.getPiece(nextForward) == null) {
            getValidPawnForward(board, myPosition, myPosition, -2, validMoves);
        }
        if (myPosition.getRow() == 4) {
            checkEnPassant(board, myPosition, myPosition, -1, 1, true, validMoves);
        }
        getValidPawnCaptures(board, myPosition, myPosition, -1, 1, true, validMoves);
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {

        Collection<ChessMove> validMoves = new ArrayList<>();
        if (board.getPiece(myPosition).getTeamColor() == WHITE) {
            whitePawn(board, myPosition, validMoves);
        } else {
            blackPawn(board, myPosition, validMoves);
        }

        return validMoves;
    }
}
