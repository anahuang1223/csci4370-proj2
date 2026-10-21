package uga.menik.csx370.models;

import java.util.List;

/**
 * Represents a poll in its expanded form within the micro blogging platform.
 * An ExpandedPoll includes comments.
 */
public class ExpandedPoll extends Poll {
    
    // List of comments for a poll
    private List<Comment> comments;

    public ExpandedPoll(String pollId, String postId, String content, String choice1, String choice2, String postDate,
            int heartsCount, int commentsCount, boolean isHearted, boolean isBookmarked, User user, int voteCount, int choice1Votes, int choice2Votes, boolean hasVoted, List<Comment> comments) {

        super(pollId, postId, content, choice1, choice2, postDate, heartsCount, commentsCount, isHearted, isBookmarked, user, voteCount, choice1Votes, choice2Votes, hasVoted);
        this.comments = comments;
        this.isShowComents = true;
    }

    /**
     * Gets the list of comments for a specific poll.
     * @return list of comments for a poll
     */
    public List<Comment> getComments() {
        return List.copyOf(comments);
    }
}
