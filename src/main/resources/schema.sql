CREATE TABLE if NOT EXISTS posts(
  id bigserial PRIMARY KEY,
  title text NOT NULL,
  text text NOT NULL,
  like_count int NOT NULL DEFAULT 0,
  tags JSON not null
 );

CREATE TABLE if NOT EXISTS comments(
  id bigserial PRIMARY KEY,
  text text NOT NULL,
  post_id bigint NOT NULL REFERENCES posts(id) ON DELETE CASCADE
 );