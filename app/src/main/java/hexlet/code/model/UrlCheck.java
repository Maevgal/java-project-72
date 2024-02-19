package hexlet.code.model;

import hexlet.code.dto.BasePage;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
@Getter
@Setter
public class UrlCheck extends BasePage {
    private Long id;
    private Long url_id;
    private int status_code;
    private String title;
    private String h1;
    private String description;
    private Timestamp createdAt;

    public UrlCheck(int status_code, String title, String h1, String description) {
        this.status_code = status_code;
        this.title = title;
        this.h1 = h1;
        this.description = description;
    }
}
