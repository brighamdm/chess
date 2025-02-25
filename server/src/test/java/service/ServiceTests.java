package service;

import model.RegisterRequest;
import model.RegisterResult;
import org.junit.jupiter.api.*;

public class ServiceTests {

    @BeforeEach
    void setUp() {
        ClearService clearService = new ClearService();
        GameService gameService = new GameService();
        UserService userService = new UserService();

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
