package handler;

import model.ClearResult;
import service.ClearService;
import spark.*;

public class ClearHandler implements Handler {

    private final ClearService clearService;

    public ClearHandler() {
        clearService = new ClearService();
    }

    @SuppressWarnings("unused")
    public Object clearHandler(Request req, Response res) { //noinspection UnusedParameter

        ClearResult result = clearService.clear();

        res.status(200);
        return gson.toJson(result);
    }
}
