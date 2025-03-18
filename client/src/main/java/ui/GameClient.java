package ui;

import serverfacade.ServerFacade;

public class GameClient {

    private ServerFacade server;

    public GameClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
    }
}
