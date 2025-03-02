package dataaccess;

import model.GameData;

import java.sql.SQLException;
import java.util.Collection;

public interface GameDAO {

    static void clear() throws DataAccessException {
        var statement = "TRUNCATE TABLE game";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    static void createGame(GameData newGame) {
        GAMES.add(newGame);
    }

    static GameData getGame(int gameID) {
        GameData found = null;
        for (GameData u : GAMES) {
            if (u.gameID() == gameID) {
                found = u;
                break;
            }
        }
        return found;
    }

    static Collection<GameData> listGames() {
        return GAMES;
    }

    static void updateGame(GameData updatedGame) {
        for (int i = 0; i < GAMES.size(); i++) {
            if (GAMES.get(i).gameID() == updatedGame.gameID()) {
                GAMES.set(i, updatedGame);
                break;
            }
        }
    }
}
