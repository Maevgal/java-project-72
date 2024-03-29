DROP TABLE IF EXISTS url_checks;
DROP TABLE IF EXISTS urls;

CREATE TABLE urls (
    id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(255),
    created_at timestamp
);

CREATE TABLE url_checks (
    id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    url_id bigint REFERENCES urls(id),
    status_code INT,
    title VARCHAR(255),
    h1 VARCHAR(255),
    description VARCHAR(255),
    created_at timestamp
);