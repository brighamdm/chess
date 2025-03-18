package serverfacade;

import com.google.gson.Gson;
import exception.ResponseException;
import model.*;

import java.io.*;
import java.net.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ServerFacade {

    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public ClearResult clear() throws ResponseException {
        var path = "/db";
        return this.makeRequest("DELETE", path, null, ClearResult.class);
    }

    public RegisterResult register(RegisterRequest registerRequest) throws ResponseException {
        var path = "/user";
        return this.makeRequest("POST", path, registerRequest, RegisterResult.class);
    }

    public LoginResult login(LoginRequest loginRequest) throws ResponseException {
        var path = "/session";
        return this.makeRequest("POST", path, loginRequest, LoginResult.class);
    }

    public LogoutResult logout(LogoutRequest logoutRequest) throws ResponseException {
        var path = "/session";
        return this.makeRequest("DELETE", path, logoutRequest, LogoutResult.class);
    }

    public ListResult list(ListRequest listRequest) throws ResponseException {
        var path = "/game";
        return this.makeRequest("GET", path, listRequest, ListResult.class);
    }

    public CreateResult create(CreateRequest createRequest) throws ResponseException {
        var path = "/game";
        return this.makeRequest("POST", path, createRequest, CreateResult.class);
    }

    public JoinResult join(JoinRequest joinRequest) throws ResponseException {
        var path = "/game";
        return this.makeRequest("PUT", path, joinRequest, JoinResult.class);
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            if (Objects.equals(method, "GET")) {
                ListRequest listRequest = (ListRequest) request;
                http.setRequestProperty("Authorization", listRequest.authToken());
            } else {
                writeBody(request, http);
            }

            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (ResponseException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }


    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            try (InputStream respErr = http.getErrorStream()) {
                if (respErr != null) {
                    throw ResponseException.fromJson(respErr);
                }
            }

            throw new ResponseException(status, "other failure: " + status);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);

                // DEBUG: Print the raw response
                String rawJson = new BufferedReader(reader).lines().collect(Collectors.joining("\n"));

                if (responseClass != null) {
                    response = new Gson().fromJson(rawJson, responseClass);
                }
            }
        }
        return response;
    }


    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
