package hexlet.code.model;

import java.util.Date;

public class Url {
    private Long id;
    private String name;
    private Date createdAt;

    public Url(String name, Date createdAt) {
        this.name = name;
        this.createdAt = createdAt;
    }
}
