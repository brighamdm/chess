package dataaccess;

public interface DataAccess {

    public final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS auth (
              `id` INT NOT NULL AUTO_INCREMENT,
              `authToken` VARCHAR(256) NOT NULL,
              `username` VARCHAR(256) NOT NULL,
               PRIMARY KEY (`id`),
               INDEX(`authToken`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_cs;
            
            CREATE TABLE IF NOT EXISTS user (
              `id` INT NOT NULL AUTO_INCREMENT,
              `username` VARCHAR(256) NOT NULL,
              `password` VARCHAR(256) NOT NULL,
              `email` VARCHAR(256) NOT NULL,
               PRIMARY KEY (`id`),
               INDEX (`username`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_cs;
            
            CREATE TABLE IF NOT EXISTS game (
              `id` INT NOT NULL AUTO_INCREMENT,
              `gameID` INT NOT NULL,
              `gameJSON` VARCHAR(256) NOT NULL,
               PRIMARY KEY (`id`),
               INDEX (`gameID`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_cs;
            """
    };

    static void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (Exception ex) {
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }
}
