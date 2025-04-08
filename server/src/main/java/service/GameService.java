package service;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.*;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.*;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

import static dataaccess.AuthDAO.getAuth;
import static dataaccess.GameDAO.*;

public class GameService implements Service {

    public boolean validGameID(int gameID) throws DataAccessException {
        return (GameDAO.getGame(gameID) != null);
    }

    public ChessGame getGame(String authToken, int gameID) throws BadRequestException, UnauthorizedException, DataAccessException {
        if (authExists(authToken)) {
            GameData gameData = GameDAO.getGame(gameID);
            if (gameData != null) {
                return gameData.game();
            } else {
                throw new BadRequestException("Bad Request");
            }
        } else {
            throw new UnauthorizedException("Unauthorized");
        }
    }

    public GameData move(String authToken, int gameID, ChessMove move) throws DataAccessException, BadRequestException, UnauthorizedException {
        if (move == null) {
            throw new BadRequestException("Invalid Move");
        }

        if (authExists(authToken)) {
            GameData game = GameDAO.getGame(gameID);
            if (game != null) {
                if (!game.over()) {
                    ChessGame chessGame = game.game();
                    try {
                        chessGame.makeMove(move);
                    } catch (InvalidMoveException e) {
                        throw new BadRequestException("Invalid Move");
                    }
                    GameData newGame = new GameData(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName(), chessGame,
                            game.game().isInCheckmate(game.game().getTeamTurn()) || game.game().isInStalemate(game.game().getTeamTurn()));
                    updateGame(newGame);
                    return newGame;
                } else {
                    throw new BadRequestException("Chess game is over.");
                }
            } else {
                throw new BadRequestException("Invalid game.");
            }
        } else {
            throw new UnauthorizedException("Unauthorized");
        }
    }

    public void leave(String authToken, int gameID) throws DataAccessException, BadRequestException, UnauthorizedException {
        if ((authToken == null)) {
            throw new BadRequestException("Bad Request");
        }

        AuthData authData = getAuth(authToken);
        if (authData != null) {
            GameData gameData = GameDAO.getGame(gameID);
            if (gameData != null) {
                updateGame(new GameData(gameData.gameID(),
                        (Objects.equals(gameData.whiteUsername(), authData.username())) ? null : gameData.whiteUsername(),
                        (Objects.equals(gameData.blackUsername(), authData.username())) ? null : gameData.blackUsername(),
                        gameData.gameName(), gameData.game(), gameData.over()));
            } else {
                throw new BadRequestException("Bad Request");
            }
        } else {
            throw new UnauthorizedException("Unauthorized");
        }
    }

    public CreateResult create(CreateRequest createRequest)
            throws UnauthorizedException, BadRequestException, DataAccessException {
        if (createRequest.authToken() == null ||
                createRequest.gameName() == null) {
            throw new BadRequestException("Bad Request");
        }

        if (authExists(createRequest.authToken())) {
            int gameID;
            while (true) {
                gameID = ThreadLocalRandom.current().nextInt(1000, 10000);
                GameData game = GameDAO.getGame(gameID);
                if (game == null) {
                    game = new GameData(gameID,
                            null, null,
                            createRequest.gameName(),
                            new ChessGame(),
                            false);
                    createGame(game);

                    break;
                }
            }
            return new CreateResult(gameID);
        } else {
            throw new UnauthorizedException("Unauthorized");
        }
    }

    public JoinResult join(JoinRequest joinRequest)
            throws UnauthorizedException, UnavailableException, BadRequestException, DataAccessException {
        if (joinRequest.authToken() == null ||
                joinRequest.gameID() == 0 ||
                joinRequest.playerColor() == null) {
            throw new BadRequestException("Bad Request");
        }

        AuthData auth = getAuth(joinRequest.authToken());
        if (auth != null) {
            GameData game = GameDAO.getGame(joinRequest.gameID());
            if (game != null) {
                if (joinRequest.playerColor().equals("WHITE")) {
                    if (game.whiteUsername() == null) {
                        updateGame(new GameData(game.gameID(),
                                auth.username(),
                                game.blackUsername(),
                                game.gameName(),
                                game.game(),
                                game.over()));

                        return new JoinResult();
                    } else {
                        throw new UnavailableException("Already Taken");
                    }
                } else if (joinRequest.playerColor().equals("BLACK")) {
                    if (game.blackUsername() == null) {
                        updateGame(new GameData(game.gameID(),
                                game.whiteUsername(),
                                auth.username(),
                                game.gameName(),
                                game.game(),
                                game.over()));

                        return new JoinResult();
                    } else {
                        throw new UnavailableException("Already Taken");
                    }
                } else {
                    throw new BadRequestException("Bad Request");
                }
            } else {
                throw new BadRequestException("Bad Request");
            }
        } else {
            throw new UnauthorizedException("Unauthorized");
        }
    }

    public ListResult list(ListRequest listRequest)
            throws UnauthorizedException, DataAccessException {
        if (authExists(listRequest.authToken())) {
            return new ListResult((List<GameData>) listGames());
        } else {
            throw new UnauthorizedException("Unauthorized");
        }
    }
}
