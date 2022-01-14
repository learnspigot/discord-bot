package com.learnspigot.bot.data;

import com.google.gson.*;
import com.learnspigot.bot.verification.profile.VerificationProfile;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class ProfileData {
    private final @NotNull String name = "profiles.json";
    private final @NotNull Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private @NotNull final JsonArray data;

    public ProfileData(final @NotNull ScheduledExecutorService scheduledExecutorService) throws IOException {
        new File(name).createNewFile();
        JsonElement fileData = JsonParser.parseReader(Files.newBufferedReader(Paths.get(name)));
        data = fileData.toString().equals("null") ? new JsonArray() : fileData.getAsJsonArray();
        scheduledExecutorService.scheduleAtFixedRate(this::write, 15L, 15L,
                TimeUnit.SECONDS);
    }

    public void cache(final @NotNull VerificationProfile verificationProfile) {
        removeIfExists(verificationProfile.id());
        data.add(gson.toJsonTree(verificationProfile, VerificationProfile.class));
    }

    private void removeIfExists(final long id) {
        JsonArray data = this.data.deepCopy();
        data.forEach(jsonElement -> {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            if (jsonObject.get("id").getAsLong() == id) {
                this.data.remove(jsonElement);
            }
        });
    }

    private void write() {
        try {
            Writer writer = new FileWriter(name);
            gson.toJson(data, writer);
            writer.close();
        } catch (IOException e) {
            System.out.println("Unable to save data to file.");
        }
    }

    public @NotNull Gson gson() {
        return gson;
    }

    public @NotNull JsonArray data() {
        return data;
    }
}
