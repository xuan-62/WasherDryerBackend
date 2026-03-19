CREATE TABLE IF NOT EXISTS `user` (
    user_id  VARCHAR(255) NOT NULL,
    email    VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    PRIMARY KEY (user_id)
);

CREATE TABLE IF NOT EXISTS `background` (
    user_id    VARCHAR(255) NOT NULL,
    about_me   VARCHAR(255),
    first_name VARCHAR(255),
    last_name  VARCHAR(255),
    email      VARCHAR(255),
    PRIMARY KEY (user_id),
    FOREIGN KEY (user_id) REFERENCES `user`(user_id)
);

CREATE TABLE IF NOT EXISTS `item` (
    item_id        VARCHAR(255) NOT NULL,
    type           VARCHAR(255),
    address        VARCHAR(255),
    user_id        VARCHAR(255),
    item_condition VARCHAR(255),
    model          VARCHAR(255),
    brand          VARCHAR(255),
    PRIMARY KEY (item_id),
    FOREIGN KEY (user_id) REFERENCES `user`(user_id)
);

CREATE TABLE IF NOT EXISTS `reservation` (
    user_id    VARCHAR(255) NOT NULL,
    item_id    VARCHAR(255) NOT NULL,
    start_time TIMESTAMP    NOT NULL,
    end_time   TIMESTAMP    NOT NULL,
    PRIMARY KEY (user_id, item_id),
    FOREIGN KEY (user_id) REFERENCES `user`(user_id),
    FOREIGN KEY (item_id) REFERENCES `item`(item_id)
);
