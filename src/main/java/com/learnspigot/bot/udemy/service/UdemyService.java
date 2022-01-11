package com.learnspigot.bot.udemy.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.learnspigot.bot.LearnSpigotConstant;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.stream.Collectors;

public final class UdemyService {
    private static final @NotNull UdemyService instance = new UdemyService();

    public static @NotNull UdemyService instance() {
        return instance;
    }

    private @Nullable HttpsURLConnection connection;

    private UdemyService() {

    }

    public long getUserID(final @NotNull String url) throws IOException {
        establishConnection(url);
        assert connection != null;

        String content = new BufferedReader(new InputStreamReader(connection.getInputStream())).lines()
                .collect(Collectors.joining());
        long id = Long.parseLong(content.substring(content.lastIndexOf("id&quot;:") + 9).substring(0, 12).replaceAll("[^0-9]", ""));

        closeConnection();
        return id;
    }

    public @NotNull JsonObject getUserInfo(final long id) throws IOException {
        establishConnection(LearnSpigotConstant.UDEMY_API_ENDPOINT.get() + "users/" + id);
        assert connection != null;

        JsonObject jsonObject = JsonParser.parseReader(new BufferedReader(new InputStreamReader(
                connection.getInputStream()))).getAsJsonObject();
        closeConnection();
        return jsonObject;
    }

    public @NotNull JsonArray getUserCourses(final long id) throws IOException {
        establishConnection(LearnSpigotConstant.UDEMY_API_ENDPOINT.get() + "users/" + id + "/subscribed-profile-courses/");
        assert connection != null;

        JsonObject jsonObject = JsonParser.parseReader(new BufferedReader(new InputStreamReader(connection.getInputStream()))).getAsJsonObject();
        closeConnection();

        JsonArray courses = new JsonArray();
        for (int i = 1; i <= (int) Math.ceil((double) jsonObject.get("count").getAsInt() / 12); i++) {
            establishConnection(LearnSpigotConstant.UDEMY_API_ENDPOINT.get() + "users/" + id + "/subscribed-profile-courses/?page=" + i);
            assert connection != null;
            JsonParser.parseReader(new BufferedReader(new InputStreamReader(connection.getInputStream()))).getAsJsonObject().get("results").getAsJsonArray().forEach(courses::add);
            closeConnection();
        }
        return courses;
    }

    public @NotNull JsonArray getLectures() throws IOException {
        establishConnection(LearnSpigotConstant.UDEMY_API_ENDPOINT.get() + "courses/" + LearnSpigotConstant.LEARN_SPIGOT_COURSE_ID.get() + "/public-curriculum-items");
        assert connection != null;

        JsonObject jsonObject = JsonParser.parseReader(new BufferedReader(new InputStreamReader(connection.getInputStream()))).getAsJsonObject();
        closeConnection();

        JsonArray lectures = new JsonArray();
        for (int i = 1; i <= (int) Math.ceil((double) jsonObject.get("count").getAsInt() / 12); i++) {
            establishConnection(LearnSpigotConstant.UDEMY_API_ENDPOINT.get() + "courses/" + LearnSpigotConstant.LEARN_SPIGOT_COURSE_ID.get() + "/public-curriculum-items/?page=" + i);
            assert connection != null;
            JsonParser.parseReader(new BufferedReader(new InputStreamReader(connection.getInputStream()))).getAsJsonObject().get("results").getAsJsonArray().forEach(jsonElement -> {
                JsonObject lectureJsonObject = jsonElement.getAsJsonObject();
                if (lectureJsonObject.get("_class").getAsString().equals("lecture") || lectureJsonObject.get("_class").getAsString().equals("quiz")) {
                    lectures.add(lectureJsonObject);
                }
            });
            closeConnection();
        }
        return lectures;
    }

    private void establishConnection(final @NotNull String endpoint) {
        try {
            connection = (HttpsURLConnection) new URL(endpoint).openConnection();

            connection.addRequestProperty("Content-Type", "application/json");
            connection.addRequestProperty("Authorization", "Bearer " + LearnSpigotConstant.BEARER.get());
            connection.addRequestProperty("Cookie", "client_id= " + LearnSpigotConstant.CLIENT_ID.get() + "; access_token=" + LearnSpigotConstant.BEARER.get());
            connection.setRequestMethod("GET");

            connection.setDoOutput(true);

            connection.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeConnection() {
        if (connection != null) {
            connection.disconnect();
        }
    }
}
