package com.mycompany.pollingApp.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mycompany.pollingApp.exception.ResourceNotFoundException;
import com.mycompany.pollingApp.model.User;
import com.mycompany.pollingApp.payload.PagedResponse;
import com.mycompany.pollingApp.payload.PollResponse;
import com.mycompany.pollingApp.payload.UserIdentityAvailability;
import com.mycompany.pollingApp.payload.UserProfile;
import com.mycompany.pollingApp.payload.UserSummary;
import com.mycompany.pollingApp.repository.PollingAppRepository;
import com.mycompany.pollingApp.repository.UserRepository;
import com.mycompany.pollingApp.repository.VoteRepository;
import com.mycompany.pollingApp.security.CurrentUser;
import com.mycompany.pollingApp.security.UserPrincipal;
import com.mycompany.pollingApp.service.PollingAppService;
import com.mycompany.pollingApp.util.Constants;

@RestController
@RequestMapping("/api")
public class UserController {

	 @Autowired
	    private UserRepository userRepository;

	    @Autowired
	    private PollingAppRepository pollRepository;

	    @Autowired
	    private VoteRepository voteRepository;

	    @Autowired
	    private PollingAppService pollService;

	    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	    @GetMapping("/user/me")
	    @PreAuthorize("hasRole('USER')")
	    public UserSummary getCurrentUser(@CurrentUser UserPrincipal currentUser) {
	        UserSummary userSummary = new UserSummary(currentUser.getId(), currentUser.getUsername(), currentUser.getName());
	        return userSummary;
	    }

	    @GetMapping("/user/checkUsernameAvailability")
	    public UserIdentityAvailability checkUsernameAvailability(@RequestParam(value = "username") String username) {
	        Boolean isAvailable = !userRepository.existsByUsername(username);
	        return new UserIdentityAvailability(isAvailable);
	    }

	    @GetMapping("/user/checkEmailAvailability")
	    public UserIdentityAvailability checkEmailAvailability(@RequestParam(value = "email") String email) {
	        Boolean isAvailable = !userRepository.existsByEmail(email);
	        return new UserIdentityAvailability(isAvailable);
	    }

	    @GetMapping("/users/{username}")
	    public UserProfile getUserProfile(@PathVariable(value = "username") String username) {
	        User user = userRepository.findByUsername(username)
	                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

	        long pollCount = pollRepository.countByCreatedBy(user.getId());
	        long voteCount = voteRepository.countByUserId(user.getId());

	        UserProfile userProfile = new UserProfile(user.getId(), user.getUsername(), user.getName(), user.getCreatedAt(), pollCount, voteCount);

	        return userProfile;
	    }

	    @GetMapping("/users/{username}/polls")
	    public PagedResponse<PollResponse> getPollsCreatedBy(@PathVariable(value = "username") String username,
	                                                         @CurrentUser UserPrincipal currentUser,
	                                                         @RequestParam(value = "page", defaultValue = Constants.DEFAULT_PAGE_NUMBER) int page,
	                                                         @RequestParam(value = "size", defaultValue = Constants.DEFAULT_PAGE_SIZE) int size) {
	        return pollService.getPollsCreatedBy(username, currentUser, page, size);
	    }


	    @GetMapping("/users/{username}/votes")
	    public PagedResponse<PollResponse> getPollsVotedBy(@PathVariable(value = "username") String username,
	                                                       @CurrentUser UserPrincipal currentUser,
	                                                       @RequestParam(value = "page", defaultValue = Constants.DEFAULT_PAGE_NUMBER) int page,
	                                                       @RequestParam(value = "size", defaultValue = Constants.DEFAULT_PAGE_SIZE) int size) {
	        return pollService.getPollsVotedBy(username, currentUser, page, size);
	    }

	    
}
