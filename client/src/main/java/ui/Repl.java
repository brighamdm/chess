package ui;

import exception.ResponseException;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl {

    private String authToken;
    private final StartClient startClient;
    private final LoggedInClient loggedInClient;
    private final GamePlayClient gamePlayClient;
    private boolean watching;
    private boolean team;
    private int gameID;
    private String mode;

    public Repl(String serverUrl) {
        authToken = null;
        startClient = new StartClient(serverUrl);
        loggedInClient = new LoggedInClient(serverUrl);
        gamePlayClient = new GamePlayClient(serverUrl);

        // Uncomment if wanting to clear database
//        try {
//            startClient.clear();
//        } catch (ResponseException ex) {
//            System.out.println("Failed to clear database");
//        }
    }

    public void run() {
        mode = "Chess Login";
        System.out.println(ERASE_SCREEN + SET_TEXT_COLOR_LIGHT_GREY + BLACK_QUEEN +
                " Welcome to Chess. Sign in to start. " + BLACK_QUEEN);
        System.out.print(startClient.help());
        System.out.println();

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals(SET_TEXT_COLOR_YELLOW + "Goodbye!")) {
            printPrompt();
            String line = scanner.nextLine();
            StringBuilder auth = new StringBuilder();
            result = startClient.eval(line, auth);
            System.out.println(SET_TEXT_COLOR_RED + result);

            if (!auth.isEmpty()) {
                authToken = auth.toString();
                loggedIn();
                mode = "Chess Login";
            }
        }
    }

    public void loggedIn() {
        mode = "Chess";

        // System.out.print(loggedInClient.help());
        // System.out.println();
        loggedInClient.initializeList(authToken);

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals(SET_TEXT_COLOR_YELLOW + "Logged out.")) {
            printPrompt();
            String line = scanner.nextLine();
            StringBuilder id = new StringBuilder();
            result = loggedInClient.eval(line, authToken, id);

            if (!id.isEmpty()) {
                gameID = Integer.parseInt(id.toString());
                if (result.equals("watching")) {
                    watching = true;
                    team = true;
                } else {
                    watching = false;
                    team = result.equals("WHITE");
                }
                gameplay();
                mode = "Chess";
            } else {
                System.out.println(SET_TEXT_COLOR_RED + result);
            }
        }
    }

    public void gameplay() {
        mode = "Chess Game";

        gamePlayClient.initializeGame(authToken, team, gameID);
        try {
            gamePlayClient.draw();
        } catch (ResponseException e) {
            System.out.println("Failed Initial Draw");
        }

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals(SET_TEXT_COLOR_YELLOW + "Leaving gameplay.")) {
            printPrompt();
            String line = scanner.nextLine();
            result = gamePlayClient.eval(line, authToken);
            System.out.println(SET_TEXT_COLOR_RED + result);
        }
    }

    private void printPrompt() {
        System.out.print(SET_TEXT_COLOR_GREEN + mode + " >>> ");
    }
}
