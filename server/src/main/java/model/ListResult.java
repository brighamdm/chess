package model;

import java.util.List;

public record ListResult(String message,
                         List<GameData> games) {
}
