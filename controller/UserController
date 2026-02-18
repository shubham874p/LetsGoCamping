package edu.usc.csci310.project.controller;

import com.fasterxml.jackson.databind.JsonNode;
import edu.usc.csci310.project.config.ApiKeyHolder;
import edu.usc.csci310.project.model.User;
import edu.usc.csci310.project.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@RestController
public class UserController {

    private final UserService userService;

    private final ApiKeyHolder apiKeyHolder;
    private RestTemplate restTemplate = new RestTemplate();


    public void setRestTemplate(RestTemplate restTemplate){this.restTemplate = restTemplate;}


    public UserController(UserService userService, ApiKeyHolder apiKeyHolder) {
        this.userService = userService;
        this.apiKeyHolder = apiKeyHolder;
    }



    @GetMapping("/listUserFavorites") //all users
    public ResponseEntity<Map<String, List<String>>> getAllUsersAndFavorites() {
        Map<String, List<String>> userFavoritesMap = userService.getUsersAndTheirFavoriteParks();

        String url = "https://developer.nps.gov/api/v1/parks?limit=10000&api_key=" + apiKeyHolder.getApiKey();
        JsonNode response = restTemplate.getForObject(url, JsonNode.class);
        Map<String, String> parkCodeToNameMap = new HashMap<>();
        if (response != null) {
            if (response.has("data")){
                for (JsonNode park : response.get("data")) {
                    String parkCode = park.get("parkCode").asText();
                    String parkName = park.get("fullName").asText();
                    parkCodeToNameMap.put(parkCode, parkName);
                }
            }
        }

        Map<String, List<String>> userFavoritesWithNameMap = userFavoritesMap.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .map(parkCode -> parkCodeToNameMap.getOrDefault(parkCode, "Unknown Park"))
                                .collect(Collectors.toList())
                ));
        return ResponseEntity.ok(userFavoritesWithNameMap);
    }

    @PutMapping("/changeListVisibility")
    public ResponseEntity<?> changeUserListPrivacy(Authentication authentication, @RequestParam boolean privacy){
        String username = authentication.getName();
        boolean success = userService.changeUserListVisibility(username, privacy);
        if (success){
            return ResponseEntity.ok().build();
        }
        else{
            return ResponseEntity.badRequest().build();
        }
    }
    @GetMapping("/getUserVisibility")
    public ResponseEntity<?> getUserVisibility(Authentication authentication){
        Optional<Boolean> privacy = userService.findListVisByUsername(authentication.getName());
        if (privacy.isPresent()){
            return ResponseEntity.ok(Collections.singletonMap("visibility", privacy.get()));
        }
        return ResponseEntity.badRequest().build();
    }




}
