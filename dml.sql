
----------------------------
-- Login and Registration --
----------------------------

-- Finds a user with matching username from the user table. Used to authenticate when logging in.
-- http://localhost:8081/login
select * from user where username = ?;


-- Registers a new user in the user table.
-- http://localhost:8081/register
insert into user (username, password, firstName, lastName) values (?, ?, ?, ?);


---------------------
-- Followable User --
---------------------

-- Selects all the users that are not the current user. Used to get a followable users list.
-- http://localhost:8081/people
select * from user where userId <> ?;


--------------
-- Hashtags --
--------------

-- Finds all posts that contains all the #s in its content. Used when searching hashtags.
-- http://localhost:8081/hashtagsearch?hashtags=%23databases
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
    select 1 from follow f where f.followerUserId = ? and f.followeeUserId = p.userId))   -- and possibly users that the current user follows
order by p.postDate desc;


-----------------------------------------
-- Likes (Hearts), Bookmarks, Comments --
-----------------------------------------

-- Adds a heart (like) from the current user to a post. "ignore" skips it if the user already hearted that post.
-- http://localhost:8081/post/3/heart/true
insert ignore into heart (postId, userId) values (?, ?);


-- Removes the current user's heart from a post.
-- http://localhost:8081/post/3/heart/false
delete from heart where postId = ? and userId = ?;


-- Counts the hearts on a post. Runs for every post shown on the home, profile, bookmarks, hashtag search, and post pages.
-- http://localhost:8081/
select count(*) as heartCount from heart where postId = ?;


-- Checks whether the current user has hearted a post (filled vs. empty heart icon). Runs for every post shown.
-- http://localhost:8081/
select 1 from heart where postId = ? and userId = ?;


-- Bookmarks a post for the current user. "ignore" skips it if the user already bookmarked that post.
-- http://localhost:8081/post/3/bookmark/true
insert ignore into bookmark (postId, userId) values (?, ?);


-- Removes a post from the current user's bookmarks.
-- http://localhost:8081/post/3/bookmark/false
delete from bookmark where postId = ? and userId = ?;


-- Checks whether the current user has bookmarked a post (filled vs. empty bookmark icon). Runs for every post shown.
-- http://localhost:8081/
select 1 from bookmark where postId = ? and userId = ?;


-- Selects all posts (and polls) the current user has bookmarked, newest first. Executes on the bookmarks page.
-- http://localhost:8081/bookmarks
select p.postId, p.content, p.postDate, p.postType, u.userId, u.firstName, u.lastName, poll.pollId, poll.choice1, poll.choice2
from bookmark b
join post p on b.postId = p.postId
join user u on p.userId = u.userId
left join poll on poll.postId = p.postId
where b.userId = ?          -- current user's userId
order by p.postDate desc;


-- Adds a comment from the current user to a post (commentDate defaults to now). Submitted from the comment box on a post's page.
-- http://localhost:8081/post/8
insert into comment (postId, userId, content) values (?, ?, ?);


-- Selects all comments on a post with each commenter's name, oldest first. Executes on a post's page.
-- http://localhost:8081/post/8
select c.commentId, c.content, c.commentDate, u.userId, u.firstName, u.lastName
from comment c
join user u on c.userId = u.userId
where c.postId = ?
order by c.commentDate asc, c.commentId asc;


-- Counts the comments on a post. Runs for every post shown.
-- http://localhost:8081/
select count(*) as commentCount from comment where postId = ?;


-- Selects a single post (or poll) by its id. Executes on a post's page, which then loads its comments with the query above.
-- http://localhost:8081/post/8
select p.postId, p.content, p.postDate, p.postType, u.userId, u.firstName, u.lastName, poll.pollId, poll.choice1, poll.choice2
from post p
join user u on p.userId = u.userId
left join poll on poll.postId = p.postId
where p.postId = ?;