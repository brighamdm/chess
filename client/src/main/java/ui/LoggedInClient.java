package ui;

import serverfacade.ServerFacade;

public class LoggedInClient {

    private ServerFacade server;

    public LoggedInClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
    }
}
