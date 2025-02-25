package handler;

import model.ClearResult;
import service.ClearService;
import spark.*;

public class ClearHandler implements Handler {

    public String clearHandler(Request req, Response res) {

        ClearService service = new ClearService();
        ClearResult result = service.clear();

        res.status(200);

        return toJson(result);
    }
}
