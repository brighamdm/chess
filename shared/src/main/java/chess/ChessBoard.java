package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    private ChessPiece[][] squares;

    public ChessBoard() {
        squares = new ChessPiece[8][8];
    }

    public ChessBoard(ChessBoard original) {
        this.squares = new ChessPiece[8][8];
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                ChessPiece toCopy = original.squares[r][c];
                if (toCopy == null) {
                    this.squares[r][c] = null;
                } else {
                    this.squares[r][c] = new ChessPiece(toCopy);
                }
            }
        }
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        squares[position.getRow()-1][position.getColumn()-1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return squares[position.getRow()-1][position.getColumn()-1];
    }

    private void setSideOfBoard(ChessGame.TeamColor team, int row, int offset) {
        squares[row][0] = new ChessPiece(team, ChessPiece.PieceType.ROOK);
        squares[row][1] = new ChessPiece(team, ChessPiece.PieceType.KNIGHT);
        squares[row][2] = new ChessPiece(team, ChessPiece.PieceType.BISHOP);
        squares[row][3] = new ChessPiece(team, ChessPiece.PieceType.QUEEN);
        squares[row][4] = new ChessPiece(team, ChessPiece.PieceType.KING);
        squares[row][5] = new ChessPiece(team, ChessPiece.PieceType.BISHOP);
        squares[row][6] = new ChessPiece(team, ChessPiece.PieceType.KNIGHT);
        squares[row][7] = new ChessPiece(team, ChessPiece.PieceType.ROOK);

        row += offset;

        squares[row][0] = new ChessPiece(team, ChessPiece.PieceType.PAWN);
        squares[row][1] = new ChessPiece(team, ChessPiece.PieceType.PAWN);
        squares[row][2] = new ChessPiece(team, ChessPiece.PieceType.PAWN);
        squares[row][3] = new ChessPiece(team, ChessPiece.PieceType.PAWN);
        squares[row][4] = new ChessPiece(team, ChessPiece.PieceType.PAWN);
        squares[row][5] = new ChessPiece(team, ChessPiece.PieceType.PAWN);
        squares[row][6] = new ChessPiece(team, ChessPiece.PieceType.PAWN);
        squares[row][7] = new ChessPiece(team, ChessPiece.PieceType.PAWN);
    }

    public boolean movePiece(ChessMove move) {
        boolean success = false;
        ChessPosition startPosition = move.getStartPosition();
        ChessPosition endPosition = move.getEndPosition();
        ChessPiece movePiece = squares[startPosition.getRow()-1][startPosition.getColumn()-1];
        ChessPiece capturePiece = squares[endPosition.getRow()-1][endPosition.getColumn()-1];
        if (movePiece != null && (capturePiece == null || (movePiece.getTeamColor() != capturePiece.getTeamColor()))) {
            if (move.getPromotionPiece() != null) {
                movePiece.setPieceType(move.getPromotionPiece());
            }
            squares[startPosition.getRow()-1][startPosition.getColumn()-1] = null;
            squares[endPosition.getRow()-1][endPosition.getColumn()-1] = movePiece;
            success = true;
        }
        return success;
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        squares = new ChessPiece[8][8];
        setSideOfBoard(ChessGame.TeamColor.WHITE, 0, 1);
        setSideOfBoard(ChessGame.TeamColor.BLACK, 7, -1);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(squares, that.squares);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(squares);
    }
}
