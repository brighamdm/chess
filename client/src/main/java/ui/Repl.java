package ui;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl {

    private String authToken;
    private StartClient startClient;
    private LoggedInClient loggedInClient;
    private GamePlayClient gamePlayClient;

    public Repl(String serverUrl) {
        authToken = null;
        startClient = new StartClient(serverUrl);
        LoggedInClient = new LoggedInClient(serverUrl);
        gamePlayClient = new GamePlayClient(serverUrl);
    }

    public void run() {
        System.out.println(ERASE_SCREEN + SET_TEXT_COLOR_BLACK + BLACK_QUEEN +
                " Welcome to Chess. Sign in to start. " + BLACK_QUEEN);
        System.out.print(startClient.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            String line = scanner.nextLine();

            try {
                result = client.eval
            }
        }
    }

    public void loggedIn() {

    }

    public void gameplay() {

    }
}
