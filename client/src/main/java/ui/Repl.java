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
        loggedInClient = new LoggedInClient(serverUrl);
        gamePlayClient = new GamePlayClient(serverUrl);
    }

    public void run() {
        System.out.println(ERASE_SCREEN + SET_TEXT_COLOR_LIGHT_GREY + BLACK_QUEEN +
                " Welcome to Chess. Sign in to start. " + BLACK_QUEEN);
        System.out.print(startClient.help());
        System.out.println();

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals(SET_TEXT_COLOR_YELLOW + "Goodbye!")) {
            printPrompt();
            String line = scanner.nextLine();
            System.out.println();
            result = startClient.eval(line);
            System.out.println(SET_TEXT_COLOR_RED + result);
        }
    }

    public void loggedIn() {

    }

    public void gameplay() {

    }

    private void printPrompt() {
        System.out.print(SET_TEXT_COLOR_GREEN + ">>> ");
    }
}
