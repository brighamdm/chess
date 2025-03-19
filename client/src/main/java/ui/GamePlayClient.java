package ui;

import exception.ResponseException;
import model.GameData;
import model.ListRequest;
import model.ListResult;
import serverfacade.ServerFacade;

import java.util.ArrayList;
import java.util.Arrays;

public class GamePlayClient {

    private ServerFacade server;

    public GamePlayClient(String serverUrl) {
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
