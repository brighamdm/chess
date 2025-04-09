package websocket.commands;

public class ConnectCommand extends UserGameCommand {
    private final int state;

    public ConnectCommand(CommandType commandType, String authToken, Integer gameID, int state) {
        super(commandType, authToken, gameID);
        this.state = state;
    }

    public int getState() {
        return state;
    }
}
