package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import exception.ResponseException;
import model.GameData;
import model.ListRequest;
import model.ListResult;
import serverfacade.ServerFacade;

import java.util.ArrayList;
import java.util.Arrays;

import static ui.EscapeSequences.*;

public class GamePlayClient {

    private ServerFacade server;
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
    }

    public void initializeGame(String authToken, boolean color, int id) {
        team = color ? 1 : 0;
        gameID = id;
        if (authToken != null) {
            try {
                ListResult listResult = server.list(new ListRequest(authToken));
                game = listResult.games().get(id - 1).game();
            } catch (Exception ex) {
                System.out.println(ex.getMessage() + "\nFailed to initialize game.\n");
            }
        } else {
            System.out.println("Failed to initialize game.\n");
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
            throw new ResponseException(400, "Team not set.\n");
        }
        if (team == 1) {
            drawWhite();
        } else {
            drawBlack();
        }
        return null;
    }

    public void drawWhite() {
        ChessBoard board = game.getBoard();
        System.out.print(edgeColor + txtColor + EMPTY);
        for (int i = 0; i < 8; i++) {
            System.out.print("  " + letters[i] + "  ");
        }
        System.out.println(edgeColor + txtColor + EMPTY);
        for (int i = 0; i < 8; i++) {
            System.out.print(edgeColor + txtColor + "  " + (8 - i) + "  ");
            for (int k = 0; k < 8; k++) {
                if ((k + i) % 2 == 0) {
                    System.out.print(bgColor1);
                } else {
                    System.out.print(bgColor2);
                }
                ChessPiece piece = board.getPiece(new ChessPosition(8 - i, k + 1));
                if (piece == null) {
                    System.out.print(EMPTY);
                } else {
                    if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                        System.out.print(whiteColor);
                    } else {
                        System.out.print(blackColor);
                    }
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
            System.out.println(edgeColor + txtColor + "  " + (8 - i) + "  ");
        }
        System.out.print(edgeColor + txtColor + EMPTY);
        for (int i = 0; i < 8; i++) {
            System.out.print("  " + letters[i] + "  ");
        }
        System.out.println(edgeColor + txtColor + EMPTY + "\n");
    }

    public void drawBlack() {

    }

    public String leave() {
        team = -1;
        return SET_TEXT_COLOR_YELLOW + "Leaving gameplay.\n";
    }

    public String help() {
        return """
                Options:
                Redraw Chess Board: "r", "redraw"
                Leave game: "leave"
                """;
    }
}
