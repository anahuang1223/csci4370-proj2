package uga.menik.csx370.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uga.menik.csx370.models.User;

/**
 * This service contains database operations related to hearts (likes).
 *
 * Heart counts are not stored anywhere. They are counted from the
 * heart table every time a post is displayed.
 */
@Service
public class HeartService {

    private final DataSource dataSource;
    private final UserService userService;

    @Autowired
    public HeartService(DataSource dataSource, UserService userService) {
        this.dataSource = dataSource;
        this.userService = userService;
    }

    /**
     * Adds a heart from the logged-in user to a post.
     * Hearting a post that is already hearted does nothing.
     *
     * @param postId ID of the post to heart
     * @return true if the logged-in user now hearts the post
     * @throws SQLException if the database operation fails
     */
    public boolean addHeart(String postId) throws SQLException {

        User user = userService.getLoggedInUser();

        if (user == null) {
            return false;
        }

        /*
         * (postId, userId) is the primary key of heart, so a user can
         * only heart a post once. "insert ignore" skips the insert
         * instead of throwing an error if the heart already exists
         * (for example, if the user double-clicks the heart button).
         */
        final String sql = "insert ignore into heart (postId, userId) values (?, ?)";

        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, postId);
            pstmt.setString(2, user.getUserId());
            pstmt.executeUpdate();

            return true;
        }
    }

    /**
     * Removes the logged-in user's heart from a post.
     *
     * @param postId ID of the post to un-heart
     * @return true if the logged-in user no longer hearts the post
     * @throws SQLException if the database operation fails
     */
    public boolean removeHeart(String postId) throws SQLException {

        User user = userService.getLoggedInUser();

        if (user == null) {
            return false;
        }

        final String sql = "delete from heart where postId = ? and userId = ?";

        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, postId);
            pstmt.setString(2, user.getUserId());
            pstmt.executeUpdate();

            return true;
        }
    }

    /**
     * Counts how many hearts a post has.
     *
     * @param postId ID of the post
     * @return number of hearts on the post
     * @throws SQLException if the database query fails
     */
    public int getHeartCount(String postId) throws SQLException {

        final String sql = "select count(*) as heartCount from heart where postId = ?";

        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, postId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("heartCount");
                }
            }
        }

        return 0;
    }

    /**
     * Checks whether the logged-in user has hearted a post.
     *
     * @param postId ID of the post
     * @return true if the logged-in user has hearted the post
     * @throws SQLException if the database query fails
     */
    public boolean isHearted(String postId) throws SQLException {

        User user = userService.getLoggedInUser();

        if (user == null) {
            return false;
        }

        final String sql = "select 1 from heart where postId = ? and userId = ?";

        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, postId);
            pstmt.setString(2, user.getUserId());

            try (ResultSet rs = pstmt.executeQuery()) {
                // A row exists only if this user hearted this post.
                return rs.next();
            }
        }
    }
}
