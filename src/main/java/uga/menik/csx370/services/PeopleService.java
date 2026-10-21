/**
Copyright (c) 2026 Sami Menik, PhD. All rights reserved.

This is a project developed by Dr. Menik to give the students an opportunity to apply database concepts learned in the class in a real world project. Permission is granted to host a running version of this software and to use images or videos of this work solely for the purpose of demonstrating the work to potential employers. Any form of reproduction, distribution, or transmission of the software's source code, in part or whole, without the prior written consent of the copyright owner, is strictly prohibited.
*/
package uga.menik.csx370.services;

import java.util.List;

import org.springframework.stereotype.Service;
import java.sql.SQLException;
import uga.menik.csx370.models.FollowableUser;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * This service contains people related functions.
 */
@Service
public class PeopleService {
    private final DataSource dataSource;

    @Autowired
    public PeopleService(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    /**
     * This function should query and return all users that 
     * are followable. The list should not contain the user 
     * with id userIdToExclude.
     */
    public List<FollowableUser> getFollowableUsers(String userIdToExclude) throws SQLException {
        // Write an SQL query to find the users that are not the current user.

        // Run the query with a datasource.
        // See UserService.java to see how to inject DataSource instance and
        // use it to run a query.

        // Use the query result to create a list of followable users.
        // See UserService.java to see how to access rows and their attributes
        // from the query result.
        // Check the following createSampleFollowableUserList function to see 
        // how to create a list of FollowableUsers.

        // Replace the following line and return the list you created.
        // Note the ? mark in the query. It is a place holder that we will later replace.
        final String sql = """
            select u.userId, u.firstName, u.lastName,
                case when f.followerUserId is not null then true
                    else false end as isFollowee,
                    (select max(p.postDate) from post p where p.userId = u.userId) as lastActiveDate
        from user u
        left join follow f
            on u.userId = f.followeeUserId
            and f.followerUserId = ?
        where u.userId != ?
        """;
        List<FollowableUser> followableUsers = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userIdToExclude);
            pstmt.setString(2, userIdToExclude);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String userId = rs.getString("userId");
                    String firstName = rs.getString("firstName");
                    String lastName = rs.getString("lastName");
                    boolean isFollowee = rs.getBoolean("isFollowee");
                    Timestamp lastPost = rs.getTimestamp("lastActiveDate");
                    String lastActiveDate = "Unknown";
                    if (lastPost != null) {
                        lastActiveDate = new SimpleDateFormat(
                            "MMM dd, yyyy, hh:mm a",
                            Locale.ENGLISH
                        ).format(lastPost);
                    }
                    FollowableUser user = new FollowableUser(
                        userId,
                        firstName,
                        lastName,
                        isFollowee,
                        lastActiveDate
                    );

                    followableUsers.add(user);

                }
            }
        }
        return followableUsers;
    }
    /**
     * Lets a user follow another user.
     */
    public void followUser(String followerId, String followeeId)
            throws SQLException {
        if (followerId.equals(followeeId)) {
            throw new IllegalArgumentException("Users cannot follow themselves.");
        }
        String sql = "insert into follow (followerUserId, followeeUserId) values (?, ?)";

        try (Connection conn = dataSource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, followerId);
            pstmt.setString(2, followeeId);

            pstmt.executeUpdate();
        }
    }

    /**
     * Lets a user unfollow another user.
     */
    public void unfollowUser(String followerId, String followeeId)
            throws SQLException {

        String sql = "delete from follow where followerUserId = ? and followeeUserId = ?";

        try (Connection conn = dataSource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, followerId);
            pstmt.setString(2, followeeId);

            pstmt.executeUpdate();
        }
    }


}
