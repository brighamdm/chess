package websocket;

import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

public interface NotificationHandler {
    void notify(ServerMessage notification, NotificationMessage notificationMessage, LoadGameMessage loadGameMessage, ErrorMessage errorMessage);
}