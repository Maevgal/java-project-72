package hexlet.code.repository;

import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;

import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UrlCheckRepository extends BaseRepository{
    public static void save(UrlCheck urlcheck) throws SQLException {
        String sql = "INSERT INTO url_checks (url_id, status_code, title, h1, description, created_at) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        Timestamp createdAt = new Timestamp(System.currentTimeMillis());

        try (var conn = dataSource.getConnection();
             var stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, urlcheck.getUrl_id());
            stmt.setInt(2, urlcheck.getStatus_code());
            stmt.setString(3, urlcheck.getTitle());
            stmt.setString(4, urlcheck.getH1());
            stmt.setString(5, urlcheck.getDescription());
            stmt.setTimestamp(6,createdAt);
            stmt.executeUpdate();
            var generatedKeys = stmt.getGeneratedKeys();
            // Устанавливаем ID в сохраненную сущность
            if (generatedKeys.next()) {
                urlcheck.setId(generatedKeys.getLong(1));
                urlcheck.setCreatedAt(createdAt);
            } else {
                throw new SQLException("DB have not returned an id after saving an entity");
            }
        }
    }

    public static List<UrlCheck> find(Long url_id) throws SQLException {
        String sql = "SELECT * FROM url_checks WHERE url_id = ?";
        List<UrlCheck> result = new ArrayList<>();
        try (var connection = dataSource.getConnection();
             var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, url_id);
            var resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                var id = resultSet.getLong("id");
                var statusCode = resultSet.getInt("status_code");
                var title = resultSet.getString("title");
                var h1 = resultSet.getString("h1");
                var description = resultSet.getString("description");
                var createdAt = resultSet.getTimestamp("created_at");
                var urlCheck = new UrlCheck(statusCode, title, h1, description);
                urlCheck.setId(id);
                urlCheck.setUrl_id(url_id);
                urlCheck.setCreatedAt(createdAt);
                result.add(urlCheck);
            }
            return result;
        }
    }
}
