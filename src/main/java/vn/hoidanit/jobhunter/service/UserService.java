package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User handleCreateUser(User user) {

        User users = this.userRepository.save(user);
        return users;
    }

    public void handleDeleteUser(Long id) {
        this.userRepository.deleteById(id);
    }

    public List<User> handleGetAllUsers() {
        return this.userRepository.findAll();
    }

    public Optional<User> handleGetAllIdUser(Long id) {

        Optional<User> user = this.userRepository.findById(id);
        return user;
    }

    public Optional<User> handleUpdateUser(Long id) {
        return this.userRepository.findById(id);
    }

    public User handleGetUserByUsername(String username) {
        return this.userRepository.findByEmail(username);
    }

}
