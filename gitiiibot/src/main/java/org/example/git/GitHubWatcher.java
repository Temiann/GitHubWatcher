package org.example.git;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Observable;

public class GitHubWatcher extends Observable implements Runnable {
    private final String repoUrl;
    private final String authToken;
    private String lastCommitSha;

    public GitHubWatcher(String repoUrl, String authToken) {
        this.repoUrl = repoUrl;
        this.authToken = authToken;
    }

    @Override
    public void run() {
        while (true) {
            try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                HttpGet request = new HttpGet(repoUrl + "/commits");
                request.addHeader(new BasicHeader("Authorization", "token " + authToken));
                HttpResponse response = httpClient.execute(request);
                HttpEntity entity = response.getEntity();

                if (entity != null) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
                    JsonElement jsonElement = JsonParser.parseReader(reader);

                    if (jsonElement.isJsonArray()) {
                        JsonArray commits = jsonElement.getAsJsonArray();
                        System.out.println("Received commits: " + commits);

                        if (commits.size() > 0) {
                            JsonObject latestCommit = commits.get(0).getAsJsonObject();
                            String commitSha = latestCommit.get("sha").getAsString();
                            System.out.println("Latest commit SHA: " + commitSha);

                            if (!commitSha.equals(lastCommitSha)) {
                                lastCommitSha = commitSha;

                                // Извлечение нужной информации из коммита
                                JsonObject commit = latestCommit.getAsJsonObject("commit");
                                JsonObject author = commit.getAsJsonObject("author");
                                String authorName = author.get("name").getAsString();
                                String message = commit.get("message").getAsString();
                                String url = latestCommit.get("html_url").getAsString();

                                String notificationMessage = String.format("New commit by %s:\n%s\n%s", authorName, message, url);
                                setChanged();
                                notifyObservers(notificationMessage);
                            }
                        }
                    } else {
                        System.out.println("Received JSON is not an array: " + jsonElement.toString());
                    }
                }

                Thread.sleep(60000); // Check every 60 seconds
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}