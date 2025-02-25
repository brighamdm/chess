package service;

import chess.ChessGame;
import model.*;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static dataaccess.AuthDAO.getAuth;
import static dataaccess.GameDAO.*;

public class GameService implements Service {

    public CreateResult create(CreateRequest createRequest)
            throws UnauthorizedException, BadRequestException {
        if (createRequest.authToken() == null ||
                createRequest.gameName() == null) {
            throw new BadRequestException("Error: bad request");
        }

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
            throw new UnauthorizedException("Error: unauthorized");
        }
    }

    public JoinResult join(JoinRequest joinRequest)
            throws UnauthorizedException, UnavailableException, BadRequestException {
        if (joinRequest.authToken() == null ||
                joinRequest.gameID() == 0 ||
                joinRequest.playerColor() == null) {
            throw new BadRequestException("Error: bad request");
        }

        AuthData auth = getAuth(joinRequest.authToken());
        if (auth != null) {
            GameData game = getGame(joinRequest.gameID());
            if (game != null) {
                if (joinRequest.playerColor().equals("WHITE")) {
                    if (game.whiteUsername() == null) {
                        updateGame(new GameData(game.gameID(),
                                auth.username(),
                                game.blackUsername(),
                                game.gameName(),
                                game.game()));

                        return new JoinResult();
                    } else {
                        throw new UnavailableException("Error: already taken");
                    }
                } else if (joinRequest.playerColor().equals("BLACK")) {
                    if (game.blackUsername() == null) {
                        updateGame(new GameData(game.gameID(),
                                game.whiteUsername(),
                                auth.username(),
                                game.gameName(),
                                game.game()));

                        return new JoinResult();
                    } else {
                        throw new UnavailableException("Error: already taken");
                    }
                } else {
                    throw new BadRequestException("Error: bad request");
                }
            } else {
                throw new BadRequestException("Error: bad request");
            }
        } else {
            throw new UnauthorizedException("Error: unauthorized");
        }
    }

    public ListResult list(LogoutRequest listRequest) throws UnauthorizedException {
        if (authExists(listRequest.authToken())) {
            return new ListResult((List<GameData>) listGames());
        } else {
            throw new UnauthorizedException("Error: unauthorized");
        }
    }
}
