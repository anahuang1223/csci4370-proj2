package uga.menik.csx370.utility;

import java.util.ArrayList;
import java.util.List;

import uga.menik.csx370.models.Comment;
import uga.menik.csx370.models.ExpandedPoll;
import uga.menik.csx370.models.ExpandedPost;
import uga.menik.csx370.models.FollowableUser;
import uga.menik.csx370.models.Poll;
import uga.menik.csx370.models.Post;
import uga.menik.csx370.models.User;

public class Utility {

    public static List<FollowableUser> createSampleFollowableUserList() {
        List<FollowableUser> followableUsers = new ArrayList<>();
        followableUsers.add(new FollowableUser("1", "Sach", "Doe",
                true, "Mar 07, 2026, 10:54 PM"));
        followableUsers.add(new FollowableUser("2", "Anna", "Doe",
                false, "Mar 05, 2026, 11:00 AM"));
        followableUsers.add(new FollowableUser("3", "Leen", "Smith",
                true, "Mar 06, 2026, 09:30 AM"));
        followableUsers.add(new FollowableUser("4", "Tyler", "Brown",
                false, "Mar 02, 2026, 08:15 PM"));
        return followableUsers;
    }

    public static List<Post> createSamplePostsListWithoutComments() {
        User user1 = new User("1", "John", "Doe");
        User user2 = new User("2", "Jane", "Doe");
        User user3 = new User("3", "Alice", "Smith");
        User user4 = new User("4", "Bob", "Brown");
        User user5 = new User("5", "Charlie", "Green");
        List<Post> postsWithoutComments = new ArrayList<>();
        postsWithoutComments.add(new Post("1", "Exploring Spring Boot features",
                "Mar 07, 2026, 10:54 PM", user1, 10, 4, false, false));
        postsWithoutComments.add(new Post("2", "Introduction to Microservices",
                "Mar 08, 2026, 11:00 AM", user2, 15, 6, true, true));
        postsWithoutComments.add(new Post("3", "Basics of Reactive Programming",
                "Mar 09, 2026, 09:30 AM", user3, 20, 3, true, false));
        return postsWithoutComments;
    }

    public static List<ExpandedPost> createSampleExpandedPostWithComments() {
        User user1 = new User("1", "John", "Doe");
        User user2 = new User("2", "Jane", "Doe");
        User user3 = new User("3", "Alice", "Smith");
        User user4 = new User("4", "Bob", "Brown");
        User user5 = new User("5", "Charlie", "Green");
        List<Comment> commentsForPost = new ArrayList<>();

        commentsForPost.add(new Comment("1", "Great insights, thanks for sharing!", 
            "Mar 07, 2026, 10:54 PM", user2));
        commentsForPost.add(new Comment("2", "I'm looking forward to trying this out.", 
            "Mar 08, 2026, 11:00 AM", user4));
        commentsForPost.add(new Comment("3", "Can you provide more examples in your next post?", 
            "Mar 09, 2026, 09:30 AM", user5));
        ExpandedPost postWithComments = new ExpandedPost("4", "Advanced Techniques " + 
            "in Spring Security", "Mar 10, 2026, 08:15 PM", user1, 25, 
            commentsForPost.size(), false, true, commentsForPost);
        return List.of(postWithComments);
    }

    // Sample Polls
    public static List<Post> createSamplePollsListWithoutComments() {
        User user1 = new User("1", "Ryu", "Naruhodo");
        User user2 = new User("2", "Susie", "Mikotoba");
        User user3 = new User("3", "Herlock", "Sholmes");
        User user4 = new User("4", "Iris", "Watson");
        User user5 = new User("5", "Theodore", "Roosevelt");
        List<Post> pollsWithoutComments = new ArrayList<>();
        // String pollId, String postId, String content, String choice1, String choice2, String postDate, 
        //       int heartsCount, int commentsCount, boolean isHearted, boolean isBookmarked, User user, int voteCount
        pollsWithoutComments.add(new Poll("1", "11", "Is it a spade or a shovel?", 
                "Spade", "Shovel", "Oct 15, 2026, 10:15 AM", 0, 0, false, false, user1, 0));
        
        pollsWithoutComments.add(new Post("2", "Exploring Spring Boot features",
                "Mar 07, 2026, 10:54 PM", user1, 10, 4, false, false));

        pollsWithoutComments.add(new Poll("3", "12", "Its definitely a shovel, right?", 
                "Yes, it's a shovel", "No, it's a spade", "Oct 15, 2026, 10:18 AM", 0, 0, false, false, user2, 0));
        
        pollsWithoutComments.add(new Poll("4", "13", "Is [SPOILERS] my dad?", 
                "Yes", "No", "Oct 16, 2026, 8:23 PM", 0, 0, false, false, user4, 0));
        


        return pollsWithoutComments;
    }


    // Sample Polls with COMMENTS
    public static List<ExpandedPoll> createSamplePollsListWithComments() {
        User user1 = new User("1", "Ryu", "Naruhodo");
        User user2 = new User("2", "Susie", "Mikotoba");
        User user3 = new User("3", "Herlock", "Sholmes");
        User user4 = new User("4", "Iris", "Watson");
        User user5 = new User("5", "Theodore", "Roosevelt");


        List<Comment> commentsForPoll = new ArrayList<>();
        commentsForPoll.add(new Comment("1", "Its a shovel!", 
            "Mar 07, 2026, 10:54 PM", user2));
        commentsForPoll.add(new Comment("2", "How do I post a comment?", 
            "Mar 08, 2026, 11:00 AM", user3));
        commentsForPoll.add(new Comment("3", "Isn't obvious?", 
            "Mar 09, 2026, 09:30 AM", user4));
        

        List<ExpandedPoll> pollsWithComments = new ArrayList<>();
        pollsWithComments.add(new ExpandedPoll("2", "11", "Is it a spade or a shovel?", 
                "Spade", "Shovel", "Oct 15, 2026, 10:15 AM", 0, 0, false, false, user1, 0, commentsForPoll));
        
        
        return pollsWithComments;
    }
    

}
