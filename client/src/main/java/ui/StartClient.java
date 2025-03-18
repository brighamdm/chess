package ui;

import exception.ResponseException;
import serverfacade.ServerFacade;

import java.util.Arrays;

import static ui.EscapeSequences.SET_TEXT_COLOR_YELLOW;

public class StartClient {

    private ServerFacade server;

    public StartClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
    }

    public String eval(String line) {
        try {
            var tokens = line.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "l", "login" -> login(params);
                case "r", "register" -> register(params);
                case "q", "quit" -> quit();
                case "h", "help" -> help();
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String login(String... params) throws ResponseException {
        if (params.length >= 2) {
            return "Valid login";
        }
        throw new ResponseException(400, "Expected: <USERNAME> <PASSWORD>\n");
    }

    public String register(String... params) throws ResponseException {
        if (params.length >= 3) {
            return "Valid register";
        }
        throw new ResponseException(400, "Expected: <USERNAME> <PASSWORD> <EMAIL>\n");
    }

    public String quit() {
        return SET_TEXT_COLOR_YELLOW + "Goodbye!";
    }

    public String help() {
        return """
                Options:
                Login as an existing user: "l", "login" <USERNAME> <PASSWORD>
                Register a new user: "r", "register" <USERNAME <PASSWORD> <EMAIL>
                Exit the program: "q", "quit"
                Print this message: "h", "help"
                """;
    }
}
