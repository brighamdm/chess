package ui;

import serverfacade.ServerFacade;

public class StartClient {

    private ServerFacade server;

    public StartClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
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
