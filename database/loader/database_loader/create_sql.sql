CREATE EXTENSION IF NOT EXISTS citext;

CREATE TABLE users
(
    user_id serial primary key,
    nickname CITEXT COLLATE ucs_basic NOT NULL UNIQUE,
    email CITEXT NOT NULL UNIQUE,
    fullname TEXT NOT NULL,
    about TEXT NOT NULL
);

CREATE INDEX users_nickname_idx
  ON users (nickname);

CREATE INDEX users_email_idx
  ON users (email);

CREATE TABLE forums
(
    forum_id serial primary key,
    posts int DEFAULT 0,
    slug CITEXT NOT NULL UNIQUE,
    threads int DEFAULT 0,
    user_id int NOT NULL,
    user_nickname CITEXT NOT NULL,
    title TEXT NOT NULL
);

CREATE INDEX forums_slug_idx
  ON forums (slug);

CREATE INDEX forums_users_idx
  ON forums (user_id);

ALTER TABLE forums
    ADD CONSTRAINT forums_fk_users
    FOREIGN KEY(user_id) REFERENCES users(user_id);

CREATE TABLE threads
(
    thread_id serial primary key,
    slug CITEXT DEFAULT NULL UNIQUE,
    forum_id int NOT NULL,
    forum CITEXT NOT NULL,
    user_id int NOT NULL,
    author CITEXT NOT NULL,
    created TIMESTAMP WITH TIME ZONE DEFAULT NULL,
    message TEXT NOT NULL,
    title TEXT NOT NULL,
    votes int DEFAULT 0
);

CREATE INDEX threads_slug_idx
  ON threads (slug);

CREATE INDEX threads_forums_idx
  ON threads (forum_id);

ALTER TABLE threads
    ADD CONSTRAINT threads_fk_forums
    FOREIGN KEY(forum_id) REFERENCES forums(forum_id);

ALTER TABLE threads
    ADD CONSTRAINT threads_fk_users
    FOREIGN KEY(user_id) REFERENCES users(user_id);

CREATE TABLE posts
(
    post_id serial primary key,
    thread_id int NOT NULL,
    thread CITEXT DEFAULT NULL,
    forum_id int NOT NULL,
    forum CITEXT NOT NULL,
    user_id int NOT NULL,
    author CITEXT NOT NULL,
    parent_id int NOT NULL,
    message TEXT NOT NULL,
    created TIMESTAMP WITH TIME ZONE DEFAULT NULL,
    is_edited BOOLEAN NOT NULL,
    m_path int []
);

CREATE INDEX posts_threads_idx
  ON posts (thread_id);

CREATE INDEX posts_parent_idx
  ON posts (parent_id);

ALTER TABLE posts
    ADD CONSTRAINT posts_fk_threads
    FOREIGN KEY(thread_id) REFERENCES threads(thread_id);

ALTER TABLE posts
    ADD CONSTRAINT posts_fk_forums
    FOREIGN KEY(forum_id) REFERENCES forums(forum_id);

ALTER TABLE posts
    ADD CONSTRAINT posts_fk_users
    FOREIGN KEY(user_id) REFERENCES users(user_id);


CREATE TABLE vote
(
    vote_id serial primary key,
    thread_id int NOT NULL,
    user_id int NOT NULL,
    vote_value int NOT NULL,
    nickname CITEXT NOT NULL
);

CREATE INDEX vote_users_idx
  ON vote (user_id);

CREATE INDEX vote_threads_idx
  ON vote (thread_id);

ALTER TABLE vote
    ADD CONSTRAINT vote_fk_users
    FOREIGN KEY(user_id) REFERENCES users(user_id);

ALTER TABLE vote
    ADD CONSTRAINT vote_fk_threads
    FOREIGN KEY(thread_id) REFERENCES threads(thread_id);

ALTER TABLE vote
    ADD CONSTRAINT thread_user_column UNIQUE
    (
        thread_id,
        user_id
    );
