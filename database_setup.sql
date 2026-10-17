-- Create the database.
create database if not exists csx370_mb_platform;

-- Use the created database.
use csx370_mb_platform;

-- Create the user table.
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
-- Create the post table.
create table if not exists post (
    postId int auto_increment,
    userId int not null,
    content varchar(255) not null,
    postDate datetime not null default current_timestamp,
    postType ENUM('NORMAL', 'POLL') not null, 
    primary key (postId),
    foreign key (userId) references user(userId)
);

-- Create the poll table.
create table if not exists poll (
    pollId int auto_increment,
    postId int not null unique,
    question varchar(255) not null,
    choice1 varchar(255) not null,
    choice2 varchar(255) not null,
    primary key (pollId),
    foreign key (postId) references post(postId)
);

-- Create the pollvote table.
create table if not exists pollvote (
    pollId int not null,
    userId int not null,
    choice int not null,
    primary key (pollId, userId),
    foreign key (pollId) references poll(pollId),
    foreign key(userId) references user(userId),
    CHECK (choice in (1,2))
);

-- Create the hashtag table.
create table if not exists hashtag (
    hashtagId int auto_increment,
    hashtag varchar(255) not null,
    primary key (hashtagId),
    unique (hashtag)
);

-- Create the relationship between posts and hashtags.
create table if not exists postHashtag (
    postId int not null,
    hashtagId int not null,
    primary key (postId, hashtagId),
    foreign key (postId) references post(postId),
    foreign key (hashtagId) references hashtag(hashtagId)
);

-- Create the follow relationship between users.
create table if not exists follow (
    followerId int not null,
    followedId int not null,
    primary key (followerId, followedId),
    foreign key (followerId) references user(userId),
    foreign key (followedId) references user(userId),
    check (followerId <> followedId)
);