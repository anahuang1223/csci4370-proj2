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

    /* The number of votes for choice 1 of a poll */
    private int choice1Votes;

    /* The number of votes for choice 2 of a poll */
    private int choice2Votes;

    /* Whether current user has voted*/
    private boolean hasVoted;

    

    /**
     * Constructs a Poll with question, choices, and user.
     *
     * @param pollId            the unique identifier of the poll
     * @param postId            the associated id of the poll
     * @param content           the poll question
     * @param choice1           the 1st poll choice
     * @param choice2           the 2nd poll choice
     * @param user              the user who created the post
     * @param heartsCount       the number of hearts (likes) the post has received
     * @param commentsCount     the number of comments the post has received
     * @param isHearted         whether the post is hearted by the current user
     * @param isBookmarked      whether the post is bookmarked by the current user
     * @param voteCount         the number of votes
     * @param choice1Votes      the number of votes for choice 1
     * @param choice2Votes      the number of votes for choice 2
     */
    public Poll(String pollId, String postId, String content, String choice1, String choice2, String postDate, 
                int heartsCount, int commentsCount, boolean isHearted, boolean isBookmarked, User user, int voteCount, int choice1Votes, int choice2Votes, 
                boolean hasVoted) {
        
        super(postId, content, postDate, user, heartsCount, commentsCount, isHearted, isBookmarked);
        
        this.pollId = pollId;
        this.choice1 = choice1;
        this.choice2 = choice2;
        this.choice1Votes = choice1Votes;
        this.choice2Votes = choice2Votes;
        this.voteCount = voteCount;
        this.hasVoted = hasVoted;
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
     * Returns the total number of votes a poll has received.
     *
     * @return the total number of votes
     */
    public int getVoteCount() {
        return voteCount;
    }

    /**
     * Returns the number of votes for choice 1 of a poll.
     *
     * @return the number of votes for choice 1
     */
    public int getChoice1Votes() {
        return choice1Votes;
    }

    /**
     * Returns the number of votes for choice 2 of a poll.
     *
     * @return the number of votes for choice 2
     */
    public int getChoice2Votes() {
        return choice2Votes;
    }

    /**
     * Returns that this is not a poll. This is here because post.mustache
     * needs to differentiate between polls and posts.
     * 
     * @return that this is a poll 
     */
    @Override
    public boolean getIsPoll() {
        return true;
    }

    /**
     * Returns whether current user has voted on this poll.
     * 
     * @return whether current user has voted on this poll.
     */
    public boolean getHasVoted() {
        return hasVoted;
    }

    /**
     * Sets hasVoted.
     * 
     * @param hasVoted
     */
    public void setHasVoted(boolean hasVoted) {
        this.hasVoted = hasVoted;
    }

}
