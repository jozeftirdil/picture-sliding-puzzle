package sk.tuke.gamestudio.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import sk.tuke.gamestudio.entity.Rating;

@Transactional
public class RatingServiceJPA implements RatingService {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void setRating(Rating rating) throws RatingException {
        entityManager.merge(rating);
    }

    @Override
    public int getAverageRating(String game) throws RatingException {
        Double average = (Double) entityManager
                .createNamedQuery("Rating.getAverageRating")
                .setParameter("game", game)
                .getSingleResult();

        if(average != null) {
            return (int) Math.round(average);
        }else {
            return 0;
        }

    }

    @Override
    public int getRating(String game, String player) throws RatingException {
        try {
            Rating result = (Rating) entityManager
                    .createNamedQuery("Rating.getRating")
                    .setParameter("game", game)
                    .setParameter("player", player)
                    .getSingleResult();
            return result.getRating();
        } catch (NoResultException e) {
            return 0;
        }
    }

    @Override
    public void reset() {
        entityManager.createNamedQuery("Rating.resetRatings").executeUpdate();
    }
}
