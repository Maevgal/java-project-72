package hexlet.code.model;

import hexlet.code.dto.BasePage;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class UrlCheck extends BasePage {
    private Long id;
    private Long urlId;
    private int statusCode;
    private String title;
    private String h1;
    private String description;
    private Timestamp createdAt;

    public UrlCheck(int statusCode, String title, String h1, String description) {
        this.statusCode = statusCode;
        this.title = title;
        this.h1 = h1;
        this.description = description;
    }
}
