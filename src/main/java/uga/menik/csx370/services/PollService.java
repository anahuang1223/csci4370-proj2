package uga.menik.csx370.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

        String trimmedContent = question.trim();

        // Find all unique hashtags contained in the poll question.
        Set<String> hashtags = extractHashtags(trimmedContent);

        // SQL statements for inserting into Post and Poll
        final String postSql = "insert into post(userId, content, postType) values (?, ?, 'POLL')";
        final String pollSql = "insert into poll(postId, question, choice1, choice2) values (?, ?, ?, ?)";
        
        try (Connection conn = dataSource.getConnection()) {
           
            // Insert into Post
            try (PreparedStatement postStmt = conn.prepareStatement(postSql, Statement.RETURN_GENERATED_KEYS)) {
                // Following lines replace the placeholders 1-2 with values userId and question (which is the same as content).
                postStmt.setString(1, user.getUserId());
                postStmt.setString(2, question);
                
                // Executes the Post SQL statement
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

                        // Executes the Poll SQL statement
                        int rowsAffected2 = pollStmt.executeUpdate();
                        if (rowsAffected2 == 0) {
                            throw new SQLException("Failed to create initial post.");
                        }
                        
                        System.out.println("Poll has been created successfully!");

                        // Inserts hashtags into hashtag table
                        for (String hashtag : hashtags) {
                            int hashtagId = getOrCreateHashtag(conn, hashtag);
                            linkPostToHashtag(conn, postId, hashtagId);
                }
                        
                        return rowsAffected2 > 0;
                        
                    }
                } else {
                    throw new SQLException();
                }
                
            }
        }

            
    }

    /**
     * Inserts a new vote to a certain poll in pollvote.
     * @param pollId
     * @param choice
     * @return
     * @throws SQLException
     */
    public boolean createVote(String pollId, String choice) throws SQLException {
        User user = userService.getLoggedInUser();
        
        if (user == null) {
            return false;
        } 

        final String voteSQL = "insert into pollvote (pollId, userId, choice) values (?, ?, ?)";
        try (Connection conn = dataSource.getConnection()) {
           
            // Insert into pollvote
            try (PreparedStatement voteStmt = conn.prepareStatement(voteSQL, Statement.RETURN_GENERATED_KEYS)) {
                // Following lines replace the placeholders 1-2 with values userId and question (which is the same as content).
                voteStmt.setString(1, pollId);
                voteStmt.setString(2, user.getUserId());
                voteStmt.setString(3, choice);
                
                // Executes the insert statement
                int rowsAffected = voteStmt.executeUpdate();

                // Getting the resulting row after the query is executed

                return rowsAffected > 0;
            }
        }

    }


    /**
     * Updates a user's choice in a certain poll in pollvote. If they click the one they chose, it removes it from database.
     * @param pollId
     * @param choice
     * @return
     * @throws SQLException
     */
    public boolean updateVote(String pollId, String choice) throws SQLException {
        User user = userService.getLoggedInUser();
        
        if (user == null) {
            return false;
        } 

        // Used to check if the vote is the same or not
        final String checkSQL = "select * from pollvote where pollId=? and userId=?";

        // Used to delete a vote if user clicks on the choice they made originally
        final String deleteSQL = "delete from pollvote where pollId=? and userId=?";

        // Used to update a vote if user clicks a different choice
        final String voteSQL = "update pollvote set choice=? where pollId = ? and userId = ?";
        
        try (Connection conn = dataSource.getConnection()) {
            
            // Checking if choices are the same
            try (PreparedStatement checkVoteStmt = conn.prepareStatement(checkSQL, Statement.RETURN_GENERATED_KEYS)) {
                
                checkVoteStmt.setString(1, pollId);
                checkVoteStmt.setString(2, user.getUserId());

                ResultSet checkRes = checkVoteStmt.executeQuery();

                // If the choice from database is the same as the new choice, delete the row from database
                if (checkRes.next() && checkRes.getString(3).equals(choice)) {
                    System.out.println("Deleting this row");

                    // Update row in pollvote
                    try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSQL, Statement.RETURN_GENERATED_KEYS)) {
                        // Following lines replace the placeholders 1-2 with values pollId and user 
                        deleteStmt.setString(1, pollId);
                        deleteStmt.setString(2, user.getUserId());
                        
                        // Executes the delete statement
                        int rowsAffected = deleteStmt.executeUpdate();

                        return rowsAffected > 0;
                    }
                    
                } else {
                    
                    // Updates row in pollvote if the choice is different
                    try (PreparedStatement voteStmt = conn.prepareStatement(voteSQL, Statement.RETURN_GENERATED_KEYS)) {
                        // Following lines replace the placeholders 1-3 with values choice, pollId, user
                        voteStmt.setString(1, choice);
                        voteStmt.setString(2, pollId);
                        voteStmt.setString(3, user.getUserId());
                        
                        // Executes the update statement
                        int rowsAffected = voteStmt.executeUpdate();

                        return rowsAffected > 0;
                    }
                }

                
            
            }

            
        }

    }

    /**
     * Returns whether the current user has voted or not on a specific poll.
     * @param pollId
     * @return true if current user has voted on the poll
     * @throws SQLException
     */
    public boolean getHasVoted(String pollId) throws SQLException {
        User user = userService.getLoggedInUser();
        
        if (user == null) {
            return false;
        } 

        final String voteSQL = "select * from pollvote where pollId = ? and userId = ?";

        try (Connection conn = dataSource.getConnection()) {
           
            // Update row in pollvote
            try (PreparedStatement voteStmt = conn.prepareStatement(voteSQL, Statement.RETURN_GENERATED_KEYS)) {
                // Following lines replace the placeholders 1-2 with values userId and question (which is the same as content).
                voteStmt.setString(1, pollId);
                voteStmt.setString(2, user.getUserId());
                
                // Executes the update statement
                ResultSet voteRes = voteStmt.executeQuery();

                // If no rows selected, return false
                if (!voteRes.next()) {
                    return false;
                } 
                // Else return true
                return true;
            }
        }

    }
        
    /**
     * Gets total number of votes for a poll.
     */
    public int getTotalVotes(String pollId) throws SQLException {
        return getVotes(pollId, null);
    }

    /**
     * Gets number of users who voted choice 1 for a poll.
     */
    public int getChoice1Votes(String pollId) throws SQLException {
        return getVotes(pollId, 1);
    }

    /**
     * Gets number of users who voted choice 2 for a poll.
     */
    public int getChoice2Votes(String pollId) throws SQLException {
        return getVotes(pollId, 2);
    }

    /**
     * Returns number of votes for a poll (total, or choice-dependent).
     */
    public int getVotes(String pollId, Integer choice) throws SQLException {
        int numVotes = 0;
        String pollSql;

        // If choice is given, then it selects only votes that correspond with that choice
        if (choice == null) {
            pollSql = "select count(*) from pollvote where pollId = ?";
        } else {
            pollSql = "select count(*) from pollvote where pollId = ? and choice = ?";
        }
        
        try (Connection conn = dataSource.getConnection();
                PreparedStatement pollStmt = conn.prepareStatement(pollSql)) {
            
            // Following lines replace the placeholders with values.
            pollStmt.setString(1, pollId);

            if (choice != null) {
                pollStmt.setInt(2, choice);
            }

            // Executes query
            try (ResultSet rs = pollStmt.executeQuery()) {
                if (!rs.next()) {
                    return 0;
                } 
                numVotes = rs.getInt("count(*)");
            }
        }  
        return numVotes;
    }

    /***********************************************
     * Hashtag Stuff (same as in PostService.java) *
     ***********************************************/

    /**
     * Extracts the hashtags from the poll question.
     * @param content
     * @return
     */
    private Set<String> extractHashtags(String content) {

        Set<String> hashtags = new HashSet<>();
        Pattern pattern =Pattern.compile("#([A-Za-z0-9_]+)");
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            hashtags.add(matcher.group(1).toLowerCase());
        }
        return hashtags;
    }

    /**
     * Inserts hashtag into hashtag and returns the hashtag id.
     * @param conn
     * @param hashtag
     * @return
     * @throws SQLException
     */
    private int getOrCreateHashtag(Connection conn, String hashtag) throws SQLException {

        /*
         * If the hashtag already exists, LAST_INSERT_ID is set
         * to its existing hashtagId.
         */
        final String sql = "insert into hashtag(hashtag) values (?) on duplicate key update hashtagId = last_insert_id(hashtagId)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, hashtag);

            pstmt.executeUpdate();

            // Retrieve the new or existing hashtagId.
            try (ResultSet rs = pstmt.getGeneratedKeys()) {

                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        throw new SQLException("Failed to get hashtagId.");
    }

    /**
     * Insert into postHashtag.
     *
     * @param conn active database connection
     * @param postId ID of the post
     * @param hashtagId ID of the hashtag
     * @throws SQLException if the database operation fails
     */
    private void linkPostToHashtag(Connection conn, int postId, int hashtagId) throws SQLException {

        final String sql = "insert into postHashtag(postId, hashtagId) values (?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, postId);
            pstmt.setInt(2, hashtagId);
            pstmt.executeUpdate();
        }
    }

}
