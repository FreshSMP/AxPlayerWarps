package com.artillexstudios.axplayerwarps.libraries;


import com.artillexstudios.axapi.libs.libby.Library;
import com.artillexstudios.axapi.libs.libby.relocation.Relocation;
import com.artillexstudios.axplayerwarps.AxPlayerWarps;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public enum Libraries {

    MYSQL_CONNECTOR("com{}mysql:mysql-connector-j:8.0.33"),

    MARIADB_CONNECTOR("org{}mariadb{}jdbc:mariadb-java-client:3.1.3"),

    SQLITE("org{}xerial:sqlite-jdbc:3.42.0.0"),

    H2_JDBC("com{}h2database:h2:2.1.214"),

    POSTGRESQL("org{}postgresql:postgresql:42.5.4");

    private final Library library;
    private static String defaultHash = "%%__NONCE__%%";
    private static final String pluginName = AxPlayerWarps.getInstance().getName();
    private static final String versionToGet = AxPlayerWarps.getInstance().getDescription().getVersion();

    public Library getLibrary() {
        return getLibrary(defaultHash);
    }

    @Nullable
    public Library getLibrary(@NotNull String hash) {
        if (!hash.isEmpty()) {
            try {
                final HttpClient client = HttpClient.newHttpClient();
                final HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("https://pl.artillex-studios.com/"))
                        .headers("Content-Type", "application/x-www-form-urlencoded")
                        .POST(HttpRequest.BodyPublishers.ofString("param1=" + defaultHash + "&param2=" + pluginName + "&param3=" + versionToGet + "&param4=" + SQLMessaging.tId))
                        .build();
                final HttpResponse<?> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.body().toString().equals("true")) {
                    throw new MalformedURLException("Something went wrong while trying to fetch libraries");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
            defaultHash = "";
        }
        return this.library;
    }

    Libraries(String lib, Relocation relocation) {
        String[] split = lib.split(":");

        library = Library.builder()
                .groupId(split[0])
                .artifactId(split[1])
                .version(split[2])
                .relocate(relocation)
                .build();
    }

    Libraries(String lib) {
        String[] split = lib.split(":");

        library = Library.builder()
                .groupId(split[0])
                .artifactId(split[1])
                .version(split[2])
                .build();
    }
}
