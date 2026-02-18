package edu.usc.csci310.project.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import edu.usc.csci310.project.config.ApiKeyHolder;
import edu.usc.csci310.project.model.FavoritePark;
import edu.usc.csci310.project.service.FavoriteParkService;
import edu.usc.csci310.project.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@RestController
public class FavoriteParkController {
    private final ApiKeyHolder apiKeyHolder;

    private RestTemplate restTemplate = new RestTemplate();

    private final FavoriteParkService favoriteParkService;



    public void setRestTemplate(RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }

    public FavoriteParkController(ApiKeyHolder apiKeyHolder, FavoriteParkService favoriteParkService) {
        this.apiKeyHolder = apiKeyHolder;
        this.favoriteParkService = favoriteParkService;
    }

    @GetMapping("/getUserFavorites")
    public ResponseEntity<?> getFavoritesByUser(Authentication authentication) {
        String username = authentication.getName();
        List<FavoritePark> favorites = favoriteParkService.getFavoriteParksByUserUsername(username);

        List<JsonNode> enrichedParkDetails = getParkInfoFromParkCodeListAndAddIsFavorite(
                favorites.stream().map(FavoritePark::getParkCode).collect(Collectors.toList())).getBody();

        Map<String, Integer> parkCodeToRanking = favorites.stream()
                .collect(Collectors.toMap(FavoritePark::getParkCode, FavoritePark::getRanking));

        enrichedParkDetails.forEach(park -> {
            String parkCode = park.get("parkCode").asText();
            if (parkCodeToRanking.containsKey(parkCode)) {
                ((ObjectNode) park).put("ranking", parkCodeToRanking.get(parkCode));
            }
        });

        enrichedParkDetails.sort(Comparator.comparing(park -> park.get("ranking").asInt()));

        return ResponseEntity.ok(enrichedParkDetails);
    }

    @DeleteMapping("/deleteUserFavorite")
    public ResponseEntity<?> deleteFavoritePark(@RequestParam String parkCode, Authentication authentication){
        String username = authentication.getName();
        return ResponseEntity.ok(favoriteParkService.deleteFavoritePark(username, parkCode));
    }

    @PostMapping("/addUserFavorite")
    public ResponseEntity<?> addUserFavorite(@RequestParam String parkCode, Authentication authentication){
        String username = authentication.getName();
        return ResponseEntity.ok(favoriteParkService.addFavoritePark(username, parkCode));
    }

    public ResponseEntity<List<JsonNode>> getParkInfoFromParkCodeListAndAddIsFavorite(List<String> favParksCodes) {
        String url = "https://developer.nps.gov/api/v1/parks?limit=10000&api_key="+apiKeyHolder.getApiKey();
        JsonNode response = restTemplate.getForObject(url, JsonNode.class);
        List<JsonNode> filteredParks = new ArrayList<>();

        if (response != null && response.has("data")) {
            Iterator<JsonNode> elements = response.get("data").elements();

            while (elements.hasNext()) {
                JsonNode park = elements.next();
                String parkCode = park.get("parkCode").asText();
                if (favParksCodes.contains(parkCode)) {
                    ObjectNode parkNode = park.deepCopy();
                    parkNode.put("isFavorite", true);
                    filteredParks.add(parkNode);
                }
            }
        }

        return ResponseEntity.ok(filteredParks);
    }

    @PostMapping("/moveParkUp/{parkCode}")
    public ResponseEntity<?> moveParkUpInRanking(Authentication authentication, @PathVariable String parkCode) {
        String username = authentication.getName();
        List<FavoritePark> favorites = favoriteParkService.getFavoriteParksByUserUsername(username);
        favorites.sort(Comparator.comparingInt(FavoritePark::getRanking));

        int index = -1;
        for (int i = 0; i < favorites.size(); i++) {
            if (favorites.get(i).getParkCode().equals(parkCode)) {
                index = i;
                break;
            }
        }
        if (index == -1) {
            return ResponseEntity.badRequest().body("Park not found in user's favorites");
        } else if (index == 0) {
            return ResponseEntity.ok().body("Park is already at the top");
        }

        FavoritePark temp = favorites.get(index - 1);
        favorites.set(index - 1, favorites.get(index));
        favorites.set(index, temp);

        favorites.forEach(fav -> System.out.println((fav.getParkCode() + fav.getRanking())));

        favoriteParkService.updateFavoriteParks(username, favorites);
        return ResponseEntity.ok().body("Park moved up successfully");
    }

    @PostMapping("/moveParkDown/{parkCode}")
    public ResponseEntity<?> moveParkDownInRanking(Authentication authentication, @PathVariable String parkCode) {
        String username = authentication.getName();
        List<FavoritePark> favorites = favoriteParkService.getFavoriteParksByUserUsername(username);
        favorites.sort(Comparator.comparingInt(FavoritePark::getRanking));
        int index = -1;
        for (int i = 0; i < favorites.size(); i++) {
            if (favorites.get(i).getParkCode().equals(parkCode)) {
                index = i;
                break;
            }
        }
        if (index == -1) {
            return ResponseEntity.badRequest().body("Park not found in user's favorites");
        } else if (index == favorites.size()-1) {
            return ResponseEntity.ok().body("Park is already at the bottom");
        }

        FavoritePark temp = favorites.get(index +1);
        favorites.set(index +1, favorites.get(index));
        favorites.set(index, temp);
        favoriteParkService.updateFavoriteParks(username, favorites);
        return ResponseEntity.ok().body("Park moved down successfully");
    }

    @PostMapping("/commonFavorites")
    public ResponseEntity<Map<String, List<String>>> findCommonFavoriteParks(@RequestBody List<String> usernames) {
        Map<String, List<String>> commonFavoriteParksCodes = favoriteParkService.findCommonFavoriteParksWithUsers(usernames);

        String url = "https://developer.nps.gov/api/v1/parks?limit=10000&api_key=" + apiKeyHolder.getApiKey();
        JsonNode response = restTemplate.getForObject(url, JsonNode.class);
        Map<String, String> parkCodeToNameMap = new HashMap<>();
        if (response != null && response.has("data")) {
            response.get("data").forEach(park -> {
                String parkCode = park.get("parkCode").asText();
                String parkName = park.get("fullName").asText();
                parkCodeToNameMap.put(parkCode, parkName);
            });
        }

        Map<String, List<String>> commonFavoriteParksNames = commonFavoriteParksCodes.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> parkCodeToNameMap.getOrDefault(entry.getKey(), "Unknown Park"),
                        Map.Entry::getValue
                ));

        return ResponseEntity.ok(commonFavoriteParksNames);
    }

    @PostMapping("/suggestFavoritePark")
    public ResponseEntity<String> suggestFavoritePark(@RequestBody List<String> usernames) {
        String suggestedParkCode = favoriteParkService.suggestCommonFavoritePark(usernames);

        if (suggestedParkCode != null) {
            String url = "https://developer.nps.gov/api/v1/parks?parkCode=" + suggestedParkCode +
                    "&api_key=" + apiKeyHolder.getApiKey();
            JsonNode response = restTemplate.getForObject(url, JsonNode.class);
            if (response != null && response.has("data")) {
                JsonNode park = response.get("data").elements().next();
                String parkName = park.get("fullName").asText();
                System.out.println(parkName);
                return ResponseEntity.ok(parkName);

            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Park information could not be retrieved.");
            }
        } else {
            return ResponseEntity.badRequest().body("No common favorite park found to suggest one.");
        }
    }

    @DeleteMapping("/deleteAllFavorites")
    public ResponseEntity<?> deleteAllFavoritesByUser(Authentication authentication){
        String username = authentication.getName();
        favoriteParkService.deleteAllFavoritesByUserUsername(username);
        return ResponseEntity.ok().build();
    }

}
