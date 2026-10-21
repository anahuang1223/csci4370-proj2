/**
Copyright (c) 2026 Sami Menik, PhD. All rights reserved.

This is a project developed by Dr. Menik to give the students an opportunity to apply database concepts learned in the class in a real world project. Permission is granted to host a running version of this software and to use images or videos of this work solely for the purpose of demonstrating the work to potential employers. Any form of reproduction, distribution, or transmission of the software's source code, in part or whole, without the prior written consent of the copyright owner, is strictly prohibited.
*/
package uga.menik.csx370.controllers;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import uga.menik.csx370.models.Post;
import uga.menik.csx370.services.BookmarkService;
import uga.menik.csx370.services.CommentService;
import uga.menik.csx370.services.HeartService;
import uga.menik.csx370.services.PostService;

/**
 * Handles /post URL and its sub urls.
 */
@Controller
@RequestMapping("/post")
public class PostController {

    private final PostService postService;
    private final HeartService heartService;
    private final BookmarkService bookmarkService;
    private final CommentService commentService;

    @Autowired
    public PostController(PostService postService, HeartService heartService,
            BookmarkService bookmarkService, CommentService commentService) {
        this.postService = postService;
        this.heartService = heartService;
        this.bookmarkService = bookmarkService;
        this.commentService = commentService;
    }

    /**
     * This function handles the /post/{postId} URL.
     * This handlers serves the web page for a specific post.
     * Note there is a path variable {postId}.
     * An example URL handled by this function looks like below:
     * http://localhost:8081/post/1
     * The above URL assigns 1 to postId.
     * 
     * See notes from HomeController.java regardig error URL parameter.
     */
    @GetMapping("/{postId}")
    public ModelAndView webpage(@PathVariable("postId") String postId,
            @RequestParam(name = "error", required = false) String error) {
        System.out.println("The user is attempting to view post with id: " + postId);
        // See notes on ModelAndView in BookmarksController.java.
        ModelAndView mv = new ModelAndView("posts_page");

        try {
            Post post = postService.getExpandedPost(postId);

            if (post == null) {
                mv.addObject("isNoContent", true);
            } else {
                mv.addObject("posts", List.of(post));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            // If an error occured, you can set the following property with the
            // error message to show the error message to the user.
            mv.addObject("errorMessage", "Failed to load the post.");
        }

        // An error message can be optionally specified with a url query parameter too.
        if (error != null) {
            mv.addObject("errorMessage", error);
        }

        return mv;
    }

    /**
     * Handles comments added on posts.
     * See comments on webpage function to see how path variables work here.
     * This function handles form posts.
     * See comments in HomeController.java regarding form submissions.
     */
    @PostMapping("/{postId}/comment")
    public String postComment(@PathVariable("postId") String postId,
            @RequestParam(name = "comment") String comment) {
        System.out.println("The user is attempting add a comment:");
        System.out.println("\tpostId: " + postId);
        System.out.println("\tcomment: " + comment);

        try {
            if (commentService.addComment(postId, comment)) {
                // Redirect the user if the comment adding is a success.
                return "redirect:/post/" + postId;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Redirect the user with an error message if there was an error.
        String message = URLEncoder.encode("Failed to post the comment. Please try again.",
                StandardCharsets.UTF_8);
        return "redirect:/post/" + postId + "?error=" + message;
    }

    /**
     * Handles likes added on posts.
     * See comments on webpage function to see how path variables work here.
     * See comments in PeopleController.java in followUnfollowUser function regarding 
     * get type form submissions and how path variables work.
     */
    @GetMapping("/{postId}/heart/{isAdd}")
    public String addOrRemoveHeart(@PathVariable("postId") String postId,
            @PathVariable("isAdd") Boolean isAdd,
            @RequestHeader(name = "Referer", required = false) String referer) {
        System.out.println("The user is attempting add or remove a heart:");
        System.out.println("\tpostId: " + postId);
        System.out.println("\tisAdd: " + isAdd);

        try {
            boolean success = isAdd ? heartService.addHeart(postId) : heartService.removeHeart(postId);

            if (success) {
                // Redirect the user if the comment adding is a success.
                return redirectBack(referer, postId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Redirect the user with an error message if there was an error.
        String message = URLEncoder.encode("Failed to (un)like the post. Please try again.",
                StandardCharsets.UTF_8);
        return "redirect:/post/" + postId + "?error=" + message;
    }

    /**
     * Handles bookmarking posts.
     * See comments on webpage function to see how path variables work here.
     * See comments in PeopleController.java in followUnfollowUser function regarding 
     * get type form submissions.
     */
    @GetMapping("/{postId}/bookmark/{isAdd}")
    public String addOrRemoveBookmark(@PathVariable("postId") String postId,
            @PathVariable("isAdd") Boolean isAdd,
            @RequestHeader(name = "Referer", required = false) String referer) {
        System.out.println("The user is attempting add or remove a bookmark:");
        System.out.println("\tpostId: " + postId);
        System.out.println("\tisAdd: " + isAdd);

        try {
            boolean success = isAdd ? bookmarkService.addBookmark(postId) : bookmarkService.removeBookmark(postId);

            if (success) {
                // Redirect the user if the comment adding is a success.
                return redirectBack(referer, postId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Redirect the user with an error message if there was an error.
        String message = URLEncoder.encode("Failed to (un)bookmark the post. Please try again.",
                StandardCharsets.UTF_8);
        return "redirect:/post/" + postId + "?error=" + message;
    }

    /**
     * Builds a redirect back to the page the user was on 
     */
    private String redirectBack(String referer, String postId) {
        if (referer != null) {
            try {
                URI uri = new URI(referer);
                String path = uri.getRawPath();
                String query = uri.getRawQuery();

                if (path != null && path.startsWith("/")) {
                    if (query != null) {
                        query = query.replaceAll("(^|&)error=[^&]*", "").replaceFirst("^&", "");
                    }
                    return "redirect:" + path + (query == null || query.isEmpty() ? "" : "?" + query);
                }
            } catch (URISyntaxException e) {
                // Fall through to below redirect
            }
        }

        return "redirect:/post/" + postId;
    }

}
