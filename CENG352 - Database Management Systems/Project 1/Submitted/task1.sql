CREATE TABLE Business (
                          business_id varchar PRIMARY KEY,
                          business_name text,
                          address text,
                          state varchar,
                          is_open bool,
                          stars float
);

CREATE TABLE Users (
                       user_id varchar PRIMARY KEY,
                       user_name text,
                       review_count integer,
                       yelping_since timestamp,
                       useful integer,
                       funny integer,
                       cool integer,
                       fans integer,
                       average_stars float
);

CREATE TABLE Friend (
                        user_id1 varchar,
                        user_id2 varchar,
                        PRIMARY KEY (user_id1, user_id2),
                        FOREIGN KEY (user_id1) REFERENCES users (user_id),
                        FOREIGN KEY (user_id2) REFERENCES users (user_id)
);

CREATE TABLE Review (
                        review_id varchar PRIMARY KEY ,
                        user_id varchar,
                        business_id varchar,
                        stars float,
                        date timestamp,
                        useful integer,
                        funny integer,
                        cool integer,
                        FOREIGN KEY (user_id) REFERENCES users (user_id),
                        FOREIGN KEY (business_id) REFERENCES business (business_id)
);

CREATE TABLE Tip (
                     tip_id serial PRIMARY KEY,
                     business_id varchar,
                     user_id varchar,
                     date timestamp,
                     compliment_count integer,
                     tip_text text,
                     FOREIGN KEY (business_id) REFERENCES business (business_id),
                     FOREIGN KEY (user_id) REFERENCES users(user_id)
);

COPY business(business_id, business_name, address, state, is_open, stars)
FROM ''
DELIMITER ','
CSV HEADER;

COPY users(user_id, user_name, review_count, yelping_since, useful, funny, cool, fans, average_stars)
FROM ''
DELIMITER ','
CSV HEADER;

COPY friend(user_id1, user_id2)
FROM ''
DELIMITER ','
CSV HEADER;


COPY review(review_id, user_id, business_id, stars, date, useful, funny, cool)
FROM ''
DELIMITER ','
CSV HEADER;

COPY tip(tip_text, date, compliment_count, business_id, user_id)
FROM ''
DELIMITER ','
CSV HEADER;
