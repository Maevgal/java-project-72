package hexlet.code.repository;

import hexlet.code.dto.UrlsCheckPage;
import hexlet.code.model.Url;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UrlRepository extends BaseRepository {
    public static List<Url> checkUrl(String name) throws SQLException {
        String sql = "SELECT * FROM urls WHERE name = ?";
        List<Url> result = new ArrayList<>();
        try (var connection = dataSource.getConnection();
             var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, name);
            var resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                var id = resultSet.getLong("id");
                var createdAt = resultSet.getTimestamp("created_at");
                Url url = new Url(name);
                url.setId(id);
                url.setCreatedAt(createdAt);
                result.add(url);
                return result;
            }
            return result;
        }
    }

    public static void save(Url url) throws SQLException {
        String sql = "INSERT INTO urls (name, created_at) VALUES (?, ?)";
        Timestamp createdAt = new Timestamp(System.currentTimeMillis());
        try (var conn = dataSource.getConnection();
             var stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, url.getName());
            stmt.setTimestamp(2, createdAt);
            stmt.executeUpdate();
            var generatedKeys = stmt.getGeneratedKeys();
            // Устанавливаем ID в сохраненную сущность
            if (generatedKeys.next()) {
                url.setId(generatedKeys.getLong(1));
            } else {
                throw new SQLException("DB have not returned an id after saving an entity");
            }
        }
    }

    public static List<UrlsCheckPage> getUrls() throws SQLException {
        var sql = """
                SELECT DISTINCT ON (urls.id) urls.id,
                      urls.name,
                      url_checks.created_at,
                      url_checks.status_code
                      FROM urls             
                      LEFT JOIN url_checks ON urls.id = url_checks.url_id
                      ORDER BY urls.id, url_checks.created_at DESC                   
                """;
        try (var conn = dataSource.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            ResultSet resultSet = stmt.executeQuery();
            List<UrlsCheckPage> result = new ArrayList<>();
            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String urlName = resultSet.getString("name");
                Timestamp createdAt = resultSet.getTimestamp("created_at");
                int statusCode = resultSet.getInt("status_code");
                UrlsCheckPage urlsCheckPage = new UrlsCheckPage(id, urlName, createdAt, statusCode);
                urlsCheckPage.setId(id);
                result.add(urlsCheckPage);
            }
            return result;
        }
    }

    public static Optional<Url> find(Long id) throws SQLException {
        String sql = "SELECT * FROM urls WHERE id = ?";
        try (var conn = dataSource.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                String urlName = resultSet.getString("name");
                Timestamp createdAt = resultSet.getTimestamp("created_at");
                Url url = new Url(urlName);
                url.setId(id);
                url.setCreatedAt(createdAt);
                return Optional.of(url);
            }
            return Optional.empty();
        }
    }
}
