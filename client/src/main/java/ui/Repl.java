package ui;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl {

    private String authToken;
    private ClearClient clearClient;
    private UserClient userClient;
    private GameClient gameClient;

    public Repl(String serverUrl) {
        authToken = null;
        clearClient = new ClearClient(serverUrl);
        userClient = new UserClient(serverUrl);
        gameClient = new GameClient(serverUrl);
    }

    public void run() {
        System.out.println(ERASE_SCREEN + SET_TEXT_COLOR_BLACK + BLACK_QUEEN +
                " Welcome to Chess. Sign in to start. " + BLACK_QUEEN);
        System.out.print(clearClient.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
    }

    public void loggedIn() {

    }

    public void gameplay() {

    }
}
