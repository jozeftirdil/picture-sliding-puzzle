package sk.tuke.gamestudio.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;
import sk.tuke.gamestudio.entity.Rating;

public class RatingServiceRestClient implements RatingService {
    private final String url = "http://localhost:8080/api/rating";

    @Autowired
    private RestTemplate restTemplate;
    //private RestTemplate restTemplate = new RestTemplate();

    @Override
    public void setRating(Rating rating) {
        restTemplate.postForEntity(url, rating, Rating.class);
    }

    @Override
    public int getAverageRating(String game) {
        Integer average = restTemplate.getForObject(url + "/" + game + "/average", Integer.class);

        if(average != null) {
            return average;
        } else {
            return 0;
        }
    }

    @Override
    public int getRating(String game, String player) {
        Integer rating = restTemplate.getForObject(url + "/" + game + "/" + player, Integer.class);

        if (rating != null) {
            return rating;
        } else {
            return 0;
        }
    }

    @Override
    public void reset() {
        throw new UnsupportedOperationException("Not supported via web service");
    }
}
