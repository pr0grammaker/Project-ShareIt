CREATE TABLE IF NOT EXISTS users (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS items (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    owner_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    available BOOLEAN NOT NULL,
    request_id BIGINT,

    CONSTRAINT fk_items_owner FOREIGN KEY (owner_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS bookings (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    booking_start TIMESTAMP NOT NULL,
    booking_end TIMESTAMP NOT NULL,
    item_id BIGINT NOT NULL,
    booker_id BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL,

    CONSTRAINT fk_bookings_item FOREIGN KEY (item_id) REFERENCES items(id),
    CONSTRAINT fk_bookings_booker FOREIGN KEY (booker_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS comments (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id BIGINT NOT NULL,
    item_id BIGINT NOT NULL,
    text VARCHAR(2000) NOT NULL,
    created TIMESTAMP NOT NULL,

    CONSTRAINT fk_comments_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_comments_item FOREIGN KEY (item_id) REFERENCES items(id)
);



