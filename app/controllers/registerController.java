package controllers;
import Forms.User;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.core.j.JavaResultExtractor;
import play.core.j.JavaResultExtractor$;
import play.data.Form;
import play.data.FormFactory;
import play.i18n.MessagesApi;
import play.libs.Json;
import play.libs.ws.WSClient;
import play.libs.ws.WSResponse;
import play.mvc.Controller;
import play.mvc.Http.Flash;
import play.mvc.Http;
import play.mvc.Result;
import play.data.validation.ValidationError;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;


import javax.inject.Inject;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.*;

public class registerController extends Controller {

    private final WSClient ws;
    private static String apiResponse; // Global variable to store API response
    private final FormFactory formFactory;
    private final MessagesApi messages;
    @Inject
    public registerController(WSClient ws,FormFactory formFactory, MessagesApi messages) {
        this.ws = ws;
        this.formFactory = formFactory;
        this.messages = messages;
    }




    public Result register(Http.Request request) {
        final Form<User>[] userForm = new Form[]{formFactory.form(User.class).bindFromRequest(request)};

        if (userForm[0].hasErrors()) {
            return badRequest(views.html.register.render(userForm[0], request, messages.preferred(request)));
        }



        // Check if passwords match
//        String password = userForm.get().getPassword();
//        String repeatPassword = userForm.get().getRepeatPassword();

        // Check if passwords match
        String password = String.valueOf(userForm[0].field("password").value());
        String repeatPassword = String.valueOf(userForm[0].field("repeatPassword").value());

//        if (password == null || password.trim().isEmpty()) {
//            // Password is empty, add an error to the form
//            userForm = userForm.withError("validatePassword", "Password is required");
//            return badRequest(views.html.register.render(userForm, request, messages.preferred(request)));
//        }

        if (password == null || !password.equals(repeatPassword)) {
            // Passwords do not match, add an error to the form
            List<String> errs = new ArrayList<>();
            errs.add("Passwords do not match");
            String errorMessage = String.join(", ", errs);  // Concatenate error messages
            userForm[0] = userForm[0].withError("validatePassword", errorMessage);
            return badRequest(views.html.register.render(userForm[0], request, messages.preferred(request)));
        }




        User user = userForm[0].get();
        // Process the user registration, save data, etc.

        // Call createUser method from UserRequestController
        CompletionStage<Result> createUserResult = createUser(user.getUsername(), user.getPassword(), user.getFirstName(), user.getLastName(), user.getPhoneNumber() );

        return createUserResult.thenApplyAsync(result -> {
            String apiResponse = result.body().toString();
            List<String> errs = new ArrayList<>();
            if (result.status() == Http.Status.BAD_REQUEST) {
                System.out.println("Message: " + apiResponse);
                errs.add("username already exists");
                String errorMessage = String.join(", ", errs);
                userForm[0] = userForm[0].withError("validateUsername", errorMessage);

                // Log the form errors
                System.out.println("Form errors after adding: " + userForm[0].errors());

                // Return a badRequest result
                return badRequest(views.html.register.render(userForm[0], request, messages.preferred(request)));
            } else {
                // Set success message in the session
                return redirect("/login").flashing("success", "Registration successfully!!Wait for admin approval");
            }
        }).toCompletableFuture().join();





//        return redirect("/login").flashing("success", "Registration successfully!!");



    }




    // To create a user
    public Result create(Http.Request request) {
        Form<User> userForm = formFactory.form(User.class);
        return ok(views.html.register.render(userForm, request, messages.preferred(request)));
    }

    public CompletionStage<Result> fetchAllUsers() {
        String apiUrl = "https://jackal-modern-javelin.ngrok-free.app/user_operations_activation_tool.php/";
        return sendGetRequest(apiUrl, "fetchAllUsers");
    }

    public CompletionStage<Result> fetchUser(String username) {
        String apiUrl = "https://jackal-modern-javelin.ngrok-free.app/user_operations_activation_tool.php/";
        apiUrl += "?action=fetchUser&username=" + username;
        return sendGetRequest(apiUrl, "fetchUser");
    }

    public CompletionStage<Result> createUser(String username, String password, String firstName, String lastName, String phoneNumber) {
        String apiUrl = "https://jackal-modern-javelin.ngrok-free.app/user_operations_activation_tool.php/";

        try {
            // Hash the password using SHA-256
            String hashedPassword = sha256Hash(password);

            ObjectNode json = Json.newObject();
            json.put("action", "createUser");
            json.put("username", username);
            json.put("password", hashedPassword);
            json.put("firstName", firstName);
            json.put("lastName", lastName);
            json.put("phoneNumber", phoneNumber);

            return sendJsonRequest(apiUrl, json);
        } catch (Exception e) {
            return completedFuture(internalServerError("Error creating JSON data"));
        }
    }

    private String sha256Hash(String password) {
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
            throw new RuntimeException("Error hashing password", e);
        }
    }

    public CompletionStage<Result> makeAdmin(String username) {
        String apiUrl = "https://jackal-modern-javelin.ngrok-free.app/user_operations_activation_tool.php/";

        ObjectNode json = Json.newObject();
        json.put("action", "makeAdmin");
        json.put("username", username);

        return sendJsonRequest(apiUrl, json);
    }

    public CompletionStage<Result> disableUser(String username) {
        String apiUrl = "https://jackal-modern-javelin.ngrok-free.app/user_operations_activation_tool.php/";

        ObjectNode json = Json.newObject();
        json.put("action", "disableUser");
        json.put("username", username);

        return sendJsonRequest(apiUrl, json);
    }

    public CompletionStage<Result> activateUser(String username) {
        String apiUrl = "https://jackal-modern-javelin.ngrok-free.app/user_operations_activation_tool.php/";

        ObjectNode json = Json.newObject();
        json.put("action", "activateUser");
        json.put("username", username);

        return sendJsonRequest(apiUrl, json);
    }

    public CompletionStage<Result> activateMultipleUsers() {
        String apiUrl = "https://jackal-modern-javelin.ngrok-free.app/user_operations_activation_tool.php/";

        ArrayNode usernamesArray = Json.newArray();
        usernamesArray.add("Test");

        ObjectNode json = Json.newObject();
        json.put("action", "activateMultipleUsers");
        json.set("usernames", usernamesArray);

        return sendJsonRequest(apiUrl, json);
    }

    public CompletionStage<Result> disableMultipleUsers() {
        String apiUrl = "https://jackal-modern-javelin.ngrok-free.app/user_operations_activation_tool.php/";

        ArrayNode usernamesArray = Json.newArray();
        usernamesArray.add("Test");
        usernamesArray.add("Test4");
        usernamesArray.add("Test3");
        usernamesArray.add("Karen Rotich");

        ObjectNode json = Json.newObject();
        json.put("action", "disableMultipleUsers");
        json.set("usernames", usernamesArray);

        return sendJsonRequest(apiUrl, json);
    }

    public static String getApiResponse() {
        return apiResponse;
    }

    private CompletionStage<Result> sendGetRequest(String apiUrl, String action) {
        return ws.url(apiUrl)
                .addQueryParameter("action", action)
                .get()
                .thenApplyAsync(this::handleResponseAndGetResult);
    }

    private CompletionStage<Result> sendJsonRequest(String apiUrl, ObjectNode json) {
        return ws.url(apiUrl)
                .post(json)
                .thenApplyAsync(this::handleResponseAndGetResult);
    }

    private Result handleResponseAndGetResult(WSResponse response) {
        handleResponse(response);

        // Save the response to the global variable
        apiResponse = response.getBody();

        // Check if the response contains "Duplicate entry"
        if (apiResponse != null && apiResponse.contains("Duplicate entry")) {
            // Extract the username from the original request
            String username = extractUsernameFromResponse(apiResponse);

            // Print a custom message
            System.out.println("The username '" + username + "' already exists.");
            String errorMessage = "The username '" + username + "' already exists.";

            return badRequest(apiResponse);

        }

        // Assuming you want to return the response body as the result
        return ok(apiResponse);
    }

    private void handleResponse(WSResponse response) {
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

    private String extractUsernameFromResponse(String responseBody) {
        try {
            // Parse the JSON response
            JsonNode jsonResponse = Json.parse(responseBody);

            // Check if the JSON response contains the 'error' field
            if (jsonResponse.has("error")) {
                // Extract the error message from the JSON response
                String errorMessage = jsonResponse.get("error").asText();

                // Check if the error message contains the username
                if (errorMessage.contains("Duplicate entry")) {
                    // Extract the username from the error message
                    int startIndex = errorMessage.indexOf("'") + 1;
                    int endIndex = errorMessage.indexOf("'", startIndex);
                    return errorMessage.substring(startIndex, endIndex);
                }
            }

            // If the username is not found, return a default value or handle accordingly
            return "N/A";
        } catch (Exception e) {
            // Handle the exception or log it
            System.out.println("Error extracting username from response: " + e.getMessage());
            return "N/A";
        }
    }



}

