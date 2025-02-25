package service;

import chess.ChessGame;
import model.*;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static dataaccess.AuthDAO.getAuth;
import static dataaccess.GameDAO.*;

public class GameService implements Service {

    public CreateResult create(CreateRequest createRequest) {
        if (authExists(createRequest.authToken())) {
            int gameID;
            while (true) {
                gameID = ThreadLocalRandom.current().nextInt(1000, 10000);
                GameData game = getGame(gameID);
                if (game == null) {
                    game = new GameData(gameID,
                            null, null,
                            createRequest.gameName(),
                            new ChessGame());
                    createGame(game);

                    break;
                }
            }
            return new CreateResult(gameID);
        } else {
            return new CreateResult("Error: Invalid AuthToken", -1);
        }
    }

    public JoinResult join(JoinRequest joinRequest) {
        AuthData auth = getAuth(joinRequest.authToken());
        if (auth != null) {
            GameData game = getGame(joinRequest.gameID());
            if (game != null) {
                if (joinRequest.playerColor().equals("WHITE")) {
                    updateGame(new GameData(game.gameID(),
                            auth.username(),
                            game.blackUsername(),
                            game.gameName(),
                            game.game()));

                    return new JoinResult(null);
                } else if (joinRequest.playerColor().equals("BLACK")) {
                    updateGame(new GameData(game.gameID(),
                            game.whiteUsername(),
                            auth.username(),
                            game.gameName(),
                            game.game()));

                    return new JoinResult(null);
                } else {
                    return new JoinResult("Error: Invalid Player Color");
                }
            } else {
                return new JoinResult("Error: Game does not exist");
            }
        } else {
            return new JoinResult("Error: Invalid AuthToken");
        }
    }

    public ListResult list(LogoutRequest listRequest) {
        if (authExists(listRequest.authToken())) {
            return new ListResult(null,
                    (List<GameData>) listGames());
        } else {
            return new ListResult("Error: Invalid AuthToken", null);
        }
    }
}
