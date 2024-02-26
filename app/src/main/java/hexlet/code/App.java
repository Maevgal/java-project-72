package hexlet.code;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.resolve.ResourceCodeResolver;
import hexlet.code.controller.RootController;
import hexlet.code.controller.UrlController;
import hexlet.code.repository.BaseRepository;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinJte;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.sql.SQLException;

@Slf4j
public class App {
    public static void main(String[] args) throws SQLException, IOException {
        Javalin app = getApp();
        app.start(getPort());
    }

    private static int getPort() {
        String port = System.getenv().getOrDefault("PORT", "7070");
        return Integer.valueOf(port);
    }

    public static Javalin getApp() {
        var hikariConfig = new HikariConfig();

        String jdbcDatabaseUrl = System.getenv()
                .getOrDefault("JDBC_DATABASE_URL", "jdbc:h2:mem:project;DB_CLOSE_DELAY=-1;");

        hikariConfig.setJdbcUrl(jdbcDatabaseUrl);
        log.info("jdbcDatabaseUrl: %s".formatted(jdbcDatabaseUrl));

        var dataSource = new HikariDataSource(hikariConfig);

        try (var connection = dataSource.getConnection();
             var statement = connection.createStatement()) {
            String sql = new String(App.class.getClassLoader()
                    .getResourceAsStream("schema.sql").readAllBytes());
            log.info(sql);
            statement.execute(sql);
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
        BaseRepository.dataSource = dataSource;

        var app = Javalin.create(config -> {
            config.plugins.enableDevLogging();
        });

        JavalinJte.init(createTemplateEngine());

        app.get("/", RootController::index);
        app.post("/urls", UrlController::create);
        app.get("/urls", UrlController::index);
        app.get("/urls/{id}", UrlController::show);
        app.post("/urls/{id}/checks", UrlController::check);

        return app;
    }

    private static TemplateEngine createTemplateEngine() {
        ClassLoader classLoader = App.class.getClassLoader();
        ResourceCodeResolver codeResolver = new ResourceCodeResolver("templates", classLoader);
        TemplateEngine templateEngine = TemplateEngine.create(codeResolver, ContentType.Html);
        return templateEngine;
    }
}
