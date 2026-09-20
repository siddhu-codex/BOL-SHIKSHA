package com.example.bolshiksha;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/** Session debug ingest (folded in call sites). */
public final class AgentDebugLog {

    private static final String SESSION = "e2e08f";
    private static final String INGEST =
            "http://127.0.0.1:7542/ingest/0b34d93f-ccd9-4412-a133-8d0f6d156358";
    private static final String INGEST_EMU =
            "http://10.0.2.2:7542/ingest/0b34d93f-ccd9-4412-a133-8d0f6d156358";
    private static final String FILE =
            "C:\\Users\\SHRI\\Desktop\\BOL SHIKSHA\\debug-e2e08f.log";

    private AgentDebugLog() {}

    public static void log(String hypothesisId, String location, String message, String dataJson) {
        String payload = "{\"sessionId\":\"" + SESSION + "\","
                + "\"hypothesisId\":\"" + esc(hypothesisId) + "\","
                + "\"location\":\"" + esc(location) + "\","
                + "\"message\":\"" + esc(message) + "\","
                + "\"data\":" + (dataJson == null ? "{}" : dataJson) + ","
                + "\"timestamp\":" + System.currentTimeMillis() + "}";
        appendFile(payload);
        post(INGEST, payload);
        post(INGEST_EMU, payload);
    }

    private static String esc(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
    }

    private static void appendFile(String payload) {
        try {
            FileOutputStream fos = new FileOutputStream(FILE, true);
            OutputStreamWriter w = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
            w.write(payload);
            w.write("\n");
            w.close();
        } catch (Exception ignored) {
        }
    }

    private static void post(final String url, final String payload) {
        new Thread(() -> {
            HttpURLConnection c = null;
            try {
                c = (HttpURLConnection) new URL(url).openConnection();
                c.setConnectTimeout(400);
                c.setReadTimeout(400);
                c.setRequestMethod("POST");
                c.setDoOutput(true);
                c.setRequestProperty("Content-Type", "application/json");
                c.setRequestProperty("X-Debug-Session-Id", SESSION);
                c.getOutputStream().write(payload.getBytes(StandardCharsets.UTF_8));
                c.getResponseCode();
            } catch (Exception ignored) {
            } finally {
                if (c != null) c.disconnect();
            }
        }, "agent-debug-log").start();
    }
}
