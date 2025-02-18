package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.ClearResult;

public class ClearService implements Service {

    public ClearResult clear() {
        GameDAO.clear();
        UserDAO.clear();
        AuthDAO.clear();

        return new ClearResult(null);
    }
}
