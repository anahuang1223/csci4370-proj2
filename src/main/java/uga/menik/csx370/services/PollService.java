package uga.menik.csx370.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import uga.menik.csx370.models.User;

/**
 * This service contains poll related functions.
 */
@Service
public class PollService {
     // added a datasource
    private final DataSource dataSource;
    private final UserService userService;

    @Autowired
    public PollService(DataSource dataSource, UserService userService) {
        this.dataSource = dataSource;
        this.userService = userService;
    }

    /**
     * Creates a new poll with the given details.
     * Returns true if poll creation is successful.
     */
    public boolean createPoll(String question, String choice1, String choice2)
            throws SQLException {
        
        User user = userService.getLoggedInUser();
        
        if (user == null) {
            return false;
        }

        // SQL statements for inserting into Post and Poll
        final String postSql = "insert into post(userId, content, postType) values (?, ?, 'POLL')";
        final String pollSql = "insert into poll (postId, question, choice1, choice2) values (?, ?, ?, ?)";
        
        try (Connection conn = dataSource.getConnection()) {
           
            // Insert into Post
            try (PreparedStatement postStmt = conn.prepareStatement(postSql, Statement.RETURN_GENERATED_KEYS)) {
                // Following lines replace the placeholders 1-2 with values userId and question (which is the same as content).
                postStmt.setString(1, user.getUserId());
                postStmt.setString(2, question);
                
                // Execute the Post SQL statement
                int rowsAffected1 = postStmt.executeUpdate();
                if (rowsAffected1 == 0) {
                    throw new SQLException("Failed to create post.");
                }
                
                // Getting the resulting row after the query is executed
                ResultSet postRes = postStmt.getGeneratedKeys();
                
                if (postRes.next()) {
                    
                    // Getting postId from the result set
                    int postId = postRes.getInt(1);
                
                    System.out.println("Added new Post: " + postId);
                
                    // Creating Poll
                    try (PreparedStatement pollStmt = conn.prepareStatement(pollSql)) {
                        // Following lines replace the placeholders 1-4 with values postId, question, choice1, choice2.
                        pollStmt.setInt(1, postId);
                        pollStmt.setString(2, question);
                        pollStmt.setString(3, choice1);
                        pollStmt.setString(4, choice2);

                        // Execute the Poll SQL statement
                        int rowsAffected2 = pollStmt.executeUpdate();
                        if (rowsAffected2 == 0) {
                            throw new SQLException("Failed to create initial post.");
                        }
                        
                        System.out.println("Poll has been created successfully!");
                        
                        return rowsAffected2 > 0;
                        
                    }
                } else {
                    throw new SQLException();
                }
                
            }
        }

            
    }
        


    /**
     * Gets total number of votes for a poll.
     */
    public int getTotalVotes(String pollId) throws SQLException {
        int numVotes = 0;
        
        final String pollSql = "select count(*) from pollvotes where pollId = ?";
        
        try (Connection conn = dataSource.getConnection();
                PreparedStatement pollStmt = conn.prepareStatement(pollSql)) {
            // Following lines replace the placeholders 1-4 with values.
            pollStmt.setString(1, pollId);

            // Getting the "count" for number of votes for choice 1
            try (ResultSet rs = pollStmt.executeQuery()) {
                numVotes = rs.getInt("count(*)");
            }
        }  
        return numVotes;
    }

    /**
     * Gets number of votes for choice 1 of a poll.
     */
    public int getPollVotes1(String pollId) throws SQLException {
        int numVotes = 0;
        
        final String pollSql = "select count(*) from pollvotes where pollId = ? and choice = 1";
        
        try (Connection conn = dataSource.getConnection();
                PreparedStatement pollStmt = conn.prepareStatement(pollSql)) {
            // Following lines replace the placeholders 1-4 with values.
            pollStmt.setString(1, pollId);

            // Getting the "count" for number of votes for choice 1
            try (ResultSet rs = pollStmt.executeQuery("Failed to create post.")) {
                numVotes = rs.getInt("count(*)");
            }
        }  
        return numVotes;
    }

    /**
     * Gets number of votes for choice 2 of a poll.
     */
    public int getPollVotes2(String pollId) throws SQLException {
        int numVotes = 0;
        
        final String pollSql = "select count(*) from pollvotes where pollId = ? and choice = 2";
        
        try (Connection conn = dataSource.getConnection();
                PreparedStatement pollStmt = conn.prepareStatement(pollSql)) {
            // Following lines replace the placeholders 1-4 with values.
            pollStmt.setString(1, pollId);

            // Getting the "count" for number of votes for choice 2
            try (ResultSet rs = pollStmt.executeQuery()) {
                numVotes = rs.getInt("count(*)");
            }
        }  
        return numVotes;
    }


}
