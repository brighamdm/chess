package service;

import model.GameData;
import model.RegisterRequest;
import model.RegisterResult;
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
}
