package controllers;

import play.mvc.*;
import play.mvc.Http;
import play.mvc.Http.Session;
import java.util.Optional;
import play.libs.ws.WSClient;
import java.net.http.HttpRequest;
import java.util.concurrent.CompletionStage;
import play.libs.Json;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javax.inject.Inject;
/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class  sessionsController extends Controller {

    private final WSClient ws;


    @Inject
    public  sessionsController(WSClient ws) {
        this.ws = ws;

    }

    public CompletionStage<Result> fetchAllAuditTrail(Http.Request request) {
        String apiUrl = "https://jackal-modern-javelin.ngrok-free.app/fetch_sessions_monitoring_tool.php/";
        String action = "fetchSessionMonitoring";
        Optional<String> mssidnOptional = request.session().get("mssidn");
        String mssidn =  mssidnOptional.orElse("");
        Optional<String> sessionIdOptional = request.session().get("sessionId");
        String sessionId =  sessionIdOptional.orElse("");
        return ws.url(apiUrl)
                .addQueryParameter("action", action)
                .get()
                .thenApplyAsync(response -> {
                    String responseBody = response.getBody();

                    // Print the API response for debugging
                    System.out.println("API Response: " + responseBody);

                    JsonNode auditTrailResponse = Json.parse(responseBody);

                    return ok(views.html.sessions.render(Json.toJson(auditTrailResponse), mssidn, sessionId));
                });
    }


    public CompletionStage<Result> sessions(Http.Request request) {
        return fetchAllAuditTrail(request);

    }

}
