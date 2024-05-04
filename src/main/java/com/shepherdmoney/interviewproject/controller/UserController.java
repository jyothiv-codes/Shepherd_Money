package com.shepherdmoney.interviewproject.controller;

import com.shepherdmoney.interviewproject.model.ApplicationUser;
import com.shepherdmoney.interviewproject.repository.UserRepository;
import com.shepherdmoney.interviewproject.vo.request.CreateUserPayload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    // TODO: wire in the user repository (~ 1 line)
    @Autowired
    UserRepository repo;
    @PutMapping("/user")
    public ResponseEntity<Integer> createUser(@RequestBody CreateUserPayload payload) {
        // TODO: Create an user entity with information given in the payload, store it in the database
        //       and return the id of the user in 200 OK response
        ApplicationUser user = new ApplicationUser();
        user.setName(payload.getName());
        user.setEmail(payload.getEmail());
        repo.save(user);
        int userId=user.getId();
        return ResponseEntity.ok(userId);
    }

    @DeleteMapping("/user")
    public ResponseEntity<String> deleteUser(@RequestParam int userId) {
        // TODO: Return 200 OK if a user with the given ID exists, and the deletion is successful
        //       Return 400 Bad Request if a user with the ID does not exist
        //       The response body could be anything you consider appropriate
        if (repo.existsById(userId)){
            repo.deleteById(userId);
            return ResponseEntity.ok("User deleted successfully");
        }
        else{
            return ResponseEntity.badRequest().body("User ID doesn't exist");
        }

    }
}
