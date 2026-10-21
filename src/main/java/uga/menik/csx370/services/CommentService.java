package uga.menik.csx370.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uga.menik.csx370.models.Comment;
import uga.menik.csx370.models.User;

/**
 * This service contains database operations related to comments.
 *
 * Comment counts are not stored anywhere. They are counted from the
 * comment table every time a post is displayed.
 */
@Service
public class CommentService {

    private final DataSource dataSource;
    private final UserService userService;

    @Autowired
    public CommentService(DataSource dataSource, UserService userService) {
        this.dataSource = dataSource;
        this.userService = userService;
    }

    /**
     * Adds a comment from the logged-in user to a post.
     *
     * @param postId ID of the post being commented on
     * @param content text of the comment
     * @return true if the comment was saved
     * @throws SQLException if the database operation fails
     */
    public boolean addComment(String postId, String content) throws SQLException {

        User user = userService.getLoggedInUser();

        if (user == null || content == null) {
            return false;
        }

        String trimmedContent = content.trim();

        // Empty comments are not allowed (the table also has a check constraint).
        if (trimmedContent.isEmpty()) {
            return false;
        }

        // commentDate defaults to the current time in the table definition.
        final String sql = "insert into comment (postId, userId, content) values (?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, postId);
            pstmt.setString(2, user.getUserId());
            pstmt.setString(3, trimmedContent);

            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Retrieves all comments on a post, oldest first, along with the
     * user who wrote each one.
     *
     * @param postId ID of the post
     * @return list of comments on the post
     * @throws SQLException if the database query fails
     */
    public List<Comment> getComments(String postId) throws SQLException {

        List<Comment> comments = new ArrayList<>();

        final String sql = "select c.commentId, c.content, c.commentDate, u.userId, u.firstName, u.lastName " +
                "from comment c join user u on c.userId = u.userId " +
                "where c.postId = ? order by c.commentDate asc, c.commentId asc";

        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, postId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    User user = new User(rs.getString("userId"), rs.getString("firstName"), rs.getString("lastName"));

                    comments.add(new Comment(rs.getString("commentId"), rs.getString("content"),
                            formatDate(rs.getTimestamp("commentDate")), user));
                }
            }
        }

        return comments;
    }

    /**
     * Counts how many comments a post has.
     *
     * @param postId ID of the post
     * @return number of comments on the post
     * @throws SQLException if the database query fails
     */
    public int getCommentCount(String postId) throws SQLException {

        final String sql = "select count(*) as commentCount from comment where postId = ?";

        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, postId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("commentCount");
                }
            }
        }

        return 0;
    }

    /**
     * Formats a comment date the same way post dates are formatted.
     * Example: Oct 16, 2026, 06:38 AM
     */
    private String formatDate(Timestamp timestamp) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy, hh:mm a");
        return timestamp.toLocalDateTime().format(formatter);
    }
}
