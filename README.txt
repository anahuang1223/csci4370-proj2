CSCI 4370 (Database Management): Project 2

Group ID: 6

### Members: 
- Travis Burton
- Ana Huang
- Leen Quazi
- Sach Shrestha
- Leanne Wong


### Setup (same instructions as those given in the Project 2 page):
1. Create the database using the given database_setup.sql file.
2. After the initial database setup, you can run the starter code using the commands:

    - On unix like machines:
        mvn spring-boot:run -Dspring-boot.run.jvmArguments='-Dserver.port=8081'

    - On windows command line:
        mvn spring-boot:run -D"spring-boot.run.arguments=--server.port=8081"

    - On windows power shell:
        mvn spring-boot:run --% -Dspring-boot.run.arguments="--server.port=8081"

3. Open browser and navigate to the following URL: http://localhost:8081/


### Contributions:
- Travis Burton: Implemented the likes, comments, bookmarks features for posts. Updated the database_setup.sql
- Ana Huang: Implemented people page and following list. Filmed Demo Video
- Leen Quazi: Implemented Posts on Profile and Home feeds and extracting hashtags from posts.
- Sach Shrestha: Implemented Hashtag search function. Drew ER Diagram.
- Leanne Wong: Implemented the new custom feature: Polls. Wrote dml.sql and README


### New Feature: Polls

# Description:
- On the homepage, users can press a button to either create a Post (normal post) or a Poll. 
- Polls are composed of one question, and 2 possible answer choices.
- If the current user has not voted on a specific poll, it will show the poll without the voting results (will show how total votes, but not which choice is winning)
- If the current user has voted on a specific poll, then the poll will display the total votes for each choice.
    - The current user can remove their vote from the poll by selecting the same choice again
    - The current user can change their vote for the poll by selecting a different choice 
- Polls will appear in the same places Posts appear. Polls can have # hashtags in the "question", and can be searched via those hashtags
- Polls will also have the same functionalities as Posts (likes, bookmarks, and comments)


# Where to find:
- UI (under resources/templates/fragments): I created/modified the following files:
    - home_page.mustache: edited home_page for the "Create Poll" and "Create Post" buttons
    - new_poll_form.mustache, new_post_form.mustache: Form Popup windows
    - post.mustache: Allow polls to show up as posts by adding the #isPoll condition; Allow users to see if they've voted on a poll before with the hasVoted condition
    - style.css: all the things under "Poll stuff" comment, but basically added poll-choices buttons and Modals for the "create new poll/post form"
- Models: Poll.java, PollVote.java
- Controller: PollController.java (poll creation and voting mappings)
- Service: PollService.java (poll creation and poll voting)
- Other Services (minor changes): PostService.java, HashtagService.java (for rendering the polls on the home, profile, hashtagsearch feeds)
- SQL/Schema: Under table names "poll" and "pollvote"



### External Resources:
https://mustache.github.io/mustache.5.html#Inverted-Sections
https://stackoverflow.com/questions/8548943/how-to-handle-an-if-statement-in-a-mustache-template/27680634#27680634
https://csci4300-web-programming.github.io/UGA-WebDAWGS-Docs-Pages/docs/category/css