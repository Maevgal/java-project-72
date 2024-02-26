package hexlet.code.repository;

import hexlet.code.model.UrlCheck;

import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class UrlCheckRepository extends BaseRepository {
    public static void save(UrlCheck urlcheck) throws SQLException {
        String sql = "INSERT INTO url_checks (url_id, status_code, title, h1, description, created_at) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        Timestamp createdAt = new Timestamp(System.currentTimeMillis());
        try (var conn = dataSource.getConnection();
             var stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, urlcheck.getUrlId());
            stmt.setInt(2, urlcheck.getStatusCode());
            stmt.setString(3, urlcheck.getTitle());
            stmt.setString(4, urlcheck.getH1());
            stmt.setString(5, urlcheck.getDescription());
            stmt.setTimestamp(6, createdAt);
            stmt.executeUpdate();
            var generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                urlcheck.setId(generatedKeys.getLong(1));
                urlcheck.setCreatedAt(createdAt);
            } else {
                throw new SQLException("DB have not returned an id after saving an entity");
            }
        }
    }

    public static List<UrlCheck> find(Long urlId) throws SQLException {
        String sql = "SELECT * FROM url_checks WHERE url_id = ?";
        List<UrlCheck> result = new ArrayList<>();
        try (var connection = dataSource.getConnection();
             var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, urlId);
            var resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                Integer statusCode = resultSet.getInt("status_code");
                String title = resultSet.getString("title");
                String h1 = resultSet.getString("h1");
                String description = resultSet.getString("description");
                Timestamp createdAt = resultSet.getTimestamp("created_at");
                UrlCheck urlCheck = new UrlCheck(statusCode, title, h1, description);
                urlCheck.setId(id);
                urlCheck.setUrlId(urlId);
                urlCheck.setCreatedAt(createdAt);
                result.add(urlCheck);
            }
            return result;
        }
    }
}
