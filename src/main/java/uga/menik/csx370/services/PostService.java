package uga.menik.csx370.services;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import uga.menik.csx370.models.Post;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uga.menik.csx370.models.User;

/**
 * This service contains post related functions.
 */
@Service
public class PostService {

    private final DataSource dataSource;
    private final UserService userService;

    @Autowired
    public PostService(DataSource dataSource, UserService userService) {
        this.dataSource = dataSource;
        this.userService = userService;
    }

    /**
     * Creates a normal post for the currently logged-in user.
     *
     * @param content the text content of the post
     * @return true if the post was successfully created
     */
    public boolean createPost(String content) throws SQLException {

    User user = userService.getLoggedInUser();

    if (user == null) {
        return false;
    }

    String trimmedContent = content.trim();

    if (trimmedContent.isEmpty()) {
        return false;
    }

    Set<String> hashtags = extractHashtags(trimmedContent);

    final String postSql =
            "insert into post(userId, content, postType) " +
            "values (?, ?, 'NORMAL')";

    try (Connection conn = dataSource.getConnection()) {

        conn.setAutoCommit(false);

        try {
            int postId;

            try (PreparedStatement postStmt =
                    conn.prepareStatement(
                            postSql,
                            Statement.RETURN_GENERATED_KEYS)) {

                postStmt.setString(1, user.getUserId());
                postStmt.setString(2, trimmedContent);

                int rowsAffected = postStmt.executeUpdate();

                if (rowsAffected == 0) {
                    conn.rollback();
                    return false;
                }

                try (ResultSet rs = postStmt.getGeneratedKeys()) {

                    if (!rs.next()) {
                        conn.rollback();
                        return false;
                    }

                    postId = rs.getInt(1);
                }
            }

            for (String hashtag : hashtags) {

                int hashtagId =
                        getOrCreateHashtag(conn, hashtag);

                linkPostToHashtag(
                        conn,
                        postId,
                        hashtagId
                );
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            conn.rollback();
            throw e;

        } finally {
            conn.setAutoCommit(true);
        }
    }
}

private int getOrCreateHashtag(
        Connection conn,
        String hashtag) throws SQLException {

    final String sql =
            "insert into hashtag(hashtag) " +
            "values (?) " +
            "on duplicate key update " +
            "hashtagId = last_insert_id(hashtagId)";

    try (PreparedStatement pstmt =
            conn.prepareStatement(
                    sql,
                    Statement.RETURN_GENERATED_KEYS)) {

        pstmt.setString(1, hashtag);

        pstmt.executeUpdate();

        try (ResultSet rs = pstmt.getGeneratedKeys()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        }
    }

    throw new SQLException(
            "Failed to get hashtagId.");
}

private void linkPostToHashtag(
        Connection conn,
        int postId,
        int hashtagId) throws SQLException {

    final String sql =
            "insert into postHashtag(postId, hashtagId) " +
            "values (?, ?)";

    try (PreparedStatement pstmt =
            conn.prepareStatement(sql)) {

        pstmt.setInt(1, postId);
        pstmt.setInt(2, hashtagId);

        pstmt.executeUpdate();
    }
}

private Set<String> extractHashtags(String content) {

    Set<String> hashtags = new HashSet<>();

    Pattern pattern = Pattern.compile("#([A-Za-z0-9_]+)");
    Matcher matcher = pattern.matcher(content);

    while (matcher.find()) {
        hashtags.add(matcher.group(1).toLowerCase());
    }

    return hashtags;
}

private String formatPostDate(Timestamp timestamp) {

    DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern(
                    "MMM dd, yyyy, hh:mm a");

    return timestamp
            .toLocalDateTime()
            .format(formatter);
}

public List<Post> getPostsByUser(String userId)
        throws SQLException {

    List<Post> posts = new ArrayList<>();

    final String sql =
            "select " +
            "p.postId, " +
            "p.content, " +
            "p.postDate, " +
            "u.userId, " +
            "u.firstName, " +
            "u.lastName " +
            "from post p " +
            "join user u on p.userId = u.userId " +
            "where p.userId = ? " +
            "and p.postType = 'NORMAL' " +
            "order by p.postDate desc";

    try (Connection conn = dataSource.getConnection();
         PreparedStatement pstmt =
                 conn.prepareStatement(sql)) {

        pstmt.setString(1, userId);

        try (ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {

                User user = new User(
                        rs.getString("userId"),
                        rs.getString("firstName"),
                        rs.getString("lastName")
                );

                Post post = new Post(
                        rs.getString("postId"),
                        rs.getString("content"),
                        formatPostDate(
                                rs.getTimestamp("postDate")),
                        user,
                        0,
                        0,
                        false,
                        false
                );

                posts.add(post);
            }
        }
    }

    return posts;
}



}