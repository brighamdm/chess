package model;

public record LoginResult(String message,
                          String username,
                          String authToken) {
}
