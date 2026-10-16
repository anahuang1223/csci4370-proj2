/**
Copyright (c) 2026 Sami Menik, PhD. All rights reserved.

This is a project developed by Dr. Menik to give the students an opportunity to apply database concepts learned in the class in a real world project. Permission is granted to host a running version of this software and to use images or videos of this work solely for the purpose of demonstrating the work to potential employers. Any form of reproduction, distribution, or transmission of the software's source code, in part or whole, without the prior written consent of the copyright owner, is strictly prohibited.
*/
package uga.menik.csx370.controllers;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import uga.menik.csx370.models.Post;
import uga.menik.csx370.services.PostService;
import uga.menik.csx370.utility.Utility;

/**
 * This controller handles the home page and some of it's sub URLs.
 */
@Controller
@RequestMapping
public class HomeController {

    private final PostService postService;

    @Autowired
    public HomeController(PostService postService) {
        this.postService = postService;
    }

    /**
     * This is the specific function that handles the root URL itself.
     *
     * Note that this accepts a URL parameter called error.
     * The value to this parameter can be shown to the user as an error message.
     * See notes in HashtagSearchController.java regarding URL parameters.
     */
    @GetMapping
    public ModelAndView webpage(@RequestParam(name = "error", required = false) String error) {

        ModelAndView mv = new ModelAndView("home_page");

        // Still using sample posts for now.
        // We will replace this with database-backed home feed logic later.
        List<Post> posts = Utility.createSamplePostsListWithoutComments();
        mv.addObject("posts", posts);

        String errorMessage = error;
        mv.addObject("errorMessage", errorMessage);

        return mv;
    }

    /**
     * Handles creation of a normal post.
     */
    @PostMapping("/createpost")
    public String createPost(@RequestParam(name = "posttext") String postText) {

        try {

            boolean success = postService.createPost(postText);

            if (success) {
                return "redirect:/";
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        String message = URLEncoder.encode(
                "Failed to create the post. Please try again.",
                StandardCharsets.UTF_8
        );

        return "redirect:/?error=" + message;
    }

}