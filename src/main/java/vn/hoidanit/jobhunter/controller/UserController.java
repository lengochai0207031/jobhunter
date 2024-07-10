package vn.hoidanit.jobhunter.controller;

import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.turkraft.springfilter.boot.Filter;
import vn.hoidanit.jobhunter.domain.ResultPaginationDTO;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.DTO.ResUpdateUserDTO;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.util.annotion.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/users")
    @ApiMessage("create a new users")
    public ResponseEntity<User> createNewUser(
            @RequestBody User createUser) throws IdInvalidException {

        String hashPassWord = this.passwordEncoder.encode(createUser.getPassword());
        createUser.setPassword(hashPassWord);
        boolean isEmailExist = this.userService.isEmailExist(createUser.getEmail());
        if (isEmailExist) {
            throw new IllegalStateException(
                    "Email already exists" + createUser.getEmail() + "đã tồn tại, vui lòng xử dung email khác");
        }
        User newUser = this.userService.handleCreateUser(createUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    @DeleteMapping("/users/{id}")
    @ApiMessage("Delete a user")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") Long id) throws IdInvalidException {
        Optional<User> currentUser = this.userService.handleGetAllIdUser(id);
        if (!currentUser.isPresent()) {
            throw new IdInvalidException("User voi id" + id + " is null");

        }
        // if (id > 1001) {
        // throw new IdInvalidException("id ko lơn hơn 1002");

        // }
        this.userService.handleDeleteUser(id);

        // XÓA LÀ OCEE NHA BAN
        return ResponseEntity.ok(null);
    }

    @GetMapping("/users")
    @ApiMessage("facet all users")
    public ResponseEntity<ResultPaginationDTO> getAllUsers(
            @Filter Specification<User> spec, Pageable pageable) {

        return ResponseEntity.status(HttpStatus.OK).body(this.userService.handleGetAllUsers(spec, pageable));
    }

    @GetMapping("/users/{id}")
    @ApiMessage("facet all users by id")
    public ResponseEntity<Optional<User>> frecUserById(@PathVariable("id") long id) throws IdInvalidException {
        Optional<User> currentUser = this.userService.handleGetAllIdUser(id);

        if (!currentUser.isPresent()) {
            throw new IdInvalidException("User với id = " + id + " không tồn tại");
        }
        return ResponseEntity.status(HttpStatus.OK).body(currentUser);
    }

    @PutMapping("/users")
    @ApiMessage("Update a user")
    public ResponseEntity<ResUpdateUserDTO> updateUser(@RequestBody User user) throws IdInvalidException {
        Optional<User> optionalUser = this.userService.handleUpdateUser(user);
        if (!optionalUser.isPresent()) {
            throw new IdInvalidException("User với id = " + user.getId() + " không tồn tại");
        }

        // có optional nha bạn cần .get dể ms ko bị lỗi
        ResUpdateUserDTO resUpdateUserDTO = this.userService.convertToResUpdateUserDTO(optionalUser.get());
        return ResponseEntity.ok(resUpdateUserDTO);
    }

}
