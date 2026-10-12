/**
Copyright (c) 2026 Sami Menik, PhD. All rights reserved.

This is a project developed by Dr. Menik to give the students an opportunity to apply database concepts learned in the class in a real world project. Permission is granted to host a running version of this software and to use images or videos of this work solely for the purpose of demonstrating the work to potential employers. Any form of reproduction, distribution, or transmission of the software's source code, in part or whole, without the prior written consent of the copyright owner, is strictly prohibited.
*/
package uga.menik.csx370.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uga.menik.csx370.models.FollowableUser;
import uga.menik.csx370.utility.Utility;

/**
 * This service contains people related functions.
 */
@Service
public class PeopleService {
    
    // added a datasource
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
    public List<FollowableUser> getFollowableUsers(String userIdToExclude) throws SQLException{
        // List of followable users
        List<FollowableUser> followableUsers = new ArrayList<>();
        
        // Write an SQL query to find the users that are not the current user.
        final String sql_query = "select * from user where userId <> ?";
        
        // Run the query with a datasource.
        // See UserService.java to see how to inject DataSource instance and
        // use it to run a query.
        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql_query)) {

            // Following line replaces the first place holder with userIdToExclude.
            pstmt.setString(1, userIdToExclude);
            
            // Use the query result to create a list of followable users.
            // See UserService.java to see how to access rows and their attributes
            // from the query result.
            try (ResultSet rs = pstmt.executeQuery()) {
                // Traverse the result rows one at a time.
                // Note: This specific while loop will only run at most once 
                // since username is unique.
                while (rs.next()) {
                    //boolean isFollowable = !rs.getString("userId").equals(userIdToExclude);
                    
                    String userId = rs.getString("userId");
                    String firstName = rs.getString("firstName");
                    String lastName = rs.getString("lastName");
                    //String profileImageName = rs.getString("profileImageName");
                    boolean isFollowed = false;
                    String lastActiveDate = "";

                    // Initialize FollowableUser.
                    followableUsers.add(new FollowableUser(userId, firstName, lastName, isFollowed, lastActiveDate));
                    
                }
            }
        }
        
        // Replace the following line and return the list you created.
        return followableUsers;
    }

}
