package ui;

import exception.ResponseException;
import model.*;
import serverfacade.ServerFacade;

import java.util.ArrayList;
import java.util.Arrays;

import static ui.EscapeSequences.SET_TEXT_COLOR_BLUE;
import static ui.EscapeSequences.SET_TEXT_COLOR_YELLOW;

public class LoggedInClient {

    private final ServerFacade server;
    private ArrayList<GameData> gamesList;

    public LoggedInClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
    }

    public void initializeList(String authToken) {
        if (authToken != null) {
            try {
                ListResult listResult = server.list(new ListRequest(authToken));
                gamesList = (ArrayList<GameData>) listResult.games();
            } catch (Exception ex) {
                System.out.println("\nFailed to initialize list.\n");
            }
        } else {
            System.out.println("Failed to initialize list.\n");
        }
    }

    public String eval(String line, String authToken, StringBuilder id) {
        try {
            var tokens = line.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "l", "list" -> list(authToken);
                case "c", "create" -> create(authToken, params);
                case "j", "join" -> join(authToken, id, params);
                case "w", "watch" -> watch(authToken, id, params);
                case "logout" -> logout(authToken);
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String list(String authToken) throws ResponseException {
        if (authToken != null) {
            try {
                ListResult listResult = server.list(new ListRequest(authToken));
                gamesList = (ArrayList<GameData>) listResult.games();
                StringBuilder result = new StringBuilder();
                if (!listResult.games().isEmpty()) {
                    for (int i = 0; i < listResult.games().size(); i++) {
                        GameData game = listResult.games().get(i);
                        String white = (game.whiteUsername() == null) ? "   White empty" : "   White: " + game.whiteUsername();
                        String black = (game.blackUsername() == null) ? "   Black empty" : "   Black: " + game.blackUsername();
                        result.append(i + 1).append(". Game name: ").append(game.gameName()).append(white).append(black).append("\n");
                    }
                } else {
                    result.append("No games to list.\n");
                }
                return SET_TEXT_COLOR_BLUE + result;
            } catch (Exception ex) {
                throw new ResponseException(400, ex.getMessage() + "\nFailed to list.\n");
            }
        }
        throw new ResponseException(400, "Failed to list.\n");
    }

    public String create(String authToken, String... params) throws ResponseException {
        if (authToken != null && params.length >= 1) {
            try {
                CreateResult createResult = server.create(new CreateRequest(params[0], authToken));
                return SET_TEXT_COLOR_BLUE + "Successfully created game " + params[0] + "\n";
            } catch (Exception ex) {
                throw new ResponseException(400, ex.getMessage() + "Expected: <GAME NAME>\n");
            }
        }
        throw new ResponseException(400, "Expected: <GAME NAME>\n");
    }

    public String join(String authToken, StringBuilder returnID, String... params) throws ResponseException {
        if (authToken != null && params.length >= 2 && !gamesList.isEmpty()) {
            int id;
            try {
                id = Integer.parseInt(params[0]);
            } catch (Exception ex) {
                throw new ResponseException(400, "Game ID must be a number.\n");
            }
            if (id > gamesList.size() || id <= 0) {
                throw new ResponseException(400, "Invalid Game ID.\n");
            }
            try {
                JoinResult joinResult = server.join(new JoinRequest(params[1].toUpperCase(), gamesList.get(id - 1).gameID(), authToken));
                returnID.append(id);
                return params[1].toUpperCase();
            } catch (Exception ex) {
                throw new ResponseException(400, ex.getMessage() + "Expected: <GAME ID> <COLOR>\n");
            }
        }
        throw new ResponseException(400, "Expected: <GAME ID> <COLOR>\nCreate a game if no game exists.\n");
    }

    public String watch(String authToken, StringBuilder returnID, String... params) throws ResponseException {
        if (authToken != null && params.length >= 2 && !gamesList.isEmpty()) {
            int id;
            try {
                id = Integer.parseInt(params[0]);
            } catch (Exception ex) {
                throw new ResponseException(400, "Game ID must be a number.\n");
            }
            if (id > gamesList.size() || id <= 0) {
                throw new ResponseException(400, "Invalid Game ID.\n");
            }
            try {
                returnID.append(id);
                return "watching";
            } catch (Exception ex) {
                throw new ResponseException(400, ex.getMessage() + "Expected: <GAME ID>\n");
            }
        }
        throw new ResponseException(400, "Expected: <GAME ID>\nCreate a game if no game exists.\n");
    }

    public String logout(String authToken) throws ResponseException {
        if (authToken != null) {
            try {
                LogoutResult logoutResult = server.logout(new LogoutRequest(authToken));
                return SET_TEXT_COLOR_YELLOW + "Logged out.\n";
            } catch (Exception ex) {
                throw new ResponseException(400, ex.getMessage() + "\nFailed to logout.\n");
            }
        }
        throw new ResponseException(400, "Failed to logout.\n");
    }

    public String help() {
        return """
                Options:
                List current games: "l", "list"
                Create a new game: "c", "create" <GAME NAME>
                Join a game: "j", "join" <GAME ID> <COLOR>
                Watch a game: "w", "watch" <GAME ID>
                Logout: "logout"
                """;
    }
}
