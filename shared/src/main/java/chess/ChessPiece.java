package chess;

import chess.moves.*;

import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType pieceType;

    public ChessPiece(ChessGame.TeamColor pieceColor, PieceType type) {
        this.pieceColor = pieceColor;
        this.pieceType = type;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && pieceType == that.pieceType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, pieceType);
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return pieceType;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> validMoves;
        switch(pieceType) {
            case KING -> {
                PieceMovesCalculator calculator = new KingCalculator();
                validMoves = calculator.pieceMoves(board, myPosition);
            }
            case QUEEN -> {
                PieceMovesCalculator calculator = new QueenCalculator();
                validMoves = calculator.pieceMoves(board, myPosition);
            }
            case BISHOP -> {
                PieceMovesCalculator calculator = new BishopCalculator();
                validMoves = calculator.pieceMoves(board, myPosition);
            }
            case KNIGHT -> {
                PieceMovesCalculator calculator = new KnightCalculator();
                validMoves = calculator.pieceMoves(board, myPosition);
            }
            case ROOK -> {
                PieceMovesCalculator calculator = new RookCalculator();
                validMoves = calculator.pieceMoves(board, myPosition);
            }
            case PAWN -> {
                PieceMovesCalculator calculator = new PawnCalculator();
                validMoves = calculator.pieceMoves(board, myPosition);
            }
            default -> throw new IllegalStateException("Unexpected value: " + pieceType);
        }
        return validMoves;
    }
}
