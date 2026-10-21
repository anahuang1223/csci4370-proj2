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
     * Bookmarks a post for the logged-in user. You cannot bookmark a page that has already been bookmarked
     */
    public boolean addBookmark(String postId) throws SQLException {

        User user = userService.getLoggedInUser();

        if (user == null) {
            return false;
        }

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
     * Removes a post from the logged-in user's bookmarks
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
     * Checks whether the logged-in user has bookmarked a post
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
                // Only if user has bookmarked
                return rs.next();
            }
        }
    }
}
