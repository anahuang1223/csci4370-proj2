/**
Copyright (c) 2026 Sami Menik, PhD. All rights reserved.

This is a project developed by Dr. Menik to give the students an opportunity to apply database concepts learned in the class in a real world project. Permission is granted to host a running version of this software and to use images or videos of this work solely for the purpose of demonstrating the work to potential employers. Any form of reproduction, distribution, or transmission of the software's source code, in part or whole, without the prior written consent of the copyright owner, is strictly prohibited.
*/
package uga.menik.csx370.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import uga.menik.csx370.models.Poll;
import uga.menik.csx370.models.Post;
import uga.menik.csx370.models.User;


/**
 * This service contains hashtag search bar related functions.
 */
@Service
public class HashtagService {
    
    private final DataSource dataSource;
    private final PollService pollService;

    /**
     * See AuthInterceptor notes regarding dependency injection and
     * inversion of control.
     */
    @Autowired
    public HashtagService(DataSource dataSource, PollService pollService) {
        this.dataSource = dataSource;
        this.pollService = pollService;
    }
    

    /**
     * This is a helper function to format datetime
     */
    private String formatPostDate(Timestamp timestamp) {

    DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern(
                    "MMM dd, yyyy, hh:mm a");

    return timestamp
            .toLocalDateTime()
            .format(formatter);
}

    /**
     * This function queries and returns all posts
     * that have the searched hashtags.
     */
    public List<Post> getMatchingTagPosts(String hashtags) {

        List<Post> posts = new ArrayList<>();

        // Parsing the hashtags string if it contains multiple tags:

        if (hashtags.equals("")) {
            return posts;
        }

        // first removing all spaces then splitting separate tags by hash to get tags in a list

        String tags = hashtags.replaceAll("\\s", "");
        List<String> tagsList = new ArrayList<>(Arrays.asList(tags.split("#")));

        // first split will contain an empty string so we remove it

        tagsList.removeIf(str -> "".equals(str));

        // then writing the SQL query:

        String query = "select p.postId, p.content, p.postDate, p.postType, u.userId, u.firstName, u.lastName, poll.pollId, poll.choice1, poll.choice2 " + 
        "from post p left join poll on poll.postId = p.postId join user u on p.userId = u.userId join postHashtag t on p.postId = t.postId join hashtag h on t.hashtagId = h.hashtagId where h.hashtag in (";

        int numTags =  tagsList.size();

        // generating required number of ? placeholders for hashtags

        String subQuery = "";

        for (int i=0; i<numTags; i++) {
            if (i==0) {
                subQuery += "?";
            } else {
                subQuery += ",?";
            }
        }

        // using group by so we can count distinct tags and set that equal to the original number of tags searched, that is to say, it is verifying the presence of all tags searched

        String endQuery = String.format(") group by p.postId, p.content, p.postDate, u.userId, u.firstName, u.lastName, poll.pollId, poll.choice1, poll.choice2 having count(distinct h.hashtag) = %d order by p.postDate desc;", numTags);

        query = query + subQuery + endQuery;
        
        final String finalQuery = query;

        // Running the query with a datasource

        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(finalQuery)) {

            int i = 1;

            for (String tag: tagsList) {
                pstmt.setString(i, tag);
                i++;
            }

            // print final query to console for debugging
            System.out.println("(DEBUG) Final Query: " + pstmt.toString());

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    if (rs.getString("postType").equals("POLL")) {
                        System.out.println("Trying to get Poll for hashtag!");
                        String pollId = rs.getString("pollId");
                        String postId = rs.getString("postId"); 
                        Timestamp postDate = rs.getTimestamp("postDate");
                        String content = rs.getString("content");
                        String choice1 = rs.getString("choice1");
                        String choice2 = rs.getString("choice2");
                        String userId = rs.getString("userID");
                        String firstName = rs.getString("firstName");
                        String lastName = rs.getString("lastName");
                        User user = new User(userId, firstName, lastName);
                        
                        posts.add(new Poll(pollId, postId, content, choice1, choice2, formatPostDate(postDate), 0, 0, false, false,
                            user, pollService.getTotalVotes(rs.getString("pollId"))));
                    } else {
                        System.out.println("Getting a post for hashtag!");
                        String postId = rs.getString("postId");
                        String content = rs.getString("content");
                        Timestamp postDate = rs.getTimestamp("postDate");
                        String userID = rs.getString("userID");
                        String firstName = rs.getString("firstName");
                        String lastName = rs.getString("lastName");
                        User user = new User(userID, firstName, lastName);
                        posts.add(new Post(postId, content, formatPostDate(postDate), user, 0, 0, false, false));
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("SQL querying error" + e.getMessage());
        }

        return posts;
    }

}

