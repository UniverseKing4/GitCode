package com.github.actions;

import android.util.Base64;
import okhttp3.*;
import org.json.JSONObject;
import java.io.IOException;

public class GitHubAPI {
    private final String username;
    private final String token;
    private final String repo;
    private final OkHttpClient client = new OkHttpClient();

    public GitHubAPI(String username, String token, String repo) {
        this.username = username;
        this.token = token;
        this.repo = repo;
    }

    public String commitAndPush(String path, String content, String message) {
        try {
            String url = "https://api.github.com/repos/" + username + "/" + repo + "/contents/" + path;
            String sha = getFileSha(url);
            
            JSONObject json = new JSONObject();
            json.put("message", message.isEmpty() ? "Update " + path : message);
            json.put("content", Base64.encodeToString(content.getBytes(), Base64.NO_WRAP));
            if (sha != null) json.put("sha", sha);

            RequestBody body = RequestBody.create(json.toString(), MediaType.parse("application/json"));
            Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "token " + token)
                .put(body)
                .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    return "Success: Committed and pushed!";
                } else {
                    return "Error: " + response.code() + " - " + response.message();
                }
            }
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    private String getFileSha(String url) {
        try {
            Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "token " + token)
                .get()
                .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    JSONObject json = new JSONObject(response.body().string());
                    return json.getString("sha");
                }
            }
        } catch (Exception ignored) {}
        return null;
    }
}
