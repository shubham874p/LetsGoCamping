package edu.usc.csci310.project.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import edu.usc.csci310.project.config.ApiKeyHolder;
import edu.usc.csci310.project.model.FavoritePark;
import edu.usc.csci310.project.service.FavoriteParkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@RestController
public class SearchParksController {
    private final ApiKeyHolder apiKeyHolder;
    @Autowired
    public SearchParksController(ApiKeyHolder apiKeyHolder, FavoriteParkService favoriteParkService) {
        this.apiKeyHolder = apiKeyHolder;
        this.favoriteParkService = favoriteParkService;
    }
    private RestTemplate restTemplate = new RestTemplate();
    private String firstResultParkName = "";



    private final FavoriteParkService favoriteParkService;

    public RestTemplate getRestTemplate(){
        return this.restTemplate;
    }

    public void setRestTemplate(RestTemplate restTemplate){this.restTemplate = restTemplate;}



    @GetMapping("/amenities")
    public ResponseEntity<List<JsonNode>> getAmenities() {
        String url = "https://developer.nps.gov/api/v1/amenities?limit=1000&api_key=" + apiKeyHolder.getApiKey();
        JsonNode response = restTemplate.getForObject(url, JsonNode.class);
        List<JsonNode> amenities = new ArrayList<>();

        if (response != null && response.has("data")) {
            for (JsonNode amenity : response.get("data")) {
                amenities.add(amenity);
            }
        }

        return ResponseEntity.ok(amenities);
    }

    @GetMapping("/amenities/parks")
    public ResponseEntity<List<JsonNode>> getParksByAmenityName(@RequestParam("id") String amenityID, Authentication authentication) {
        String url = "https://developer.nps.gov/api/v1/amenities/parksplaces?limit=1000&id=" + amenityID + "&api_key=" + apiKeyHolder.getApiKey();
        JsonNode response = restTemplate.getForObject(url, JsonNode.class);
        List<JsonNode> parks = new ArrayList<>();


        if (response != null) {
            if (response.has("data")) {
                response.get("data").get(0).forEach(parks::add);
            }
        }


        return returnParksWithInfo(ResponseEntity.ok(parks), authentication);
    }

    @GetMapping("/activities")
    public ResponseEntity<List<JsonNode>> getActivities() {
        String url = "https://developer.nps.gov/api/v1/activities?limit=1000&api_key=" + apiKeyHolder.getApiKey();
        JsonNode response = restTemplate.getForObject(url, JsonNode.class);
        List<JsonNode> activities = new ArrayList<>();

        if (response != null ) {
            if (response.has("data")) {
                for (JsonNode activity : response.get("data")) {
                    activities.add(activity);
                }
            }
        }

        return ResponseEntity.ok(activities);
    }

    @GetMapping("/activities/parks")
    public ResponseEntity<List<JsonNode>> getParksByActivityName(@RequestParam("q") String activityName, Authentication authentication) {
        String url = "https://developer.nps.gov/api/v1/activities/parks?limit=1000&q=" + activityName + "&api_key=" + apiKeyHolder.getApiKey();

        JsonNode response = restTemplate.getForObject(url, JsonNode.class);

        List<JsonNode> parks = new ArrayList<>();
        List<JsonNode> finalList = new ArrayList<>();


        if (response != null && response.has("data")) {
            response.get("data").forEach(parks::add);
        }
        for (JsonNode jn: parks){

            if (jn.get("name").asText().contains(activityName)){
                finalList.add(jn);
                break;
            }
        }
        return returnParksWithInfo(ResponseEntity.ok(finalList), authentication);
    }

    @GetMapping("/search/allParks")
    public ResponseEntity<List<JsonNode>> getAllParks(Authentication authentication) {
        String url = "https://developer.nps.gov/api/v1/parks?limit=1000&api_key="+apiKeyHolder.getApiKey();

        JsonNode response = restTemplate.getForObject(url, JsonNode.class);
        List<JsonNode> filteredParks = new ArrayList<>();

        if (response != null && response.has("data")) {
            Iterator<JsonNode> elements = response.get("data").elements();
            while (elements.hasNext()) {
                JsonNode park = elements.next();
                filteredParks.add(park);
            }
        }


        return ResponseEntity.ok(getListOfParksWithFavoritesAdded(filteredParks, authentication));
    }


    @GetMapping("/search/state/{stateCode}")
    public ResponseEntity<List<JsonNode>> getParksByState(@PathVariable String stateCode, Authentication authentication) {
        String url = "https://developer.nps.gov/api/v1/parks?limit=1000&api_key="+apiKeyHolder.getApiKey();

        JsonNode response = restTemplate.getForObject(url, JsonNode.class);
        List<JsonNode> filteredParks = new ArrayList<>();

        if (response != null && response.has("data")) {
            Iterator<JsonNode> elements = response.get("data").elements();

            while (elements.hasNext()) {
                JsonNode park = elements.next();
                String states = park.get("states").asText();
                if (states.contains(stateCode)) {
                    filteredParks.add(park);
                }
            }
        }

        return ResponseEntity.ok(getListOfParksWithFavoritesAdded(filteredParks, authentication));
    }

    @GetMapping("/topics")
    public ResponseEntity<List<JsonNode>> getTopics() {
        String url = "https://developer.nps.gov/api/v1/topics?limit=1000&api_key=" + apiKeyHolder.getApiKey();

        JsonNode response = restTemplate.getForObject(url, JsonNode.class);
        List<JsonNode> myTopics = new ArrayList<>();
        if (response != null && response.has("data")) {
            for (JsonNode topic : response.get("data")) {
                myTopics.add(topic);
            }
        }

        return ResponseEntity.ok(myTopics);
    }


    @GetMapping("/topics/parks")
    public ResponseEntity<List<JsonNode>> getParksByTopicName(@RequestParam("q") String topicName, Authentication authentication) {
        String url = "https://developer.nps.gov/api/v1/topics/parks?limit=1000&q=" + topicName + "&api_key=" + apiKeyHolder.getApiKey();
        JsonNode response = restTemplate.getForObject(url, JsonNode.class);
        List<JsonNode> parks = new ArrayList<>();

        if (response != null) {
            if (response.has("data")) {
                response.get("data").forEach(parks::add);
            }

        }
        return returnParksWithInfo(ResponseEntity.ok(parks), authentication);
    }

    @GetMapping("/search/name")
    public ResponseEntity<List<JsonNode>> getParksByName(@RequestParam(value="name", defaultValue="") String nameParam, Authentication authentication) throws JsonProcessingException {

        String url = "https://developer.nps.gov/api/v1/parks?limit=1000&api_key=" + apiKeyHolder.getApiKey();
        this.firstResultParkName = "";
        JsonNode response = restTemplate.getForObject(url, JsonNode.class);
        List<JsonNode> filteredParks = new ArrayList<>();

        nameParam = nameParam.strip().toLowerCase();

        if (response != null && response.has("data")) {

            Iterator<JsonNode> elements = response.get("data").elements();

            while (elements.hasNext()) {
                JsonNode park = elements.next();
                String parkName = park.get("fullName").asText();
                if (parkName.toLowerCase().contains(nameParam)) {
                    if(firstResultParkName.isEmpty()){
                        firstResultParkName = parkName;
                    }
                    filteredParks.add(park);
                }
            }
        }else{
            this.firstResultParkName = "ERROR GETTING FROM NATIONAL PARK API";
        }
        return ResponseEntity.ok(getListOfParksWithFavoritesAdded(filteredParks, authentication));
    }

    @GetMapping("/amenities/{parkCode}")
    public ResponseEntity<List<JsonNode>> getAmenitiesByParkCode(@PathVariable String parkCode) {
        String url = "https://developer.nps.gov/api/v1/amenities/parksplaces/?limit=1000&parkCode=" + parkCode + "&api_key=" + apiKeyHolder.getApiKey();
        JsonNode response = restTemplate.getForObject(url, JsonNode.class);
        List<JsonNode> amenities = new ArrayList<>();

        if (response != null && response.has("data")) {
            for (JsonNode amenity : response.get("data")) {
                amenities.add(amenity);
            }
        }

        return ResponseEntity.ok(amenities);
    }


    public ResponseEntity<List<JsonNode>> returnParksWithInfo(ResponseEntity<List<JsonNode>> parksResponseEntity, Authentication authentication) {
//        return parksResponseEntity;

        List<String> ourParks = new ArrayList<>();
        List<JsonNode> info = parksResponseEntity.getBody();
        if (info == null || info.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList()); // Return an empty response
        }
        JsonNode infoBrokenDown = info.get(0);

        JsonNode parks = infoBrokenDown.get("parks");

        if (parks != null) {
            for (JsonNode park : parks) {
                ourParks.add(park.get("parkCode").asText());
            }
        }

        String url = "https://developer.nps.gov/api/v1/parks?limit=1000&api_key="+apiKeyHolder.getApiKey();
        JsonNode response = restTemplate.getForObject(url, JsonNode.class);
        List<JsonNode> filteredParks = new ArrayList<>();

        if (response != null) {
            if (response.has("data")) {

                Iterator<JsonNode> elements = response.get("data").elements();


                while (elements.hasNext()) {
                    JsonNode park = elements.next();
                    String parkCode = park.get("parkCode").asText();
                    if (ourParks.contains(parkCode)) {
                        filteredParks.add(park);
                    }
                }
            }
        }

        return ResponseEntity.ok(getListOfParksWithFavoritesAdded(filteredParks, authentication));
    }

    public List<JsonNode> getListOfParksWithFavoritesAdded(List<JsonNode> parksWithoutFavorites, Authentication authentication){
        List<FavoritePark> favorites = favoriteParkService.getFavoriteParksByUserUsername(authentication.getName());

        List<String> userFavParkCodes = new ArrayList<String>();
        favorites.forEach(park -> userFavParkCodes.add(park.getParkCode()));
        List<JsonNode> finalParks = new ArrayList<JsonNode>();

        for (JsonNode park : parksWithoutFavorites) {
            String parkCode = park.get("parkCode").asText();
            ObjectNode parkNode = park.deepCopy();
            if (userFavParkCodes.contains(parkCode)) {
                parkNode.put("isFavorite", true);
            } else {
                parkNode.put("isFavorite", false);
            }
            finalParks.add(parkNode);

        }
        return finalParks;
    }



}
