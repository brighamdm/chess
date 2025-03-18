package ui;

import serverfacade.ServerFacade;

public class GamePlayClient {

    private ServerFacade server;

    public GamePlayClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
    }
}
