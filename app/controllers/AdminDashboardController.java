package controllers;

import play.mvc.*;
import play.mvc.Http;
import play.mvc.Http.Session;

import java.util.List;
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
public class AdminDashboardController extends Controller {

    private final WSClient ws;


    @Inject
    public  AdminDashboardController(WSClient ws) {
        this.ws = ws;

    }

    public CompletionStage<Result> fetchAllAuditTrail(Http.Request request) {
        String apiUrl = "https://jackal-modern-javelin.ngrok-free.app/fetch_payments_monitoring_tool.php/";


        // Define an array of valid actions
        String[] validActions = {"fetchAllPayments", "failedMetrics", "succeededMetrics"};

        // Get the action from the request query parameter or default to "fetchAllPayments"
        String action = Optional.ofNullable(request.getQueryString("action"))
                .filter(a -> List.of(validActions).contains(a))
                .orElse("fetchAllPayments");


        Optional<String>  serviceCodeOptional = request.session().get("serviceCode");
        String  serviceCode =  serviceCodeOptional.orElse("");
        Optional<String>  mpesaRefOptional = request.session().get("mpesaRef");
        String  mpesaRef =  mpesaRefOptional.orElse("");
        return ws.url(apiUrl)
                .addQueryParameter("action", action)
                .get()
                .thenApplyAsync(response -> {
                    String responseBody = response.getBody();
                    // Print the API response for debugging
                    System.out.println("API Response: " + responseBody);

                    JsonNode auditTrailResponse = Json.parse(responseBody);
                    return ok(views.html.adminDashboard.render(Json.toJson(auditTrailResponse), mpesaRef, serviceCode));
                });
    }

    public CompletionStage<Result>  adminDashboard(Http.Request request) {
        return fetchAllAuditTrail(request);

    }

}
