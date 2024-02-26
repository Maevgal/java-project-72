package hexlet.code;

import hexlet.code.model.Url;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlRepository;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

class AppTest {
    private static Javalin app;

    @BeforeEach
    public void setUp() throws Exception {
        app = App.getApp();
    }


    @Test
    void testMainPage() throws Exception {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/");
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body()).isNotNull();
            assertThat(response.body().string())
                    .contains("Бесплатно проверяйте сайты на SEO пригодность");
        });
    }

    @Test
    public void testUrlPage() {
        JavalinTest.test(app, ((server, client) -> {
            var response = client.get("/urls");
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body()).isNotNull();
            assertThat(response.body().string()).contains("urls");
        }));
    }

    @Test
    public void testCreatePage() {
        JavalinTest.test(app, ((server, client) -> {
            var requestBody = "url=https://www.example.com";
            var response = client.post("/urls", requestBody);
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).contains("https://www.example.com");

            var createdUrl = UrlRepository.checkUrl("https://www.example.com");
            assertThat(createdUrl.size()).isEqualTo(1);

            assertThat(createdUrl.get(0).getName()).isEqualTo("https://www.example.com");

        }));
    }

    @Test
    public void testCreateIncorrectPage() throws SQLException {
        JavalinTest.test(app, (server, client) -> {
            var requestBody = "url=12345";
            var response = client.post("/urls", requestBody);
            assertThat(response.code()).isEqualTo(200);
            assertThat(UrlRepository.getUrls().size()).isEqualTo(0);
        });
    }

    @Test
    public void testUrlNotFound() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/urls111");
            assertThat(response.code()).isEqualTo(404);
        });
    }

    @Test
    public void testListUrls() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/urls");
            assertThat(response.code()).isEqualTo(200);
        });
    }

    @Test
    public void testShow() {
        JavalinTest.test(app, (server, client) -> {
            var url = new Url("https://www.example.com");
            UrlRepository.save(url);
            var createdUrl = UrlRepository.checkUrl("https://www.example.com");
            var id = createdUrl.get(0).getId();
            var response = client.get("/urls/" + id);
            assertThat(response.code()).isEqualTo(200);
        });
    }

    @Test
    public void testUrlCheck() throws IOException, SQLException {
        var mockServer = new MockWebServer();
        var ckUrl = mockServer.url("/").toString();

        Path filePath = Paths.get("src", "test", "resources", "index.html").toAbsolutePath().normalize();
        String file = Files.readString(filePath).trim();
        var mockResponse = new MockResponse().setBody(file);
        mockServer.enqueue(mockResponse);

        JavalinTest.test(app, (server, client) -> {
            var requestBody = "url=" + ckUrl;
            var response = client.post("/urls", requestBody);
            assertThat(response.code()).isEqualTo(200);

            var formattedName = String.format("%s://%s", mockServer.url("/").url().getProtocol(),
                    mockServer.url("/").url().getAuthority());
            var addUrl = UrlRepository.checkUrl(formattedName);
            assertThat(addUrl).isNotNull();
            assertThat(addUrl.get(0).getName()).isEqualTo(formattedName);

            var response2 = client.post("/urls/" + addUrl.get(0).getId() + "/checks");
            assertThat(response2.code()).isEqualTo(200);

            var ursCheck = UrlCheckRepository.find(addUrl.get(0).getId());
            var title = ursCheck.get(0).getTitle();
            var h1 = ursCheck.get(0).getH1();
            var description = ursCheck.get(0).getDescription();

            assertThat(title).as("Check title").isEqualTo("This is a title");
            assertThat(h1).as("Check h1").isEqualTo("This is a header");
            assertThat(description).as("Check description").isEqualTo("This is a description");
        });
    }

    @Test
    public void testCreteUrl() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.post("/urls", "url=https://ru.hexlet.io/projects/72/members/37059?step=6");
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string())
                    .contains("https://ru.hexlet.io");
        });
    }

    @Test
    public void testUrlPageId() {
        var mockServer = new MockWebServer();
        JavalinTest.test(app, (server, client) -> {
            client.post("/urls", "url=" + mockServer.url("/test"));
            assertThat(client.get("/urls?page=1").code()).isEqualTo(200);
        });
    }
}
