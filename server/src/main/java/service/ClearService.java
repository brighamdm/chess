package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import com.ClearResult;

public class ClearService implements Service {

    public ClearResult clear() throws DataAccessException {
        GameDAO.clear();
        UserDAO.clear();
        AuthDAO.clear();

        return new ClearResult();
    }
}
