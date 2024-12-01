package org.example.librarymanager.services;

import org.example.librarymanager.Config;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Backblaze {
    private static Backblaze instance;
    private S3Client b2;

    /**
     * backblaze for file uploading
     */
    private Backblaze() {
        Matcher matcher = Pattern.compile("https://s3\\.([a-z0-9-]+)\\.backblazeb2\\.com").matcher(Config.BB_ENDPOINT);
        if (!matcher.find()) {
            System.out.println("Backblaze connected fail");
        }
        String region = matcher.group(1);
        try {
            b2 = S3Client.builder()
                    .region(Region.of(region))
                    .credentialsProvider(ProfileCredentialsProvider.create(Config.BB_CREDENTIAL))
                    .endpointOverride(new URI(Config.BB_ENDPOINT))
                    .build();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public static synchronized Backblaze getInstance() {
        if (instance == null) {
            instance = new Backblaze();
        }
        return instance;
    }

    /**
     * upload a file and return public url
     * @param key
     * @param path
     * @return
     */
    public String upload(String key, String path) {
        PutObjectResponse response = b2.putObject(PutObjectRequest.builder()
                .bucket(Config.BB_BUCKET_NAME)
                .key(key)
                .build(),
                Paths.get(path)
        );
        return Config.BB_BUCKET_ENDPOINT + "/" + key;
    }
}
