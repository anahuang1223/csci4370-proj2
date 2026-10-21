package uga.menik.csx370.controllers;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import java.sql.SQLException;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import uga.menik.csx370.services.PollService;


/**
 * Poll controller handles all poll and vote mappings.
 */
@Controller
public class PollController {
    private final PollService pollService;

    public PollController(PollService pollService) {
        this.pollService = pollService;
    }

    // Handles /createpoll functionality
    @PostMapping("/createpoll")
    public String createPoll(
        @RequestParam("question") String question,
        @RequestParam("choice1") String choice1,
        @RequestParam("choice2") String choice2) {
        
            System.out.println("User is creating poll with question: " + question);
            System.out.println("User is creating poll with choice 1: " + choice1);
            System.out.println("User is creating poll with choice 2: " + choice2);

            try {
                boolean poll = pollService.createPoll(question, choice1, choice2);
                if (!poll) {
                    throw new SQLException();
                }
                
            } catch (SQLException error) {
                String message = URLEncoder.encode("Failed to create poll. Please try again.",
                StandardCharsets.UTF_8);
                return "redirect:/?error=" + message;
            }
            
            return "redirect:/";
    }

    // Handles /vote functionality
    @PostMapping("/vote")
    public String createVote(
        @RequestParam("pollId") String pollId,
        @RequestParam("choice") String choice) {
        
            System.out.println("User is voting on a poll with pollId: " + pollId);

            try {
                boolean poll = pollService.createVote(pollId, choice);
                if (!poll) {
                    throw new SQLException();
                }
                
            } catch (SQLException error) {
                String message = URLEncoder.encode("Failed to vote on poll. Please try again.",
                StandardCharsets.UTF_8);
                return "redirect:/?error=" + message;
            }
            
            return "redirect:/";
    }

    // Handles /vote functionality
    @PostMapping("/changevote")
    public String updateVote(
        @RequestParam("pollId") String pollId,
        @RequestParam("choice") String choice) {
        
            System.out.println("User changing their vote on a poll with pollId: " + pollId);

            try {
                boolean poll = pollService.updateVote(pollId, choice);
                if (!poll) {
                    throw new SQLException();
                }
                
            } catch (SQLException error) {
                String message = URLEncoder.encode("Failed to change vote on poll. Please try again.",
                StandardCharsets.UTF_8);
                return "redirect:/?error=" + message;
            }
            
            return "redirect:/";
    }

}
