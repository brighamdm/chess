package handler;

import model.ClearResult;
import service.ClearService;
import spark.*;

public class ClearHandler implements Handler {

    public Object clearHandler(Request _req, Response res) {

        ClearService service = new ClearService();
        ClearResult result = service.clear();

        res.status(200);
        return gson.toJson(result);
    }
}
