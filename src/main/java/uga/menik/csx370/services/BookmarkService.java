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
 * This service contains database operations related to bookmarks.
 *
 * The list of bookmarked posts shown on /bookmarks is built in
 * PostService.getBookmarkedPosts so it can reuse the same post/poll
 * building code as the home and profile feeds.
 */
@Service
public class BookmarkService {

    private final DataSource dataSource;
    private final UserService userService;

    @Autowired
    public BookmarkService(DataSource dataSource, UserService userService) {
        this.dataSource = dataSource;
        this.userService = userService;
    }

    /**
     * Bookmarks a post for the logged-in user.
     * Bookmarking a post that is already bookmarked does nothing.
     *
     * @param postId ID of the post to bookmark
     * @return true if the logged-in user now has the post bookmarked
     * @throws SQLException if the database operation fails
     */
    public boolean addBookmark(String postId) throws SQLException {

        User user = userService.getLoggedInUser();

        if (user == null) {
            return false;
        }

        // (postId, userId) is the primary key, so duplicates are skipped.
        final String sql = "insert ignore into bookmark (postId, userId) values (?, ?)";

        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, postId);
            pstmt.setString(2, user.getUserId());
            pstmt.executeUpdate();

            return true;
        }
    }

    /**
     * Removes a post from the logged-in user's bookmarks.
     *
     * @param postId ID of the post to un-bookmark
     * @return true if the logged-in user no longer has the post bookmarked
     * @throws SQLException if the database operation fails
     */
    public boolean removeBookmark(String postId) throws SQLException {

        User user = userService.getLoggedInUser();

        if (user == null) {
            return false;
        }

        final String sql = "delete from bookmark where postId = ? and userId = ?";

        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, postId);
            pstmt.setString(2, user.getUserId());
            pstmt.executeUpdate();

            return true;
        }
    }

    /**
     * Checks whether the logged-in user has bookmarked a post.
     *
     * @param postId ID of the post
     * @return true if the logged-in user has bookmarked the post
     * @throws SQLException if the database query fails
     */
    public boolean isBookmarked(String postId) throws SQLException {

        User user = userService.getLoggedInUser();

        if (user == null) {
            return false;
        }

        final String sql = "select 1 from bookmark where postId = ? and userId = ?";

        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, postId);
            pstmt.setString(2, user.getUserId());

            try (ResultSet rs = pstmt.executeQuery()) {
                // A row exists only if this user bookmarked this post.
                return rs.next();
            }
        }
    }
}
