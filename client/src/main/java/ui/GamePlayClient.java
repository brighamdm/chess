package ui;

import exception.ResponseException;
import model.GameData;
import model.ListRequest;
import model.ListResult;
import serverfacade.ServerFacade;

import java.util.ArrayList;
import java.util.Arrays;

import static ui.EscapeSequences.SET_TEXT_COLOR_YELLOW;

public class GamePlayClient {

    private final ServerFacade server;
    private int team;
    private int gameID;
    private GameData game;

    public GamePlayClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
        team = -1;
    }

    public void initializeGame(String authToken, boolean color, int id) {
        team = color ? 1 : 0;
        gameID = id;
        if (authToken != null) {
            try {
                ListResult listResult = server.list(new ListRequest(authToken));
                game = listResult.games().get(id - 1);
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


        return null;
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
