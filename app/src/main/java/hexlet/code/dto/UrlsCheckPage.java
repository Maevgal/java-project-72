package hexlet.code.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@AllArgsConstructor
@Setter
public class UrlsCheckPage extends BasePage {
    private Long id;
    private String url;
    private Timestamp check;
    private int statusCode;
}
