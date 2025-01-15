package chess.moves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

import static chess.ChessGame.TeamColor.WHITE;

public class PawnCalculator implements PieceMovesCalculator {

    private void getValidPawnForward(ChessBoard board, ChessPosition startPosition, ChessPosition myPosition, int rowChange, int colChange, boolean recursive, Collection<ChessMove> validMoves) {

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
            }
        }
    }

    private void getValidPawnCaptures(ChessBoard board, ChessPosition startPosition, ChessPosition myPosition, int rowChange, int colChange, boolean recursive, Collection<ChessMove> validMoves) {

        int nextRow = myPosition.getRow() + rowChange;
        int nextCol = myPosition.getColumn() + colChange;
        if (inBoard(nextRow, nextCol)) {
            ChessPosition nextPosition = new ChessPosition(nextRow, nextCol);
            ChessPiece nextSquarePiece = board.getPiece(nextPosition);
            if ((nextSquarePiece != null) && (nextSquarePiece.getTeamColor() != (board.getPiece(startPosition)).getTeamColor())) {
                ChessMove move = new ChessMove(startPosition, nextPosition, null);
                validMoves.add(move);
                if (recursive) {
                    getValidPawnCaptures(board, startPosition, myPosition, rowChange, -1, false, validMoves);
                }
            }
        }
    }

    private void whitePawn(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> validMoves) {
        getValidPawnForward(board, myPosition, myPosition, 1, 0, false, validMoves);
        if (myPosition.getRow() == 2) {
            getValidPawnForward(board, myPosition, myPosition, 2, 0, false, validMoves);
        }
        getValidPawnCaptures(board, myPosition, myPosition, 1, 1, true, validMoves);
    }

    private void blackPawn(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> validMoves) {
        getValidPawnForward(board, myPosition, myPosition, -1, 0, false, validMoves);
        if (myPosition.getRow() == 7) {
            getValidPawnForward(board, myPosition, myPosition, -2, 0, false, validMoves);
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
