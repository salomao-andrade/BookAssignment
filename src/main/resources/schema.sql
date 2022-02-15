CREATE TABLE Book
(
    id   INTEGER      NOT NULL AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL,
    isbn VARCHAR(100) NOT NULL,
    published_date DATE NOT NULL,
    PRIMARY KEY (id)
);
