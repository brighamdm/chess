package service;

import model.RegisterRequest;
import model.RegisterResult;
import org.junit.jupiter.api.*;

public class ServiceTests {

    private ClearService clearService;
    private GameService gameService;
    private UserService userService;

    @BeforeEach
    void setUp() {
        clearService = new ClearService();
        gameService = new GameService();
        userService = new UserService();

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
