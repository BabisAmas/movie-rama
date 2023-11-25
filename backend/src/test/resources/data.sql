-- Inserting users
INSERT INTO "user" (id, date_joined, email, password, username, firstname, lastname) VALUES
(1, '2023-01-01 10:00:00', 'user1@example.com', '$2a$10$NdB4l5PuokLed8gSeY4wQ.2RCwHxHDYlYgahU5C6Of1YuDcyEk3Pi', 'user1', 'John', 'Doe'), --1TestPassword123#
(2, '2023-01-02 11:00:00', 'user2@example.com', '$2a$10$CEUCajSx9lmwXha6dvDYX.NVTjYEu9oAEAhd.cYh46TBJJDQhM.UW', 'user2', 'Jane', 'Doe'), --2TestPassword123#
(3, '2023-01-03 12:00:00', 'user3@example.com', '$2a$10$AJ0LDPnTN3EK0RNLsjVSh.r1s4vtCQyzM3w5kzwEp1YlE/UGekXMy', 'user3', 'Alice', 'Smith'), --3TestPassword123#
(4, '2023-01-04 13:00:00', 'user4@example.com', '$2a$10$6uGJXMS9MgQ2Oep7TxJpLeT/TrYGqdjzJU9RaJc5HsBaTKyGRJL6i', 'user4', 'Bob', 'Johnson'), --4TestPassword123#
(5, '2023-01-05 14:00:00', 'user5@example.com', '$2a$10$WRJOkMG/HuqR85JSxP8RUOswPxajixoEQrmmJTJWm3OofSkhEkecC', 'user5', 'Charlie', 'Brown'); --5TestPassword123#

-- Inserting movies
INSERT INTO `movie` (id, date_added, description, title, user_id) VALUES
(1, '2023-01-06 15:00:00', 'Description of Movie 1', 'Movie 1', 1),
(2, '2023-01-07 16:00:00', 'Description of Movie 2', 'Movie 2', 2),
(3, '2023-01-08 17:00:00', 'Description of Movie 3', 'Movie 3', 3),
(4, '2023-01-09 18:00:00', 'Description of Movie 4', 'Movie 4', 4),
(5, '2023-01-10 19:00:00', 'Description of Movie 5', 'Movie 5', 5),
(6, '2023-01-11 20:00:00', 'Description of Movie 6', 'Movie 6', 1),
(7, '2023-01-12 21:00:00', 'Description of Movie 7', 'Movie 7', 2),
(8, '2023-01-13 22:00:00', 'Description of Movie 8', 'Movie 8', 3),
(9, '2023-01-14 23:00:00', 'Description of Movie 9', 'Movie 9', 4),
(10, '2023-01-15 00:00:00', 'Description of Movie 10', 'Movie 10', 5);

-- Inserting votes
-- Votes for each movie
INSERT INTO `vote` (type, movie_id, user_id) VALUES
('LIKE', 1, 2), ('LIKE', 1, 3), ('LIKE', 1, 4), ('LIKE', 1, 5),
('HATE', 2, 1), ('HATE', 2, 3), ('HATE', 2, 4), ('HATE', 2, 5),
('LIKE', 3, 1), ('HATE', 3, 2), ('LIKE', 3, 4), ('HATE', 3, 5),
('LIKE', 4, 1), ('LIKE', 4, 2), ('HATE', 4, 3), ('LIKE', 4, 5),
('HATE', 5, 1), ('LIKE', 5, 2), ('LIKE', 5, 3), ('HATE', 5, 4),
('LIKE', 6, 2), ('LIKE', 6, 3), ('HATE', 6, 4), ('LIKE', 6, 5),
('HATE', 7, 1), ('LIKE', 7, 3), ('LIKE', 7, 4), ('HATE', 7, 5),
('LIKE', 8, 1), ('HATE', 8, 2), ('LIKE', 8, 4), ('LIKE', 8, 5),
('LIKE', 9, 1), ('LIKE', 9, 2), ('HATE', 9, 3), ('LIKE', 9, 5),
('HATE', 10, 1), ('HATE', 10, 2), ('LIKE', 10, 3), ('HATE', 10, 4);

-- Inserting movie counters based on votes
INSERT INTO `movie_counter` (id, hate_count, like_count) VALUES
(1, 0, 4), -- Movie 1: 0 hates, 4 likes
(2, 4, 0), -- Movie 2: 4 hates, 0 likes
(3, 2, 2), -- Movie 3: 2 hates, 2 likes
(4, 1, 3), -- Movie 4: 1 hate, 3 likes
(5, 2, 2), -- Movie 5: 2 hates, 2 likes
(6, 1, 3), -- Movie 6: 1 hate, 3 likes
(7, 2, 2), -- Movie 7: 2 hates, 2 likes
(8, 1, 3), -- Movie 8: 1 hate, 3 likes
(9, 1, 3), -- Movie 9: 1 hate, 3 likes
(10, 3, 1); -- Movie 10: 3 hates, 1 like
