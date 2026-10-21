
----------------------------
-- Login and Registration --
----------------------------

-- Finds a user with matching username from the user table. Used to authenticate when logging in.
-- http://localhost:8081/login
select * from user where username = ?;


-- Registers a new user in the user table.
-- http://localhost:8081/register
insert into user (username, password, firstName, lastName) values (?, ?, ?, ?);


------------
-- Follow --
------------

-- Selects all the users that are not the current user. Used to get a followable users list.
-- http://localhost:8081/people

select u.userId, u.firstName, u.lastName,
    case when f.followerUserId is not null then true
        else false end as isFollowee,
        (select max(p.postDate) from post p where p.userId = u.userId) as lastActiveDate
from user u
left join follow f on u.userId = f.followeeUserId and f.followerUserId = ?
where u.userId != ?;


-- Inserts a new follow relationship into follow table. Used when user follows another user.
-- http://localhost:8081/people
insert into follow (followerUserId, followeeUserId) values (?, ?);


-- Deletes a follow relationship from the follow table. Used when user unfollows a user.
-- http://localhost:8081/people
delete from follow where followerUserId = ? and followeeUserId = ?;


--------------
-- Hashtags --
--------------

-- Finds all posts that contains all the #s in its content. Used when searching hashtags.
-- WILL REPLACE WITH EXAMPLE URL FROM DEMO DATABASE_SETUP:
-- http://localhost:8081/hashtagsearch?hashtags=%23spade
select p.postId, p.content, p.postDate, p.postType, u.userId, u.firstName, u.lastName, poll.pollId, poll.choice1, poll.choice2 
from post p 
left join poll on poll.postId = p.postId 
join user u on p.userId = u.userId 
join postHashtag t on p.postId = t.postId 
join hashtag h on t.hashtagId = h.hashtagId 
where h.hashtag in (?, ?, ...) -- each placeholder represents a tag, so it can be any number of tags (at least one) the user writes in the search bar
group by p.postId, p.content, p.postDate, u.userId, u.firstName, u.lastName, poll.pollId, poll.choice1, poll.choice2 
having count(distinct h.hashtag) = %d order by p.postDate desc; -- %d is the number of tags



---------------------------------
-- Post and Hashtag extraction --
---------------------------------

-- Inserts a new normal post into post table, with postType NORMAL. Executed when users create a new post
-- http://localhost:8081/#postModal
insert into post(userId, content, postType) values (?, ?, 'NORMAL');


-- Inserts a new hashtag into the hashtag table. Executed when users create a new post with a #. Executes after the above statement.
-- http://localhost:8081/#postModal
insert into hashtag(hashtag) values (?) on duplicate key update hashtagId = last_insert_id(hashtagId);


-- Inserts into postHashtag. Executed after the above statement to connect a post with a hashtagId.
-- http://localhost:8081/#postModal
insert into postHashtag(postId, hashtagId) values (?, ?);



------------------------------
-- Poll Creation and Voting --
------------------------------

-- Inserts a new post into post table, with postType Poll. Runs before the statement below.
-- http://localhost:8081/#pollModal
insert into post(userId, content, postType) values (?, ?, 'POLL');


-- Inserts a new poll into the poll table, with the same postId as the above statement. Executes when users create a new poll.
-- http://localhost:8081/#pollModal
insert into poll(postId, question, choice1, choice2) values (?, ?, ?, ?);


-- Inserts a new pollvote into the pollvote table. Executes when user votes on a poll (clicks on one of the choice buttons)
-- http://localhost:8081/
insert into pollvote (pollId, userId, choice) values (?, ?, ?);



----------------------
-- Displaying Posts --
----------------------

-- Selects all posts (and polls) created by current user. Executes on the profile page.
-- http://localhost:8081/profile
select p.postId, p.content, p.postDate, p.postType, u.userId, u.firstName, u.lastName, poll.pollId, poll.choice1, poll.choice2
from post p 
join user u on p.userId = u.userId 
left join poll on poll.postId = p.postId 
where p.userId = ?          -- current user's userId
order by p.postDate desc;


-- Selects all posts (and polls) created by current user or by users that the current user follows. Executes on the home page.
-- http://localhost:8081/
select p.postId, p.content, p.postDate, u.userId, u.firstName, u.lastName, p.postType, po.pollId, po.choice1, po.choice2 
from post p 
join user u on p.userId = u.userId 
left join poll po on po.postId = p.postId 
where (p.userId = ? or exists (             -- current user's userId
    select 1 from follow f where f.followerId = ? and f.followedId = p.userId))   -- and possibly users that the current user follows
order by p.postDate desc;