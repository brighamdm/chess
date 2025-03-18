package ui;

import serverfacade.ServerFacade;

public class ClearClient {

    private ServerFacade server;

    public ClearClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
    }

}
