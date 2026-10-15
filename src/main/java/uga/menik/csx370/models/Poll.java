package uga.menik.csx370.models;

/**
 * The Poll extends the Post class since it's a type of post.
 * 
 */
public class Poll extends Post {

    /* The poll ID. */
    private String pollId;

    /* The 1st poll choice.*/
    private String choice1;

    /* The 2nd poll choice.*/
    private String choice2;

    /* The total number of votes a poll has received.*/
    private int voteCount;

    /**
     * Constructs a Poll with question, choices, date, and user.
     *
     * @param pollId        the unique identifier of the poll
     * @param postId        the associated id of the poll
     * @param content       the poll question
     * @param choice1       the 1st poll choice
     * @param choice2       the 2nd poll choice
     * @param user           the user who created the post
     * @param heartsCount    the number of hearts (likes) the post has received
     * @param commentsCount  the number of comments the post has received
     * @param isHearted      whether the post is hearted by the current user
     * @param isBookmarked   whether the post is bookmarked by the current user
     * @param voteCount     the number of votes
     */
    public Poll(String pollId, String postId, String content, String choice1, String choice2, String postDate, 
                int heartsCount, int commentsCount, boolean isHearted, boolean isBookmarked, User user, int voteCount) {
        
        super(postId, content, postDate, user, heartsCount, commentsCount, isHearted, isBookmarked);
        
        this.pollId = pollId;
        this.choice1 = choice1;
        this.choice2 = choice2;
        this.voteCount = voteCount;
    }

    /**
     * Returns the poll ID.
     *
     * @return the poll ID
     */
    public String getPollId() {
        return pollId;
    }

    /**
     * Returns choice 1 of the poll.
     *
     * @return the choice 1 of the poll
     */
    public String getChoice1() {
        return choice1;
    }

    /**
     * Returns choice 2 of the poll.
     *
     * @return the choice 2 of the poll
     */
    public String getChoice2() {
        return choice2;
    }

    /**
     * Returns the number of total number of votes a poll has received.
     *
     * @return the total number of votes
     */
    public int getVoteCount() {
        return voteCount;
    }

}
