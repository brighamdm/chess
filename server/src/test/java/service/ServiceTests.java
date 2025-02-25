package service;

import model.*;
import org.junit.jupiter.api.*;

public class ServiceTests {

    private static ClearService clearService;
    private static GameService gameService;
    private static UserService userService;

    @BeforeAll
    static void init() {
        clearService = new ClearService();
        gameService = new GameService();
        userService = new UserService();
    }

    @BeforeEach
    void setUp() {
        clearService.clear();
    }

    @Test
    public void registerSuccess() throws UnavailableException, BadRequestException {
        RegisterResult result = userService.register(new RegisterRequest("bm888",
                "brickwall", "bm888@byu.edu"));
        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.authToken());
        Assertions.assertEquals("bm888", result.username());
    }

    @Test
    public void registerFail() throws UnavailableException, BadRequestException {
        RegisterResult result = userService.register(new RegisterRequest("bm888",
                "brickwall", "bm888@byu.edu"));
        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.authToken());
        Assertions.assertEquals("bm888", result.username());

        Assertions.assertThrows(UnavailableException.class, () ->
                userService.register(new RegisterRequest("bm888",
                        "newpassword", "bm888@byu.edu")));

        Assertions.assertThrows(BadRequestException.class, () ->
                userService.register(new RegisterRequest("loser",
                        "password", null)));
    }

    @Test
    public void logoutSuccess() throws UnavailableException, BadRequestException, UnauthorizedException {
        RegisterResult registerResult = userService.register(new RegisterRequest("bm888",
                "brickwall", "bm888@byu.edu"));

        LogoutResult logoutResult = userService.logout(new LogoutRequest(registerResult.authToken()));
        Assertions.assertNotNull(logoutResult);
    }

    @Test
    public void logoutFail() throws UnavailableException, BadRequestException, UnauthorizedException {
        RegisterResult registerResult = userService.register(new RegisterRequest("bm888",
                "brickwall", "bm888@byu.edu"));

        Assertions.assertThrows(BadRequestException.class, () -> userService.logout(null));
    }

    @Test
    public void loginSuccess() throws UnavailableException, BadRequestException, UnauthorizedException {
        RegisterResult registerResult = userService.register(new RegisterRequest("bm888",
                "brickwall", "bm888@byu.edu"));

        LogoutResult logoutResult = userService.logout(new LogoutRequest(registerResult.authToken()));

        LoginResult result = userService.login(new LoginRequest("bm888", "brickwall"));
        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.authToken());
        Assertions.assertEquals("bm888", result.username());
    }

    @Test
    public void createSuccess() throws UnauthorizedException, BadRequestException, UnavailableException {
        RegisterResult registerResult = userService.register(new RegisterRequest("bm888",
                "brickwall", "bm888@byu.edu"));

        CreateResult result = gameService.create(new CreateRequest("newGame", registerResult.authToken()));
        Assertions.assertNotNull(result);
        Assertions.assertNotEquals(0, result.gameID());
    }

}
