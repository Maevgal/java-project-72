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
import org.postgresql.Driver;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.util.stream.Collectors;

@Slf4j
public class App {
    public static void main(String[] args) throws Exception {
        var app = getApp();
        app.start(getPort());
    }

    private static int getPort() {
        String port = System.getenv().getOrDefault("PORT", "7070");
        return Integer.valueOf(port);
    }

    public static Javalin getApp() throws Exception {
        var hikariConfig = new HikariConfig();

        /*String jdbcDatabaseUrl = System.getenv()
                .getOrDefault("JDBC_DATABASE_URL", "jdbc:h2:mem:project;DB_CLOSE_DELAY=-1;");*/
        String jdbcDatabaseUrl = "jdbc:h2:mem:project;DB_CLOSE_DELAY=-1;";
        if (System.getenv("JDBC_DATABASE_URL") != null) {
            hikariConfig.setDriverClassName(Driver.class.getCanonicalName());
            jdbcDatabaseUrl = System.getenv("JDBC_DATABASE_URL");
            hikariConfig.setUsername(System.getenv("JDBC_DATABASE_USERNAME"));
            hikariConfig.setPassword(System.getenv("JDBC_DATABASE_PASSWORD"));
        }
        hikariConfig.setJdbcUrl(jdbcDatabaseUrl);
        log.info("jdbcDatabaseUrl: %s".formatted(jdbcDatabaseUrl));

        var dataSource = new HikariDataSource(hikariConfig);


        URL url = App.class.getClassLoader().getResource("schema.sql");
        File file = new File(url.getFile());
        String sql = Files.lines(file.toPath())
                .collect(Collectors.joining("\n"));

        // Получаем соединение, создаем стейтмент и выполняем запрос
        try (var connection = dataSource.getConnection();
             var statement = connection.createStatement()) {
            statement.execute(sql);
        }
        BaseRepository.dataSource = dataSource;

        var app = Javalin.create(config -> {
            config.plugins.enableDevLogging();
        });

        JavalinJte.init(createTemplateEngine());

        app.get("/", RootController::index);
        app.start(7070);
        app.post("/urls", UrlController::create);
        app.get("/urls", UrlController::index);
        app.get("/urls/{id}", UrlController::show);
        return app;
    }

    private static TemplateEngine createTemplateEngine() {
        ClassLoader classLoader = App.class.getClassLoader();
        ResourceCodeResolver codeResolver = new ResourceCodeResolver("templates", classLoader);
        TemplateEngine templateEngine = TemplateEngine.create(codeResolver, ContentType.Html);
        return templateEngine;
    }


}
