package ui;

import com.LoginRequest;
import com.LoginResult;
import com.RegisterRequest;
import com.RegisterResult;
import exception.ResponseException;
import serverfacade.ServerFacade;

import java.util.Arrays;

import static ui.EscapeSequences.*;

public class StartClient {

    private final ServerFacade server;

    public StartClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
    }

    public String eval(String line, StringBuilder authToken) {
        try {
            var tokens = line.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "l", "login" -> login(authToken, params);
                case "r", "register" -> register(authToken, params);
                case "q", "quit" -> quit();
                case "h", "help" -> help();
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String login(StringBuilder authToken, String... params) throws ResponseException {
        if (params.length >= 2) {
            try {
                var username = params[0];
                var password = params[1];
                LoginResult loginResult = server.login(new LoginRequest(username, password));
                authToken.append(loginResult.authToken());
                return SET_TEXT_COLOR_BLUE + "Successfully logged in.\n";
            } catch (Exception ex) {
                throw new ResponseException(400, ex.getMessage() + "\nExpected: <USERNAME> <PASSWORD>\n");
            }
        }
        throw new ResponseException(400, "Expected: <USERNAME> <PASSWORD>\n");
    }

    public String register(StringBuilder authToken, String... params) throws ResponseException {
        if (params.length >= 3) {
            try {
                var username = params[0];
                var password = params[1];
                var email = params[2];
                RegisterResult registerResult = server.register(new RegisterRequest(username, password, email));
                authToken.append(registerResult.authToken());
                return SET_TEXT_COLOR_BLUE + "Successfully registered.\n";
            } catch (Exception ex) {
                throw new ResponseException(400, ex.getMessage() + "\nExpected: <USERNAME> <PASSWORD> <EMAIL>\n");
            }
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

    public void clear() throws ResponseException {
        server.clear();
    }
}
