package com.github.sputnik1111.service.product.domain.user.rest;

import com.github.sputnik1111.common.domain.page.Page;
import com.github.sputnik1111.service.product.domain.user.CreateUserDto;
import com.github.sputnik1111.service.product.domain.user.UserService;
import com.github.sputnik1111.service.product.domain.user.UserView;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserView> create(@RequestBody CreateUserDto request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.create(request));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<UserView> findAll() {
        return new Page<>(userService.findAll());
    }

    @GetMapping(value = "/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Optional<UserView> getById(@PathVariable Long userId) {
        return userService.findById(userId);
    }

    @PatchMapping(value = "/{userId}/username", consumes = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<Void> updateUsername(@PathVariable Long userId, @RequestBody String username) {
        return userService.updateUsername(userId, username)
                ? ResponseEntity.status(HttpStatus.OK).build()
                : ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping(value = "/{userId}")
    public ResponseEntity<Void> delete(@PathVariable Long userId) {
        return userService.delete(userId)
                ? ResponseEntity.status(HttpStatus.OK).build()
                : ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping(value = "/all")
    public ResponseEntity<Void> deleteAll() {
        userService.clear();
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


}
