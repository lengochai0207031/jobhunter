package vn.hoidanit.jobhunter.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

@RestController
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/users")
    public ResponseEntity<User> createNewUser(@RequestBody User createUser) {
        // này gán cứng nha bạn
        // User user = new User();
        // user.setName("lengochai");
        // user.setEmail("lengochai@gmail.com");
        // user.setPassword("123456");

        String hashPassWord = this.passwordEncoder.encode(createUser.getPassword());
        createUser.setPassword(hashPassWord);
        // return new ResponseEntity<User>("bạn đã tao thanh
        // cong",responseHeader,HttpStatus.CREATED);
        // này mã phản hồi nha bạn
        User newUser = this.userService.handleCreateUser(createUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    @DeleteMapping("/users/{id}")

    public ResponseEntity<String> deleteUser(@PathVariable("id") Long id) throws IdInvalidException {
        if (id > 1001) {
            throw new IdInvalidException("id ko lơn hơn 1002");

        }
        this.userService.handleDeleteUser(id);

        // XÓA LÀ OCEE NHA BAN
        return ResponseEntity.status(HttpStatus.OK).body("OK");
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = this.userService.handleGetAllUsers();
        return ResponseEntity.status(HttpStatus.OK).body(users);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<Optional<User>> frecUserById(@PathVariable("id") long id) {
        Optional<User> user = this.userService.handleGetAllIdUser(id);

        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<Optional<User>> handleUpdateUser(@RequestBody User reqUser, @PathVariable("id") Long id) {
        Optional<User> optionalUser = this.userService.handleUpdateUser(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setName(reqUser.getName());
            user.setEmail(reqUser.getEmail());
            user.setPassword(reqUser.getPassword());

        }
        User updatedUser = this.userService.handleCreateUser(reqUser);

        return ResponseEntity.status(HttpStatus.OK).body(Optional.of(updatedUser));
    }

}
