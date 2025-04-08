package ui;

import chess.*;
import exception.ResponseException;
import com.ListRequest;
import com.ListResult;
import serverfacade.ServerFacade;
import websocket.NotificationHandler;
import websocket.WebSocketFacade;

import java.util.Arrays;

import static ui.EscapeSequences.*;

public class GamePlayClient {

    private final ServerFacade server;
    private final WebSocketFacade websocket;
    private int team;
    private int gameID;
    private boolean watching;
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

    public GamePlayClient(String serverUrl, NotificationHandler notificationHandler) {
        this.server = new ServerFacade(serverUrl);
        try {
            this.websocket = new WebSocketFacade(serverUrl, notificationHandler);
        } catch (ResponseException e) {
            System.out.println("\nFailed to connect to Websocket.");
            throw new RuntimeException(e);
        }
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
        if (authToken != null) {
            try {
                ListResult listResult = server.list(new ListRequest(authToken));
                game = listResult.games().get(id - 1).game();
                gameID = listResult.games().get(id - 1).gameID();
            } catch (Exception ex) {
                System.out.println(ex.getMessage() + "\nFailed to initialize game.");
            }
        } else {
            System.out.println("Failed to initialize game.");
        }
    }

    public void setWatching(boolean watching) {
        this.watching = watching;
    }

    public void setGame(ChessGame game) {
        this.game = game;
    }

    public String eval(String line, String authToken) {
        try {
            var tokens = line.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "r", "redraw" -> draw();
                case "leave" -> leave(authToken);
                case "resign" -> watching ? null : resign(authToken);
                case "m", "move" -> watching ? null : makeMove(authToken, params);
                case "highlight" -> highlight(params);
                default -> watching ? watchingHelp() : help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String highlight(String... params) {
        return "";
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

    public void connect(String authToken) throws ResponseException {
        websocket.connect(authToken, gameID);
    }

    public String makeMove(String authToken, String... params) throws ResponseException {
        boolean valid_input = true;
        ChessMove move = toChessMove(params);
        if (move == null) {
            System.out.println(SET_TEXT_COLOR_RED + "Invalid Move\nExpected: <START_POSITION> <END_POSITION>");
        }
        websocket.makeMove(authToken, gameID, move);
        return "";
    }

    public String leave(String authToken) throws ResponseException {
        websocket.leave(authToken, gameID);
        team = -1;
        return SET_TEXT_COLOR_YELLOW + "Leaving gameplay.";
    }

    public String resign(String authToken) throws ResponseException {
        websocket.resign(authToken, gameID);
        return "";
    }

    public String help() {
        return """
                \nOptions:
                Redraw Chess Board: "r", "redraw"
                Leave game: "leave"
                Resign: "resign"
                Make Move: "m" "move" <START_POSITION> <END_POSITION>
                Highlight Legal Moves: "highlight" <POSITION>
                """;
    }

    public String watchingHelp() {
        return """
                \nOptions:
                Redraw Chess Board: "r", "redraw"
                Leave game: "leave"
                Highlight Legal Moves: "highlight" <POSITION>
                """;
    }

    public ChessMove toChessMove(String... params) {
        ChessMove move = null;
        int startRow = -1;
        int startCol = -1;
        int endRow = -1;
        int endCol = -1;
        if (params.length == 2) {
            if (params[0].length() == 2) {
                startCol = (switch (params[0].toLowerCase().charAt(0)) {
                    case 'a' -> 1;
                    case 'b' -> 2;
                    case 'c' -> 3;
                    case 'd' -> 4;
                    case 'e' -> 5;
                    case 'f' -> 6;
                    case 'g' -> 7;
                    case 'h' -> 8;
                    default -> -1;
                });
                int row = params[0].charAt(1) - '0';
                if (row > 0 && row < 9) {
                    startRow = row;
                }
            }
            if (params[1].length() == 2) {
                endCol = (switch (params[1].toLowerCase().charAt(0)) {
                    case 'a' -> 1;
                    case 'b' -> 2;
                    case 'c' -> 3;
                    case 'd' -> 4;
                    case 'e' -> 5;
                    case 'f' -> 6;
                    case 'g' -> 7;
                    case 'h' -> 8;
                    default -> -1;
                });
                int row = params[1].charAt(1) - '0';
                if (row > 0 && row < 9) {
                    endRow = row;
                }
            }
            if (startRow != -1 && startCol != -1 && endRow != -1 && endCol != -1) {
                move = new ChessMove(new ChessPosition(startRow, startCol), new ChessPosition(endRow, endCol), ChessPiece.PieceType.QUEEN);
            }
        }
        return move;
    }
}
