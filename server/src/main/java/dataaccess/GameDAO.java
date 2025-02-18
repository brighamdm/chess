package dataaccess;

import model.GameData;

import java.util.ArrayList;
import java.util.Collection;

public interface GameDAO {

    ArrayList<GameData> games = new ArrayList<>();

    static void clear() {
        games.clear();
    }

    static void createGame(GameData newGame) {
        games.add(newGame);
    }

    static GameData getGame(int gameID) {
        GameData found = null;
        for (GameData u : games) {
            if (u.gameID() == gameID) {
                found = u;
                break;
            }
        }
        return found;
    }

    static Collection<GameData> listGames() {
        return games;
    }

    static void updateGame(GameData updatedGame) {
        for (int i = 0; i < games.size(); i++) {
            if (games.get(i).gameID() == updatedGame.gameID()) {
                games.set(i, updatedGame);
                break;
            }
        }
    }
}
