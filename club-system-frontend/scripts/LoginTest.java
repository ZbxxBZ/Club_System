import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

public class LoginTest {
    public static void main(String[] args) {
        String baseUrl = getArg(args, "--baseUrl", "http://localhost:8080/api");
        String username = getArg(args, "--username", "20230001");
        String password = getArg(args, "--password", "123456");

        String loginUrl = baseUrl.endsWith("/") ? baseUrl + "auth/login" : baseUrl + "/auth/login";

        String jsonBody = "{\"username\":\"" + escapeJson(username) + "\",\"password\":\"" + escapeJson(password) + "\"}";

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(loginUrl))
                .timeout(Duration.ofSeconds(15))
                .header("Content-Type", "application/json; charset=UTF-8")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody, StandardCharsets.UTF_8))
                .build();

        System.out.println("=== Login API Test ===");
        System.out.println("URL: " + loginUrl);
        System.out.println("Request Body: " + jsonBody);

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            System.out.println("\nStatus: " + response.statusCode());
            System.out.println("Response Body: ");
            System.out.println(response.body());

            if (response.statusCode() == 200) {
                System.out.println("\nHTTP 200: 请求已到达后端，请检查响应中的 code/message/data。");
            } else if (response.statusCode() == 401) {
                System.out.println("\nHTTP 401: 认证失败（账号/密码错误或登录策略限制）。");
            } else {
                System.out.println("\n收到非 200/401 状态，请按后端日志进一步排查。");
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("\n请求失败: " + e.getMessage());
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private static String getArg(String[] args, String key, String defaultValue) {
        for (int i = 0; i < args.length - 1; i++) {
            if (key.equals(args[i])) {
                return args[i + 1];
            }
        }
        return defaultValue;
    }

    private static String escapeJson(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
