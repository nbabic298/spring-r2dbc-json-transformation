CREATE TABLE products (
    id bigserial,
    name varchar(30) NOT NULL,
    owner_id bigint,
    parameters json NOT NULL,
    PRIMARY KEY (id)
);