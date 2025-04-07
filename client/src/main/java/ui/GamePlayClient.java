package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import exception.ResponseException;
import com.ListRequest;
import com.ListResult;
import serverfacade.ServerFacade;
import websocket.WebSocketFacade;

import java.util.Arrays;

import static ui.EscapeSequences.*;

public class GamePlayClient {

    private final ServerFacade server;
    private final WebSocketFacade websocket;
    private int team;
    private int gameID;
    private ChessGame game;
    private char[] letters;
    private String bgColor1;
    private String bgColor2;
    private String edgeColor;
    private String whiteColor;
    private String blackColor;
    private String txtColor;
    private String fgColor1;
    private String fgColor2;
    private String fgColor3;

    public GamePlayClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
        team = -1;

        letters = new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};

        bgColor1 = SET_BG_COLOR_LIGHT_GREY;
        bgColor2 = SET_BG_COLOR_BLACK;
        edgeColor = SET_BG_COLOR_DARK_GREY;
        whiteColor = SET_TEXT_COLOR_WHITE;
        blackColor = SET_TEXT_COLOR_DARK_GREY;
        txtColor = SET_TEXT_COLOR_LIGHT_GREY;
        fgColor1 = SET_TEXT_COLOR_LIGHT_GREY;
        fgColor2 = SET_TEXT_COLOR_BLACK;
        fgColor3 = SET_TEXT_COLOR_DARK_GREY;
    }

    public void initializeGame(String authToken, boolean color, int id) {
        team = color ? 1 : 0;
        gameID = id;
        if (authToken != null) {
            try {
                ListResult listResult = server.list(new ListRequest(authToken));
                game = listResult.games().get(id - 1).game();
            } catch (Exception ex) {
                System.out.println(ex.getMessage() + "\nFailed to initialize game.");
            }
        } else {
            System.out.println("Failed to initialize game.");
        }
    }

    public String eval(String line, String authToken) {
        try {
            var tokens = line.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "r", "redraw" -> draw();
                case "leave" -> leave();
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String draw() throws ResponseException {
        if (team == -1) {
            throw new ResponseException(400, "Team not set.");
        }
        drawBoard(team == 1);
        return "";
    }

    public void drawBoard(boolean isWhitePerspective) {
        ChessBoard board = game.getBoard();

        System.out.print(edgeColor + txtColor + "  ");

        // Determine column labels order
        if (isWhitePerspective) {
            for (int i = 0; i < 8; i++) {
                System.out.print(fgColor3 + HIDDEN_KING + txtColor + letters[i]);
            }
        } else {
            for (int i = 7; i >= 0; i--) {
                System.out.print(fgColor3 + HIDDEN_KING + txtColor + letters[i]);
            }
        }

        System.out.println("    " + RESET_BG_COLOR);

        // Determine row iteration order
        for (int i = 0; i < 8; i++) {
            int row = isWhitePerspective ? 8 - i : i + 1;
            System.out.print(edgeColor + txtColor + " " + row + " ");

            // Determine column iteration order
            for (int k = 0; k < 8; k++) {
                int col = isWhitePerspective ? k + 1 : 8 - k;

                int colorCheck = isWhitePerspective ? 0 : 1;
                String hiddenColor1 = isWhitePerspective ? fgColor1 : fgColor2;
                String hiddenColor2 = isWhitePerspective ? fgColor2 : fgColor1;

                if ((col - 1 + i) % 2 == colorCheck) {
                    System.out.print(bgColor1);
                } else {
                    System.out.print(bgColor2);
                }

                ChessPiece piece = board.getPiece(new ChessPosition(row, col));
                if (piece == null) {
                    if ((col - 1 + i) % 2 == 0) {
                        System.out.print(hiddenColor1 + BLACK_KING);
                    } else {
                        System.out.print(hiddenColor2 + BLACK_KING);
                    }
                } else {
                    System.out.print(piece.getTeamColor() == ChessGame.TeamColor.WHITE ? whiteColor : blackColor);

                    switch (piece.getPieceType()) {
                        case KING -> System.out.print(BLACK_KING);
                        case QUEEN -> System.out.print(BLACK_QUEEN);
                        case BISHOP -> System.out.print(BLACK_BISHOP);
                        case KNIGHT -> System.out.print(BLACK_KNIGHT);
                        case ROOK -> System.out.print(BLACK_ROOK);
                        case PAWN -> System.out.print(BLACK_PAWN);
                    }
                }
            }

            System.out.println(edgeColor + txtColor + " " + row + " " + RESET_BG_COLOR);
        }

        System.out.print(edgeColor + txtColor + "  ");

        // Print bottom column labels
        if (isWhitePerspective) {
            for (int i = 0; i < 8; i++) {
                System.out.print(fgColor3 + HIDDEN_KING + txtColor + letters[i]);
            }
        } else {
            for (int i = 7; i >= 0; i--) {
                System.out.print(fgColor3 + HIDDEN_KING + txtColor + letters[i]);
            }
        }

        System.out.println("    " + RESET_BG_COLOR);
    }

    public String leave() {
        team = -1;
        return SET_TEXT_COLOR_YELLOW + "Leaving gameplay.";
    }

    public String help() {
        return """
                \nOptions:
                Redraw Chess Board: "r", "redraw"
                Leave game: "leave"
                """;
    }
}
