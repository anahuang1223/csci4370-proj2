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
import uga.menik.csx370.services.UserService;

/**
 * This controller handles the home page and some of it's sub URLs.
 */
@Controller
@RequestMapping
public class HomeController {

    private final PostService postService;
    private final UserService userService;

    @Autowired
    public HomeController(PostService postService,
        UserService userService) {

        this.postService = postService;
        this.userService = userService;
    }

   /**
     * Displays the home page.
     *
     * The home feed contains the logged-in user's own posts and
     * posts from users that they follow, ordered newest to oldest.
     *
     * @param error optional error message passed through the URL
     * @return home page and its post data
     */
    @GetMapping
    public ModelAndView webpage(@RequestParam(name = "error", required = false) String error) {

        ModelAndView mv = new ModelAndView("home_page");

       
        try {
            
            // Get the ID of the user currently logged into the application.
            String loggedInUserId =
                    userService.getLoggedInUser().getUserId();

            /*
             * Retrieve the user's home feed from the database.
             * This includes the user's own posts and posts from
             * users they follow.
             */
            List<Post> posts =
                    postService.getHomeFeed(loggedInUserId);

            mv.addObject("posts", posts);

            // Show the no-content message if the feed is empty.
            if (posts.isEmpty()) {
                mv.addObject("isNoContent", true);
            }

        } catch (SQLException e) {

            e.printStackTrace();

            mv.addObject(
                    "errorMessage",
                    "Failed to load posts."
            );
        }

        /*
         * If an error message was included in the URL,
         * make it available to the home page template.
         */
        if (error != null) {
            mv.addObject(
                    "errorMessage",
                    error);
        }

        return mv;
    }

     /**
     * Creates a normal post submitted from the home page.
     *
     * @param postText text entered by the user
     * @return redirect back to the home page
     */
    @PostMapping("/createpost")
    public String createPost(@RequestParam(name = "posttext") String postText) {

        try {

            /*
             * PostService validates and stores the post along
             * with any hashtags contained in the post text.
             */
            boolean success = postService.createPost(postText);

            if (success) {
                return "redirect:/";
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        /*
         * Redirect back to the home page with an error message
         * if the post could not be created.
         */
        String message = URLEncoder.encode(
                "Failed to create the post. Please try again.",
                StandardCharsets.UTF_8
        );

        return "redirect:/?error=" + message;
    }

}