package uga.menik.csx370.models;


public class PollVote {

    /* The poll ID. */
    private String pollId;

    /* The user who responded to a specific poll. */
    private User user;

    /* The choice a user made on a specific poll.*/
    private int userChoice;

    /**
     * Constructs a PollVote for when a user votes on a poll.
     *
     * @param pollId        the unique identifier of the post
     * @param user          the user who responded to a poll
     * @param voteCount     the number of votes
     */
    public PollVote(String pollId, User user, int userChoice) {
        this.pollId = pollId;
        this.user = user;
        this.userChoice = userChoice;
        
    }

    
    
}