package ui;

import com.google.gson.Gson;
import exception.ResponseException;
import websocket.NotificationHandler;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.util.Scanner;

import static ui.EscapeSequences.*;
import static websocket.messages.ServerMessage.ServerMessageType.*;

public class Repl implements NotificationHandler {

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
        gamePlayClient = new GamePlayClient(serverUrl, this);

        // Uncomment if wanting to clear database
        try {
            startClient.clear();
        } catch (ResponseException ex) {
            System.out.println("Failed to clear database");
        }
    }

    public void run() {
        mode = "Chess Login";
        System.out.println("\n" + ERASE_SCREEN + SET_TEXT_COLOR_LIGHT_GREY + BLACK_QUEEN +
                " Welcome to Chess. Type help to get started. " + BLACK_QUEEN);
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
            gamePlayClient.connect(authToken);
        } catch (ResponseException e) {
            System.out.println("Failed to Connect");
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

    @Override
    public void notify(ServerMessage msg, NotificationMessage notificationMessage, LoadGameMessage loadGameMessage, ErrorMessage errorMessage) {
        System.out.print("\r" + ERASE_LINE);
        if (msg.getServerMessageType() == LOAD_GAME) {
            gamePlayClient.setGame(loadGameMessage.getGame());
            try {
                gamePlayClient.draw();
            } catch (ResponseException e) {
                System.out.println("Failed Draw");
            }
        } else if (msg.getServerMessageType() == NOTIFICATION) {
            System.out.println(SET_TEXT_COLOR_BLUE + notificationMessage.getMessage());
        } else if (msg.getServerMessageType() == ERROR) {
            System.out.println(SET_TEXT_COLOR_RED + errorMessage.getErrorMessage());
        }
        printPrompt();
        System.out.flush();
    }
}
