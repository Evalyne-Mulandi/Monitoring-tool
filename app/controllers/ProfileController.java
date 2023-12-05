package controllers;

import play.mvc.*;

import java.util.Optional;

import play.libs.ws.WSClient;

import java.util.concurrent.CompletionStage;

import play.libs.Json;
import com.fasterxml.jackson.databind.JsonNode;

import javax.inject.Inject;

public class ProfileController extends Controller {

    private final WSClient ws;




    @Inject
    public ProfileController(WSClient ws) {
        this.ws = ws;

    }

    public CompletionStage<Result> fetchUser(String username) {
        String apiUrl = "https://jackal-modern-javelin.ngrok-free.app/user_operations_activation_tool.php/";
        apiUrl += "?action=fetchUser&username=" + username;
        return sendGetRequest(apiUrl, "fetchUser", username);
    }

    private CompletionStage<Result> sendGetRequest(String apiUrl, String action, String username) {
        return ws.url(apiUrl)
                .addQueryParameter("action", action)
                .get()
                .thenApplyAsync(response -> {
                    String responseBody = response.getBody();
                    JsonNode userResponse = Json.parse(responseBody);
                    System.out.println(userResponse);
                    return ok(views.html.Profile.render(Json.toJson(userResponse),username ));
                });
    }
    private void handleResponse(play.libs.ws.WSResponse response) {
        System.out.println("Received response from the API");
        System.out.println("Response Status Code: " + response.getStatus());

        String responseBody = response.getBody();
        if (responseBody != null) {
            System.out.println("Response Body Length: " + responseBody.length());
            if (!responseBody.isEmpty()) {
                System.out.println("Response: " + responseBody);
            } else {
                System.out.println("Response Body is empty.");
            }
        } else {
            System.out.println("Response Body is null.");
        }
    }

    public CompletionStage<Result> Profile(Http.Request request) {
        Optional<String> usernameOptional = request.session().get("username");
        String loggedUsername = usernameOptional.orElse("Guest");
        return fetchUser(loggedUsername);
    }
}
