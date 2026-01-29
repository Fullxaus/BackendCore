package ru.mentee.power.crm.web;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import java.net.ServerSocket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class HelloCrmServerTest {

    private HelloCrmServer server;
    private int port;

    // Находим свободный порт перед запуском сервера
    private int findFreePort() throws IOException {
        try (ServerSocket socket = new ServerSocket(0)) {
            socket.setReuseAddress(true);
            return socket.getLocalPort();
        }
    }

    @BeforeEach
    void setUp() throws IOException {
        port = findFreePort();
        server = new HelloCrmServer(port);
        server.start();
    }

    @AfterEach
    void tearDown() {
        if (server != null) {
            server.stop();
        }
    }

    @Test
    void helloEndpointReturnsHtml() throws IOException {
        URL url = new URL("http://localhost:" + port + "/hello");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        conn.connect();

        int status = conn.getResponseCode();
        assertEquals(200, status, "Expected HTTP 200");

        String contentType = conn.getHeaderField("Content-Type");
        assertNotNull(contentType, "Content-Type header must be present");
        assertTrue(contentType.toLowerCase().contains("text/html"), "Content-Type should be text/html");

        try (InputStream is = conn.getInputStream();
             BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String body = br.lines().collect(Collectors.joining("\n"));
            assertTrue(body.contains("<h1>Hello CRM!</h1>"), "Response body should contain greeting HTML");
            assertTrue(body.contains("<html"), "Response should be an HTML document");
        } finally {
            conn.disconnect();
        }
    }
}
