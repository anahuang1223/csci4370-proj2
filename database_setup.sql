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

/*
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
*/