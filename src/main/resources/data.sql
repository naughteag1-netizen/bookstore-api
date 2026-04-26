-- =========================
-- AUTHORS
-- =========================
INSERT INTO author (name, birthday) VALUES ('Robert C. Martin', '1952-12-05');
INSERT INTO author (name, birthday) VALUES ('Joshua Bloch', '1961-08-28');
INSERT INTO author (name, birthday) VALUES ('Martin Fowler', '1963-12-18');
INSERT INTO author (name, birthday) VALUES ('Eric Evans', '1965-01-01');
INSERT INTO author (name, birthday) VALUES ('Brian Goetz', '1969-02-19');

-- =========================
-- BOOKS
-- =========================
INSERT INTO book (isbn, title, publish_year, price, genre) VALUES ('9780132350884', 'Clean Code', 2008, 40.00, 'Programming');
INSERT INTO book (isbn, title, publish_year, price, genre) VALUES ('9780134685991', 'Effective Java', 2018, 55.00, 'Programming');
INSERT INTO book (isbn, title, publish_year, price, genre) VALUES ('9780201485677', 'Refactoring', 1999, 47.50, 'Programming');
INSERT INTO book (isbn, title, publish_year, price, genre) VALUES ('9780321125217', 'Domain-Driven Design', 2003, 60.00, 'Architecture');
INSERT INTO book (isbn, title, publish_year, price, genre) VALUES ('9780321356680', 'Java Concurrency in Practice', 2006, 52.00, 'Programming');
INSERT INTO book (isbn, title, publish_year, price, genre) VALUES ('9780134494166', 'Clean Architecture', 2017, 45.00, 'Architecture');

-- =========================
-- BOOK_AUTHORS (Many-to-Many)
-- =========================
INSERT INTO book_authors (book_isbn, author_id)
VALUES ('9780132350884',(SELECT id FROM author WHERE name = 'Robert C. Martin'));

INSERT INTO book_authors (book_isbn, author_id)
VALUES ('9780134685991', (SELECT id FROM author WHERE name = 'Joshua Bloch'));

INSERT INTO book_authors (book_isbn, author_id)
VALUES ('9780201485677', (SELECT id FROM author WHERE name = 'Martin Fowler'));

INSERT INTO book_authors (book_isbn, author_id)
VALUES ('9780321125217', (SELECT id FROM author WHERE name = 'Eric Evans'));

INSERT INTO book_authors (book_isbn, author_id)
VALUES ('9780321356680', (SELECT id FROM author WHERE name = 'Brian Goetz'));

INSERT INTO book_authors (book_isbn, author_id)
VALUES ('9780134494166', (SELECT id FROM author WHERE name = 'Robert C. Martin'));