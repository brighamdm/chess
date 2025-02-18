package service;

import chess.ChessGame;
import model.AuthData;
import model.CreateRequest;
import model.CreateResult;
import model.GameData;

import java.util.concurrent.ThreadLocalRandom;

import static dataaccess.AuthDAO.getAuth;
import static dataaccess.GameDAO.createGame;
import static dataaccess.GameDAO.getGame;

public class GameService implements Service {

    public CreateResult create(CreateRequest createRequest) {
        AuthData auth = getAuth(createRequest.authToken());
        if (auth != null) {
            int gameID = ThreadLocalRandom.current().nextInt(1000, 10000);
            GameData game = getGame(gameID);
            if (game == null) {
                game = new GameData(gameID,
                        null, null,
                        createRequest.gameName(),
                        new ChessGame());
                createGame(game);

                return new CreateResult(null, gameID);
            } else {
                return new CreateResult("Error: ", -1);
            }
        } else {
            return new CreateResult("Error: ", -1);
        }
    }


}
