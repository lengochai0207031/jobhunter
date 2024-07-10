package vn.hoidanit.jobhunter.controller;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.DTO.ReqLoginDTO;
import vn.hoidanit.jobhunter.domain.DTO.ResLoginDTO;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.util.SecurityUtil;
import vn.hoidanit.jobhunter.util.annotion.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/v1")
public class AuthController {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final SecurityUtil securityUtil;
    private final UserService userService;
    @Value("${hoidanit.jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiration;

    public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder, SecurityUtil security,
            UserService userService) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.securityUtil = security;
        this.userService = userService;
    }

    @PostMapping("/auth/login")
    public ResponseEntity<ResLoginDTO> login(@Valid @RequestBody ReqLoginDTO loginDTO) {
        // Nạp input gồm username/password vào Security
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginDTO.getUsername(), loginDTO.getPassword());
        // xác thực người dùng => cần viết hàm loadUserByUsername
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        // create token
        // String access_token = this.securityUtil.createAccessToken(authentication,
        // token.getUser());
        // nạp thông tin (nếu xử lý thành công) vào SecurityContext
        SecurityContextHolder.getContext().setAuthentication(authentication);
        ResLoginDTO token = new ResLoginDTO();
        User currentUserDB = this.userService.handleGetUserByUsername(loginDTO.getUsername());

        if (currentUserDB != null) {
            ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(
                    currentUserDB.getId(),
                    currentUserDB.getName(),
                    currentUserDB.getEmail());
            token.setUser(userLogin);

        }
        // create assess token
        String access_token = this.securityUtil.createAccessToken(authentication.getName(), token.getUser());
        token.setAccessToken(access_token);
        // create Refresh token
        String refresh_token = this.securityUtil.createRefreshToken(loginDTO.getUsername(), token);
        // SET COOKIE
        ResponseCookie responseCookie = ResponseCookie
                .from("refresh_token", refresh_token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .build();
        // update user
        this.userService.updatedUserToken(refresh_token, loginDTO.getUsername());
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(token);
    }

    @GetMapping("/auth/account")
    @ApiMessage("fetch account message")
    public ResponseEntity<ResLoginDTO.UserLogin> getAccount() {
        // Retrieve the current user's JWT and fetch the email
        String email = SecurityUtil.getCurrentUserLogin().isPresent()
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";
        // Fetch the user from the database using the email
        User currentUserDB = this.userService.handleGetUserByUsername(email);

        // Create a UserLogin DTO to hold the user details
        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin();
        if (currentUserDB != null) {
            userLogin.setId(currentUserDB.getId());
            userLogin.setEmail(currentUserDB.getEmail());
            userLogin.setName(currentUserDB.getName());
        }

        // Return the user details as the response
        return ResponseEntity.ok(userLogin);
    }

    @GetMapping("/auth/refresh")
    @ApiMessage("Refresh token message")
    public ResponseEntity<ResLoginDTO> getRefreshToken(@CookieValue(name = "refresh_token") String refreshToken)
            throws IdInvalidException {
        // Validate the refresh token
        Jwt decodedToken = this.securityUtil.checkValidRefreshToken(refreshToken);
        String email = decodedToken.getSubject();

        // Check user by token and email
        User currentUser = this.userService.findByRefreshTokenAndEmail(refreshToken, email);
        if (currentUser == null) {
            throw new IdInvalidException("Refresh token is not valid");
        }

        // Prepare the response DTO
        ResLoginDTO token = new ResLoginDTO();
        User currentUserDB = this.userService.handleGetUserByUsername(email);
        if (currentUserDB != null) {
            ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(
                    currentUserDB.getId(), currentUserDB.getName(), currentUserDB.getEmail());
            token.setUser(userLogin);
        }

        // Create a new access token
        String accessToken = this.securityUtil.createAccessToken(email, token.getUser());
        token.setAccessToken(accessToken);

        // Create a new refresh token
        String newRefreshToken = this.securityUtil.createRefreshToken(email, token);

        // Update user with the new refresh token
        this.userService.updatedUserToken(newRefreshToken, email);

        // Set the new refresh token as a cookie
        ResponseCookie responseCookie = ResponseCookie
                .from("refresh_token", newRefreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .build();

        // Return the response with the new tokens
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(token);
    }

    @PostMapping("/auth/logout")
    @ApiMessage("Logout User")
    public ResponseEntity<Void> logout() throws IdInvalidException {
        // Get the current user's email from the security context
        String email = SecurityUtil.getCurrentUserLogin().orElse("");

        // Check if the email is empty, indicating an invalid access token
        if (email.isEmpty()) {
            throw new IdInvalidException("Invalid Access Token");
        }

        // Update the user's refresh token to null in the database
        this.userService.updatedUserToken(null, email);

        // Remove the refresh token cookie by setting its maxAge to 0
        ResponseCookie deleteCookie = ResponseCookie
                .from("refresh_token", null)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();

        // Return the response with the deleted cookie
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                .build();
    }

}