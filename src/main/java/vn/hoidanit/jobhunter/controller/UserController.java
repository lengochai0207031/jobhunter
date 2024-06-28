package vn.hoidanit.jobhunter.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.service.UserService;

@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/user")
    public User createNewUser(@RequestBody User cretaeUser) {
        // User user = new User();
        // user.setName("lengochai");
        // user.setEmail("lengochai@gmail.com");
        // user.setPassword("123456");

        User newUser = this.userService.handleCreateUser(cretaeUser);
        return newUser;
    }

    @DeleteMapping("/user/{id}")

    public String deleteUser(@PathVariable("id") Long id) {

        this.userService.handleDeleteUser(id);
        return "delete thanh cong";
    }

    @GetMapping("/user")
    public List<User> getAllUsers() {
        List<User> users = this.userService.handleGetAllUsers();
        return users;
    }

    @GetMapping("/user/{id}")
    public Optional<User> frecUserById(@PathVariable("id") long id) {
        Optional<User> user = this.userService.handleGetAllIdUser(id);

        return user;
    }

    @PutMapping("/user/{id}")
    public Optional<User> handleUpdateUser(@RequestBody User reqUser, @PathVariable("id") Long id) {
        Optional<User> optionalUser = this.userService.handleUpdateUser(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setName(reqUser.getName());
            user.setEmail(reqUser.getEmail());
            user.setPassword(reqUser.getPassword());

        }
        User updatedUser = this.userService.handleCreateUser(reqUser);
        return Optional.of(updatedUser);
    }

}
