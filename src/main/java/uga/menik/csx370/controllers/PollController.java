package uga.menik.csx370.controllers;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import java.sql.SQLException;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import uga.menik.csx370.services.PollService;


/**
 * Handles /polls URL.
 */
@Controller
public class PollController {
    private final PollService pollService;

    public PollController(PollService pollService) {
        this.pollService = pollService;
    }

    @PostMapping("/createpoll")
    public String createPoll(
        @RequestParam("question") String question,
        @RequestParam("question") String choice1,
        @RequestParam("question") String choice2) {
        
            System.out.println("User is creating poll with question: " + question);
            System.out.println("User is creating poll with choice 1: " + choice1);
            System.out.println("User is creating poll with choice 2: " + choice2);

            try {
                pollService.createPoll(question, choice1, choice2);
            } catch (SQLException error) {
                String message = URLEncoder.encode("Failed to create poll. Please try again.",
                StandardCharsets.UTF_8);
                return "redirect:/?error=" + message;
            }
            
            return "redirect:/";
        }
}
