package ui;

import serverfacade.ServerFacade;

public class UserClient {

    private ServerFacade server;

    public UserClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
    }
}
