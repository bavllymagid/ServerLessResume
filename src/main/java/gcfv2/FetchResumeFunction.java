package gcfv2;

import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class FetchResumeFunction implements HttpFunction {
    private final FireStoreService firestoreService;
    private final Gson gson;

    public FetchResumeFunction() throws IOException {
        this.firestoreService = new FireStoreService();
        this.gson = new Gson();
    }

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        String resumeId = request.getFirstQueryParameter("id").orElse("resume1");

        try {
            JsonObject resumeJson = fetchResumeData(resumeId);
            writeResponse(response, 200, resumeJson.toString());
        } catch (Exception e) {
            JsonObject errorJson = new JsonObject();
            errorJson.addProperty("error", "Error fetching resume: " + e.getMessage());
            writeResponse(response, 500, errorJson.toString());
        }
    }

    private JsonObject fetchResumeData(String resumeId) throws ExecutionException, InterruptedException {
        var documentSnapshot = firestoreService.getFirestore().collection("resume").document(resumeId).get().get();

        if (documentSnapshot.exists()) {
            Map<String, Object> dataMap = documentSnapshot.getData();
            return gson.toJsonTree(dataMap).getAsJsonObject();
        } else {
            JsonObject errorJson = new JsonObject();
            errorJson.addProperty("error", "No resume found with the provided id");
            return errorJson;
        }
    }

    private void writeResponse(HttpResponse response, int statusCode, String message) throws IOException {
        response.setStatusCode(statusCode);
        response.setContentType("application/json");
        try (BufferedWriter writer = response.getWriter()) {
            writer.write(message);
        }
    }
}
