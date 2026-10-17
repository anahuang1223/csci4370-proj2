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
import javax.xml.crypto.Data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import uga.menik.csx370.models.Post;
import uga.menik.csx370.utility.Utility;

/**
 * This service contains hashtag search bar related functions.
 */
@Service
public class HashtagService {
    
    private final DataSource dataSource;

    /**
     * See AuthInterceptor notes regarding dependency injection and
     * inversion of control.
     */
    @Autowired
    public HashtagService(DataSource dataSource) {
        this.dataSource = dataSource;
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

        // then writing the SQL query

        String query = "select distinct p.postId, p.content, p.postDate, u.userId, u.firstName, u.lastName from post p join user u on p.userId = u.userId where p.postType = 'NORMAL' and p.postId in (";

        String subQuery = "select postId from post p join postHashtag t on p.postId = t.postId join hashtag h on t.hashtagId = h.hashtagId where p.postType = 'NORMAL'";

        for (String tag : tagsList) {
            subQuery += " and h.hashtagId = ?";
        }

        final String finalQuery = query + subQuery + ") order by p.postDate desc;";

        // Running the query with a datasource

        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            int i = 1;

            for (String tag: tagsList) {
                pstmt.setString(i, tag);
                i++;
            }

            System.out.println("(DEBUG) Querying Hashtags: " + pstmt.toString());

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
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
        } catch (SQLException e) {
            System.out.println("SQL querying error");
        }

        return posts;
    }

}

