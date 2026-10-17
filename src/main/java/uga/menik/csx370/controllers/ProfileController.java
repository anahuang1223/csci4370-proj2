/**
Copyright (c) 2026 Sami Menik, PhD. All rights reserved.

This is a project developed by Dr. Menik to give the students an opportunity to apply database concepts learned in the class in a real world project. Permission is granted to host a running version of this software and to use images or videos of this work solely for the purpose of demonstrating the work to potential employers. Any form of reproduction, distribution, or transmission of the software's source code, in part or whole, without the prior written consent of the copyright owner, is strictly prohibited.
*/
package uga.menik.csx370.controllers;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import uga.menik.csx370.models.Post;
import uga.menik.csx370.services.PostService;
import uga.menik.csx370.services.UserService;

/**
 * Handles /profile URL and its sub URLs.
 */
@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final UserService userService;
    private final PostService postService;

    @Autowired
    public ProfileController(
            UserService userService,
            PostService postService) {

        this.userService = userService;
        this.postService = postService;
    }

    /**
     * Shows posts made by the logged-in user.
     */
    @GetMapping
    public ModelAndView profileOfLoggedInUser() {

        System.out.println(
                "User is attempting to view profile of the logged in user.");

        return profileOfSpecificUser(
                userService.getLoggedInUser().getUserId());
    }

    /**
     * Shows posts made by a specific user.
     */
    @GetMapping("/{userId}")
    public ModelAndView profileOfSpecificUser(
            @PathVariable("userId") String userId) {

        System.out.println(
                "User is attempting to view profile: " + userId);

        ModelAndView mv =
                new ModelAndView("posts_page");

        try {

            List<Post> posts =
                    postService.getPostsByUser(userId);

            mv.addObject("posts", posts);

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

        return mv;
    }

}