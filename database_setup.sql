-- Database setup: DDL for every table + demo data.
-- IMPORTANT: Every demo account below uses the password:  password 
--
-- If you already ran an older version of this file, start fresh first:
--     drop database if exists csx370_mb_platform;
-- (create table if not exists will NOT update a table that already exists.)

create database if not exists csx370_mb_platform;

use csx370_mb_platform;


-- user table.
create table if not exists user (
    userId int auto_increment,
    username varchar(255) not null,
    password varchar(255) not null,
    firstName varchar(255) not null,
    lastName varchar(255) not null,
    primary key (userId),
    unique (username),
    constraint userName_min_length check (char_length(trim(userName)) >= 2),
    constraint firstName_min_length check (char_length(trim(firstName)) >= 2),
    constraint lastName_min_length check (char_length(trim(lastName)) >= 2)
);

-- post table.
create table if not exists post (
    postId int auto_increment,
    userId int not null,
    content varchar(255) not null,
    postDate datetime not null default current_timestamp,
    postType ENUM('NORMAL', 'POLL') not null,
    primary key (postId),
    foreign key (userId) references user(userId)
);

-- poll table.
create table if not exists poll (
    pollId int auto_increment,
    postId int not null unique,
    question varchar(255) not null,
    choice1 varchar(255) not null,
    choice2 varchar(255) not null,
    primary key (pollId),
    foreign key (postId) references post(postId)
);

-- pollvote table (one vote per user per poll).
create table if not exists pollvote (
    pollId int not null,
    userId int not null,
    choice int not null,
    primary key (pollId, userId),
    foreign key (pollId) references poll(pollId),
    foreign key(userId) references user(userId),
    CHECK (choice in (1,2))
);

-- hashtag table.
create table if not exists hashtag (
    hashtagId int auto_increment,
    hashtag varchar(255) not null,
    primary key (hashtagId),
    unique (hashtag)
);

-- relationship between posts and hashtags.
create table if not exists postHashtag (
    postId int not null,
    hashtagId int not null,
    primary key (postId, hashtagId),
    foreign key (postId) references post(postId),
    foreign key (hashtagId) references hashtag(hashtagId)
);

-- comment table.
create table if not exists comment (
    commentId int auto_increment,
    postId int not null,
    userId int not null,
    content varchar(255) not null,
    commentDate datetime not null default current_timestamp,
    primary key (commentId),
    foreign key (postId) references post(postId),
    foreign key (userId) references user(userId),
    constraint comment_not_empty check (char_length(trim(content)) > 0)
);

-- heart (like) table. One heart per user per post.
-- Heart and comment counts are NOT stored; they are counted from these tables.
create table if not exists heart (
    postId int not null,
    userId int not null,
    primary key (postId, userId),
    foreign key (postId) references post(postId),
    foreign key (userId) references user(userId)
);

-- bookmark table. One bookmark per user per post.
create table if not exists bookmark (
    postId int not null,
    userId int not null,
    primary key (postId, userId),
    foreign key (postId) references post(postId),
    foreign key (userId) references user(userId)
);

-- follow table. followerUserId follows followeeUserId.
create table if not exists follow (
    followerUserId int not null,
    followeeUserId int not null,
    primary key (followerUserId, followeeUserId),
    foreign key (followerUserId) references user(userId),
    foreign key (followeeUserId) references user(userId),
    constraint no_self_follow check (followerUserId <> followeeUserId)
);


-- ---------------------------------------------------------------------------
-- Demo data
-- ---------------------------------------------------------------------------

-- Users (password: password).
insert into user (userId, username, password, firstName, lastName) values
    (1, 'mthompson', '$2a$10$mUVDDr4KDe4RMr5MjzfzBuNTEN86yn2IzwTww3IbsnXPMjaE/J/j.', 'Maya', 'Thompson'),
    (2, 'jcarter',   '$2a$10$4vCRjTw3ZpvcBsud1oz8re6DFTBJFH18ziV0kgohd0nKx2o94hzki', 'Jordan', 'Carter'),
    (3, 'pkapoor',   '$2a$10$tz1vZ.xSqSwvOtU1fpkkdusuFgHAxOM4e8OhjseXuvkXaAK01g49O', 'Priya', 'Kapoor'),
    (4, 'dnguyen',   '$2a$10$uoTPZinH10lzTNRBUK1BzuiirYDhDEKWEGjCAWGtYVrO.Admc70Kq', 'Danny', 'Nguyen'),
    (5, 'sreyes',    '$2a$10$EkJX9NrvbLk/cEmQDzFpVe0f45ZO0RzmsxXL5AQLA/xQHrVoeDluK', 'Sofia', 'Reyes'),
    (6, 'bwilliams', '$2a$10$VVvr.7PK99ulTuywdJxlSOtmIEw4hj6oKDeT2RyzIpvQpv6SdQPmO', 'Brandon', 'Williams'),
    (7, 'egarcia',   '$2a$10$c32ymucSoM1s4IVqs8HSyezFBIehzUwmpaJORIJPLLaz04c4wbqjW', 'Elena', 'Garcia'),
    (8, 'khall',     '$2a$10$.LnJen2raM5TnqtoEFOl2eTC/7kcrcL7L1LBDW/ld//SiYtlVRbjK', 'Kevin', 'Hall');

-- Posts. Poll posts store the poll question as their content (same as PollService does).
insert into post (postId, userId, content, postDate, postType) values
    (1,  1, 'Welcome to the platform! Excited to see what everyone posts this semester #uga #csci4370', '2026-10-05 09:15:00', 'NORMAL'),
    (2,  2, 'Just finished normalizing our schema to 3NF. Never thought I would enjoy functional dependencies this much #databases #csci4370', '2026-10-05 14:40:00', 'NORMAL'),
    (3,  3, 'Best study spot on campus? Main library is packed again #uga #studygroup', '2026-10-06 10:05:00', 'NORMAL'),
    (4,  4, 'Game day in Athens hits different. Go Dawgs! #gamedawgs #football #athens', '2026-10-06 18:30:00', 'NORMAL'),
    (5,  5, 'Pro tip: index your foreign key columns before you blame the ORM #databases', '2026-10-07 11:20:00', 'NORMAL'),
    (6,  6, 'Tabs or spaces?', '2026-10-07 16:45:00', 'POLL'),
    (7,  7, 'Cold brew is the only reason I survived this week #coffee #athens', '2026-10-08 08:10:00', 'NORMAL'),
    (8,  8, 'Anyone want to form a study group for the midterm? Thinking Thursday at 6 #studygroup #csci4370', '2026-10-08 13:10:00', 'NORMAL'),
    (9,  3, 'Finally got our Spring Boot app talking to MySQL. Only took three port conflicts #springboot #csci4370', '2026-10-09 20:30:00', 'NORMAL'),
    (10, 5, 'Best lunch near campus?', '2026-10-10 12:00:00', 'POLL'),
    (11, 2, 'Normal forms cheat sheet: 1NF atomic values, 2NF no partial dependencies, 3NF no transitive dependencies #databases #studygroup', '2026-10-10 14:30:00', 'NORMAL'),
    (12, 6, 'Sanford Stadium was electric tonight. What a comeback #gamedawgs #football', '2026-10-11 22:15:00', 'NORMAL'),
    (13, 4, 'New coffee shop downtown makes the best oat milk latte in Athens #coffee #athens', '2026-10-12 09:00:00', 'NORMAL'),
    (14, 1, 'Wrote my first correlated subquery today and it actually worked #databases #csci4370', '2026-10-13 16:20:00', 'NORMAL'),
    (15, 8, 'Midterm review: in person or Zoom? #studygroup', '2026-10-14 10:00:00', 'POLL'),
    (16, 5, 'The leaves on North Campus are finally changing. Fall in Athens is unmatched #athens #uga', '2026-10-15 15:00:00', 'NORMAL');

-- Polls (one per POLL post; question matches the post content).
insert into poll (pollId, postId, question, choice1, choice2) values
    (1, 6,  'Tabs or spaces?', 'Tabs', 'Spaces'),
    (2, 10, 'Best lunch near campus?', 'Pizza', 'Tacos'),
    (3, 15, 'Midterm review: in person or Zoom? #studygroup', 'In person', 'Zoom');

-- Poll votes (choice is 1 or 2).
-- mthompson (userId 1) has NOT voted on poll 2 so voting can be demoed from her home feed.
insert into pollvote (pollId, userId, choice) values
    (1, 1, 2), (1, 2, 2), (1, 3, 1), (1, 4, 2), (1, 5, 1), (1, 7, 2),
    (2, 3, 2), (2, 4, 1), (2, 6, 2), (2, 8, 1),
    (3, 1, 1), (3, 2, 1), (3, 3, 2), (3, 5, 1), (3, 8, 1);

-- Hashtags that appear in the demo posts (lowercase, without #).
insert into hashtag (hashtagId, hashtag) values
    (1, 'uga'),
    (2, 'csci4370'),
    (3, 'databases'),
    (4, 'studygroup'),
    (5, 'gamedawgs'),
    (6, 'football'),
    (7, 'athens'),
    (8, 'coffee'),
    (9, 'springboot');

-- Link each post to exactly the hashtags in its content.
insert into postHashtag (postId, hashtagId) values
    (1, 1), (1, 2),
    (2, 3), (2, 2),
    (3, 1), (3, 4),
    (4, 5), (4, 6), (4, 7),
    (5, 3),
    (7, 8), (7, 7),
    (8, 4), (8, 2),
    (9, 9), (9, 2),
    (11, 3), (11, 4),
    (12, 5), (12, 6),
    (13, 8), (13, 7),
    (14, 3), (14, 2),
    (15, 4),
    (16, 7), (16, 1);

-- Comments.
insert into comment (commentId, postId, userId, content, commentDate) values
    (1,  1,  2, 'Glad to be here!', '2026-10-05 10:00:00'),
    (2,  1,  3, 'Looking forward to it', '2026-10-05 11:30:00'),
    (3,  2,  5, 'BCNF next, then you are unstoppable', '2026-10-05 16:10:00'),
    (4,  3,  8, 'Science Library 2nd floor, always quiet', '2026-10-06 11:00:00'),
    (5,  3,  1, 'Second the Science Library', '2026-10-06 12:20:00'),
    (6,  4,  6, 'GO DAWGS!', '2026-10-06 19:00:00'),
    (7,  6,  3, 'Spaces, and I will not be taking questions', '2026-10-07 17:30:00'),
    (8,  8,  3, 'Count me in', '2026-10-08 14:00:00'),
    (9,  8,  4, 'I can do Thursday. Which room?', '2026-10-08 15:45:00'),
    (10, 8,  8, 'Booking a room in the library, will post details', '2026-10-08 16:30:00'),
    (11, 9,  2, 'Congrats! Which port did you end up using?', '2026-10-09 21:00:00'),
    (12, 11, 4, 'Saving this for the exam', '2026-10-10 16:00:00'),
    (13, 14, 5, 'Wait until you try window functions', '2026-10-13 18:00:00'),
    (14, 15, 1, 'In person please, Zoom review sessions never work for me', '2026-10-14 11:00:00'),
    (15, 16, 7, 'Took a walk there today, so pretty', '2026-10-15 17:00:00');

-- Hearts (likes).
insert into heart (postId, userId) values
    (1, 2), (1, 3), (1, 4), (1, 5),
    (2, 1), (2, 3), (2, 5),
    (3, 8),
    (4, 1), (4, 5), (4, 6), (4, 7), (4, 8),
    (5, 2), (5, 4),
    (6, 1), (6, 3),
    (7, 6),
    (8, 3), (8, 4),
    (9, 2), (9, 5),
    (10, 3),
    (11, 1), (11, 3), (11, 4), (11, 8),
    (12, 4), (12, 8),
    (13, 7),
    (14, 3), (14, 5), (14, 8),
    (15, 3),
    (16, 1), (16, 2), (16, 3), (16, 7);

-- Bookmarks.
insert into bookmark (postId, userId) values
    (5, 1), (9, 1), (11, 1), (15, 1),
    (9, 2),
    (2, 3), (8, 3),
    (5, 4), (11, 4),
    (3, 8);

-- Follows (followerUserId follows followeeUserId).
insert into follow (followerUserId, followeeUserId) values
    (1, 2), (1, 3), (1, 4), (1, 5),
    (2, 1), (2, 3), (2, 5),
    (3, 1), (3, 2), (3, 8),
    (4, 1), (4, 5), (4, 6),
    (5, 2), (5, 3),
    (6, 4), (6, 5),
    (7, 1), (7, 8),
    (8, 3), (8, 4), (8, 7);
