package ui;

import chess.*;
import exception.ResponseException;
import com.ListRequest;
import com.ListResult;
import serverfacade.ServerFacade;
import websocket.NotificationHandler;
import websocket.WebSocketFacade;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

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
    private String hlBColor1;
    private String hlBColor2;
    private String hlFColor1;
    private String hlFColor2;
    private String hlSColor;

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
        hlBColor1 = SET_BG_COLOR_LIGHT_PURPLE;
        hlBColor2 = SET_BG_COLOR_PURPLE;
        hlFColor1 = SET_TEXT_COLOR_LIGHT_PURPLE;
        hlFColor2 = SET_TEXT_COLOR_PURPLE;
        hlSColor = SET_BG_COLOR_MAGENTA;
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
                case "resign" -> watching ? "" : resign(authToken);
                case "m", "move" -> watching ? "" : makeMove(authToken, params);
                case "highlight" -> highlight(params);
                default -> watching ? watchingHelp() : help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String highlight(String... params) throws ResponseException {
        if (params.length == 1) {
            ChessPosition pos = toChessPosition(params[0]);
            ArrayList<ChessMove> moves = (ArrayList<ChessMove>) game.validMoves(pos);
            ArrayList<ChessPosition> positions = new ArrayList<>();
            ChessPosition startPosition = null;
            if (moves != null) {
                for (ChessMove m : moves) {
                    positions.add(m.getEndPosition());
                }
                startPosition = moves.get(0).getStartPosition();
            }
            System.out.println("               ");
            drawBoard(team == 1, positions, startPosition);
            System.out.println();
            return "";
        } else {
            throw new ResponseException(400, "Bad Input\nExpected <POSITION>");
        }
    }

    public String draw() throws ResponseException {
        if (team == -1) {
            throw new ResponseException(400, "Team not set.");
        }
        ArrayList<ChessPosition> emptyList = new ArrayList<>();
        ChessPosition nullPosition = null;
        System.out.println("               ");
        drawBoard(team == 1, emptyList, nullPosition);
        System.out.println();
        return "";
    }

    public void drawBoard(boolean isWhitePerspective, ArrayList<ChessPosition> positions, ChessPosition startPosition) {
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
                String bgc1 = bgColor1;
                String bgc2 = bgColor2;

                if (positions != null && positions.contains(new ChessPosition(row, col))) {
                    hiddenColor1 = isWhitePerspective ? hlFColor1 : hlFColor2;
                    hiddenColor2 = isWhitePerspective ? hlFColor2 : hlFColor1;
                    bgc1 = hlBColor1;
                    bgc2 = hlBColor2;
                }
                if (startPosition != null && (startPosition.getRow() == row && startPosition.getColumn() == col)) {
                    System.out.print(hlSColor);
                } else if ((col - 1 + i) % 2 == colorCheck) {
                    System.out.print(bgc1);
                } else {
                    System.out.print(bgc2);
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
        int state;
        if (watching) {
            state = 2;
        } else if (team == 1) {
            state = 0;
        } else {
            state = 1;
        }
        websocket.connect(authToken, gameID, state);
    }

    public String makeMove(String authToken, String... params) throws ResponseException {
        ChessMove move = toChessMove(params);
        if (move != null) {
            ChessPiece piece = game.getBoard().getPiece(move.getStartPosition());
            ChessGame.TeamColor teamColor = (team == 1) ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;
            if ((piece != null) && (piece.getTeamColor() == teamColor)) {
                if (piece.getPieceType() == ChessPiece.PieceType.PAWN &&
                        (move.getEndPosition().getRow() == 1 || move.getEndPosition().getRow() == 8)) {
                    move = new ChessMove(move.getStartPosition(), move.getEndPosition(), getPromotionPiece());
                }
                websocket.makeMove(authToken, gameID, move);
                return "";
            } else {
                throw new ResponseException(400, "Invalid Move\nExpected: <START_POSITION> <END_POSITION>");
            }
        } else {
            throw new ResponseException(400, "Invalid Move\nExpected: <START_POSITION> <END_POSITION>");
        }
    }

    public ChessPiece.PieceType getPromotionPiece() {
        var prompt = SET_TEXT_COLOR_BLUE +
                "Choose Promotion Piece: \"queen\", \"rook\", \"bishop\", \"knight\", \"pawn\"\n" +
                SET_TEXT_COLOR_GREEN +
                "Piece Type >>> ";
        Scanner scanner = new Scanner(System.in);
        ChessPiece.PieceType type = null;
        while (type == null) {
            System.out.print(prompt);
            String line = scanner.nextLine();
            var tokens = line.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "";
            switch (cmd) {
                case "queen" -> type = ChessPiece.PieceType.QUEEN;
                case "rook" -> type = ChessPiece.PieceType.ROOK;
                case "bishop" -> type = ChessPiece.PieceType.BISHOP;
                case "knight" -> type = ChessPiece.PieceType.KNIGHT;
                case "pawn" -> type = ChessPiece.PieceType.PAWN;
            }
        }
        return type;
    }

    public String leave(String authToken) throws ResponseException {
        websocket.leave(authToken, gameID);
        team = -1;
        return SET_TEXT_COLOR_YELLOW + "Leaving gameplay.";
    }

    public String resign(String authToken) throws ResponseException {
        if (confirmResign()) {
            websocket.resign(authToken, gameID);
        }
        return "";
    }

    public boolean confirmResign() {
        boolean result = false;
        var prompt = SET_TEXT_COLOR_BLUE +
                "Confirm Resignation: \"yes\", \"no\"\n" +
                SET_TEXT_COLOR_GREEN +
                "YES/NO >>> ";
        Scanner scanner = new Scanner(System.in);
        String cmd = null;
        while (!result && !Objects.equals(cmd, "no")) {
            System.out.print(prompt);
            String line = scanner.nextLine();
            var tokens = line.toLowerCase().split(" ");
            cmd = (tokens.length > 0) ? tokens[0] : "";
            if (Objects.equals(cmd, "yes")) {
                result = true;
            }
        }
        return result;
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

    public ChessPosition toChessPosition(String pos) {
        ChessPosition position = null;
        int row = -1;
        int col = -1;
        if (pos.length() == 2) {
            col = (switch (pos.toLowerCase().charAt(0)) {
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
            int tempRow = pos.charAt(1) - '0';
            if (tempRow > 0 && tempRow < 9) {
                row = tempRow;
            }
            if (row != -1 && col != -1) {
                position = new ChessPosition(row, col);
            }
        }
        return position;
    }

    public ChessMove toChessMove(String... params) {
        ChessMove move = null;
        if (params.length == 2) {
            ChessPosition pos1 = toChessPosition(params[0]);
            ChessPosition pos2 = toChessPosition(params[1]);
            if (pos1 != null && pos2 != null) {
                move = new ChessMove(pos1, pos2, null);
            }
        }
        return move;
    }
}
