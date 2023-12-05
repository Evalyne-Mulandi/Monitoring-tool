package controllers;

import Forms.Login;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.data.Form;
import play.data.FormFactory;
import play.i18n.MessagesApi;
import play.libs.Json;
import play.libs.ws.WSClient;
import play.libs.ws.WSResponse;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Http.Session;
import javax.inject.Inject;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class loginController extends Controller {
    private final FormFactory formFactory;

    private final WSClient ws;

    private final MessagesApi messages;
    private static String globalSessionToken;

    private static String globalRole;

    @Inject
    public loginController(FormFactory formFactory, MessagesApi messages, WSClient ws) {
        this.formFactory = formFactory;
        this.messages = messages;
        this.ws = ws;
    }



    private static String sha256Hash(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(password.getBytes(StandardCharsets.UTF_8));
            byte[] hashedBytes = md.digest();

            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }

            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            // Handle the exception (e.g., log it) or throw a RuntimeException
            throw new RuntimeException("Error hashing password", e);
        }
    }









    public CompletionStage<Result> userLogin(String username, String password) {
        String apiUrl = "https://jackal-modern-javelin.ngrok-free.app/login_users_operations_activation_tool.php";

        // Hash the password using SHA-256
        String hashedPassword = sha256Hash(password);

        ObjectNode json = Json.newObject();
        json.put("action", "userLogin");
        json.put("username", username);
        json.put("password", hashedPassword);

        return sendPostRequest(apiUrl, json)
                .thenApplyAsync(response -> {
                    JsonNode jsonResponse = Json.parse(response.getBody());
                    if (jsonResponse.has("message") && jsonResponse.get("message").asText().equals("Login successful")) {
                        String sessionToken = extractSessionToken(jsonResponse);
                        saveSessionToken(sessionToken);
                        // Extract and save the role
                        String  globalRole = extractRole(jsonResponse);
                        saveRole(globalRole);

//                        if ("Admin".equals(role)) {
//                            return redirect("/adminDashboard").flashing("success", "Login successful!!");
//                       } else if ("Regular".equals(role)) {
//                            return redirect("/generateCode").flashing("success", "Login successful!!");
//                       }



                        return ok(Json.toJson(response.getBody())).as("application/json");
                    } else if (jsonResponse.has("error")) {
                        String errorMessage = jsonResponse.get("error").asText();
                        return badRequest(errorMessage).as("application/json");
                    } else {
                        String errorMessage = "Invalid Response";
                        return badRequest(errorMessage).as("application/json");
                    }
                });
    }

    private String extractRole(JsonNode jsonResponse){
        return jsonResponse.get("role").asText();
    }

    private void saveRole(String role){
        globalRole = role;
        System.out.println("Saved Role:"+ "" + globalRole);
    }

    public static String getGlobalRole(){
        return globalRole;
    }

    private String extractSessionToken(JsonNode jsonResponse) {
        // Implement logic to extract the session token from the JSON response
        // Replace "sessionToken" with the actual field name in your response
        return jsonResponse.get("sessionToken").asText();
    }

    private void saveSessionToken(String sessionToken) {
        // Implement logic to save the session token (e.g., store it in a database or session)
        globalSessionToken = sessionToken;
        System.out.println("Session Token: " + sessionToken);
        System.out.println("Saved Global Session Token: " + globalSessionToken);
    }

    // Add a method to retrieve the global session token if needed
    public static String getGlobalSessionToken() {
        return globalSessionToken;
    }

    public CompletionStage<Result> userLogout(String sessionToken) {
        String apiUrl = "https://jackal-modern-javelin.ngrok-free.app/login_users_operations_activation_tool.php";

        // Retrieve the global session token directly
//        String sessionToken = getGlobalSessionToken();

        ObjectNode json = Json.newObject();
        json.put("action", "userLogout");
        json.put("sessionToken", sessionToken);

        return sendPostRequest(apiUrl, json)
                .thenApplyAsync(response -> ok(response.getBody()));
    }


    private CompletionStage<WSResponse> sendPostRequest(String apiUrl, ObjectNode json) {
        System.out.println("Sending JSON request to " + apiUrl + ": " + json.toString());

        // Send the POST request
        return ws.url(apiUrl)
                .post(json)
                .thenApplyAsync(response -> {
                    // Print the response
                    System.out.println("Received response from " + apiUrl + ": " + response.getBody());
                    return response;
                });
    }


    private CompletionStage<Result> sendGetRequest(String apiUrl, String action) {
        return ws.url(apiUrl)
                .addQueryParameter("action", action)
                .get()
                .thenApplyAsync(response -> {
                    handleResponse(response);
                    return ok(response.getBody());
                });
    }

    private CompletionStage<Result> sendJsonRequest(String apiUrl, ObjectNode json) {
        return ws.url(apiUrl)
                .post(json)
                .thenApplyAsync(response -> {
                    handleResponse(response);
                    return ok(response.getBody());
                });
    }

    private CompletionStage<Result> sendPostRequest(String apiUrl, String action, String username) {
        return ws.url(apiUrl)
                .addQueryParameter("action", action)
                .post(Json.newObject().put("username", username))
                .thenApplyAsync(response -> {
                    handleResponse(response);
                    return ok(response.getBody());
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



    public CompletionStage<Result> makeAdmin(String username) {
        String apiUrl = "https://jackal-modern-javelin.ngrok-free.app/user_operations_activation_tool.php/";

        ObjectNode json = Json.newObject();
        json.put("action", "makeAdmin");
        json.put("username", username);

        return sendJsonRequest(apiUrl, json);
    }


    //    // Existing login method
//    public Result login(play.mvc.Http.Request request) {
//        Form<Login> loginForm = formFactory.form(Login.class).bindFromRequest(request);
//
//        if (loginForm.hasErrors()) {
//            return badRequest(views.html.login.render(loginForm, request, messages.preferred(request)));
//        }
//
////        // Check if the password is correct
////        if (!isValidPassword(loginForm.get().getPassword())) {
////            List<String> errs = new ArrayList<>();
////            errs.add("Invalid password");
////            String errorMessage = String.join(", ", errs);
////            loginForm = loginForm.withError("validatePassword", errorMessage);
////            return badRequest(views.html.login.render(loginForm, request, messages.preferred(request)));
////        }
//
//
//
//        Login login = loginForm.get();
//
//        String username = login.getUsername();
//        String password = login.getPassword();
//
//        // Call userLogin method asynchronously
//        CompletionStage<Result> userLoginResult = userLogin(username, password);
//
//
//        return redirect("/generateCode").flashing("success", "Login successful!!");
//
//
//    }
    public CompletionStage<Result> login(play.mvc.Http.Request request) {
        final Form<Login>[] loginForm = new Form[]{formFactory.form(Login.class).bindFromRequest(request)};

        if (loginForm[0].hasErrors()) {
            return CompletableFuture.completedFuture(badRequest(views.html.login.render(loginForm[0], request, messages.preferred(request))));
        }

        Login login = loginForm[0].get();

        String username = login.getUsername();
        String password = login.getPassword();
        System.out.println("Attempting login for username: " + username);

        // Call userLogin method asynchronously
        return userLogin(username, password).thenApplyAsync(result -> {
            String  apiResponse = result.body().toString();
            List<String> errs = new ArrayList<>();
            if (result.status() == Http.Status.OK) {
//            JsonNode jsonResponse = Json.parse(apiResponse);
                // Check the API response here and handle it accordingly
                System.out.println("Login process proceed: " + username);
                System.out.println("Message: " + apiResponse);
                System.out.println("Login successful for username: " + username);
                // Login was successful, redirect to generateCode and adminDashboard

                    return redirect("/adminDashboard").addingToSession(request, "username", username).addingToSession(request, "password", password);
                }
              else {
                // Login was unsuccessful, display an error message
                System.out.println("Message: " + apiResponse);
                errs.add("Invalid username and password");
                String errorMessage = String.join(", ", errs);
//            String errorMessage = "Invalid username and password";
                System.out.println("Login unsuccessful for username: " + username + ", Error: " + errorMessage);
                // Use the correct field name when adding the global error
                loginForm[0] = loginForm[0].withError("validatePassword", errorMessage);
                return badRequest(views.html.login.render(loginForm[0], request, messages.preferred(request)));
            }
        });
    }

    // Existing create method
    public Result create(play.mvc.Http.Request request) {
        Form<Login> loginForm = formFactory.form(Login.class);
        return ok(views.html.login.render(loginForm, request, messages.preferred(request)));
    }





    public CompletionStage<Result> logOut() {
        // Retrieve the global session token directly
        String sessionToken = getGlobalSessionToken();
        System.out.println("Session Token: " + sessionToken);

        // Call the userLogout method asynchronously with the session token
        CompletionStage<Result> userLogoutResult = userLogout(sessionToken );

        // Return the CompletionStage<Result> directly
        return userLogoutResult.thenApplyAsync(response -> {
            // Log the response status and body
            System.out.println("Logout Response Status: " + response.status());
            System.out.println("Logout Response Body: " + response.body());

            // Check the response status and handle it accordingly
            if (response.status() == OK) {
                System.out.println("Logout successful");
                // Logout was successful, redirect to a suitable page
                return redirect("/").withNewSession();
            } else {
                // Logout was unsuccessful, display an error message
                System.out.println("Logout unsuccessful");
                String errorMessage = "Logout unsuccessful";
                return badRequest(errorMessage);
            }
        });
    }










}
