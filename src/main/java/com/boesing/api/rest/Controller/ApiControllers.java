package com.boesing.api.rest.Controller;

import com.boesing.api.rest.Models.User;
import com.boesing.api.rest.Repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
public class ApiControllers {

    @Autowired
    private UserRepo userRepo;
    @GetMapping(value="/")
    public String getPage() {
        return "Welcome";
    }

    @GetMapping(value="/users")
    public List<User> getUsers() {
        return userRepo.findAll();
    }

    @PostMapping(value="/update")
    @ResponseBody
    public Map<String, String> update(@RequestBody User user) {
        Map<String, String> response = new HashMap<>();
        try {
            User existingUser = userRepo.findByUsername(user.getUsername());

            if(existingUser == null) {
                userRepo.save(user);
                response.put("status", "success");
            } else {
                existingUser.setName(user.getName());
                existingUser.setAuthorized(user.isAuthorized());
                existingUser.setPassword(user.getPassword());
                existingUser.setStatus(user.getStatus());
                userRepo.save(existingUser);
                response.put("status", "success");
            }
        } catch (Exception e) {
            response.put("status", "error");
        }
        return response;
    }

    @GetMapping(value="/authenticate")
    @ResponseBody
    public Map<String, Object> authenticate(@RequestBody User user){
        Map<String, Object> response = new HashMap<>();
        try {
            User existingUser = userRepo.findByUsernameAndPassword(user.getUsername(), user.getPassword());
            if (existingUser != null) {
                response.put("Access", true);
                response.put("AccountId", existingUser.getId());
            } else {
                response.put("access", false);
            }
        } catch (Exception e) {
            response.put("access", false);
        }
        return response;
    }

    @GetMapping("/authorize/{id}")
    @ResponseBody
    public Map<String, Object> authorize(@PathVariable long id, @RequestBody User user){
        Map<String, Object> response = new HashMap<>();
        try{
            User existingUser = userRepo.findById(id).get();
            if(existingUser.isAuthorized()) {
                response.put("access", true);
            } else {
                response.put("access", false);
            }
        } catch (Exception e) {
            response.put("access", false);
        }
        return response;
    }
}
