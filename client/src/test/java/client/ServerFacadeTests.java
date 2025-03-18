package client;

import exception.ResponseException;
import org.junit.jupiter.api.*;
import server.Server;
import model.*;
import serverfacade.ServerFacade;

public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;
    private static final String serverUrl = "http://localhost:";

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);

        serverFacade = new ServerFacade(serverUrl + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    public void setup() throws ResponseException {
        serverFacade.clear();
    }

    @Test
    public void clear() throws ResponseException {
        ClearResult clear = serverFacade.clear();
        Assertions.assertNotNull(clear);
    }

    @Test
    public void registerSuccess() throws ResponseException {
        RegisterRequest registerRequest = new RegisterRequest("bm888",
                "brickwall", "bm888@byu.edu");
        RegisterResult registerResult = serverFacade.register(registerRequest);
        Assertions.assertNotNull(registerResult);
        Assertions.assertNotNull(registerResult.authToken());
        Assertions.assertEquals("bm888", registerResult.username());
    }

    @Test
    public void registerFail() throws ResponseException {
        RegisterRequest registerRequest = new RegisterRequest("bm888",
                "brickwall", "bm888@byu.edu");
        serverFacade.register(registerRequest);
        registerRequest = new RegisterRequest("bm888", "bm888",
                "bm888@byu.edu");
        RegisterRequest finalRegisterRequest = registerRequest;
        Assertions.assertThrows(ResponseException.class, () -> serverFacade.register(finalRegisterRequest));
    }

    @Test
    public void loginSuccess() throws ResponseException {
        RegisterResult registerResult = serverFacade.register(new RegisterRequest("bm888",
                "brickwall", "bm888@byu.edu"));
        serverFacade.logout(new LogoutRequest(registerResult.authToken()));
        LoginRequest loginRequest = new LoginRequest("bm888", "brickwall");
        LoginResult loginResult = serverFacade.login(loginRequest);
        Assertions.assertNotNull(loginResult);
        Assertions.assertNotNull(loginResult.authToken());
    }

    @Test
    public void loginFail() throws ResponseException {
        RegisterResult registerResult = serverFacade.register(new RegisterRequest("bm888", "brickwall", "bm888@byu.edu"));
        serverFacade.logout(new LogoutRequest(registerResult.authToken()));
        LoginRequest loginRequest = new LoginRequest("bm888", "brickwalll");
        Assertions.assertThrows(ResponseException.class, () -> serverFacade.login(loginRequest));
    }

    @Test
    public void logoutSuccess() throws ResponseException {
        RegisterResult registerResult = serverFacade.register(new RegisterRequest("bm888", "brickwall", "bm888@byu.edu"));
        LogoutResult logoutResult = serverFacade.logout(new LogoutRequest(registerResult.authToken()));
        Assertions.assertNotNull(logoutResult);
    }

    @Test
    public void logoutFail() throws ResponseException {
        RegisterResult registerResult = serverFacade.register(new RegisterRequest("bm888", "brickwall", "bm888@byu.edu"));
        Assertions.assertThrows(ResponseException.class, () -> serverFacade.logout(new LogoutRequest("fakeAuth")));
    }

    @Test
    public void createSuccess() throws ResponseException {
        RegisterResult registerResult = serverFacade.register(new RegisterRequest("bm888", "brickwall", "bm888@byu.edu"));
        CreateResult createResult = serverFacade.create(new CreateRequest("newGame", registerResult.authToken()));
        Assertions.assertNotNull(createResult);
    }

    @Test
    public void createFail() throws ResponseException {
        RegisterResult registerResult = serverFacade.register(new RegisterRequest("bm888", "brickwall", "bm888@byu.edu"));
        Assertions.assertThrows(ResponseException.class, () -> serverFacade.create(new CreateRequest("newGame", "fakeAuth")));
    }

    @Test
    public void joinSuccess() throws ResponseException {
        RegisterResult registerResult = serverFacade.register(new RegisterRequest("bm888", "brickwall", "bm888@byu.edu"));
        CreateResult createResult = serverFacade.create(new CreateRequest("newGame", registerResult.authToken()));
        JoinResult joinResult = serverFacade.join(new JoinRequest("WHITE", createResult.gameID(), registerResult.authToken()));
        Assertions.assertNotNull(joinResult);
    }

    @Test
    public void joinFail() throws ResponseException {
        RegisterResult registerResult = serverFacade.register(new RegisterRequest("bm888", "brickwall", "bm888@byu.edu"));
        CreateResult createResult = serverFacade.create(new CreateRequest("newGame", registerResult.authToken()));
        Assertions.assertThrows(ResponseException.class, () -> serverFacade.join(new JoinRequest("WHITE", 0, registerResult.authToken())));
    }

    @Test
    public void listSuccess() throws ResponseException {
        RegisterResult registerResult = serverFacade.register(new RegisterRequest("bm888", "brickwall", "bm888@byu.edu"));
        serverFacade.create(new CreateRequest("newGame", registerResult.authToken()));
        serverFacade.create(new CreateRequest("otherGame", registerResult.authToken()));
        ListResult listResult = serverFacade.list(new ListRequest(registerResult.authToken()));
        Assertions.assertNotNull(listResult);
        Assertions.assertEquals(2, listResult.games().size());
    }

    @Test
    public void listFail() throws ResponseException {
        RegisterResult registerResult = serverFacade.register(new RegisterRequest("bm888", "brickwall", "bm888@byu.edu"));
        serverFacade.create(new CreateRequest("newGame", registerResult.authToken()));
        serverFacade.create(new CreateRequest("otherGame", registerResult.authToken()));
        Assertions.assertThrows(ResponseException.class, () -> serverFacade.list(new ListRequest("fakeAuth")));
    }
}
