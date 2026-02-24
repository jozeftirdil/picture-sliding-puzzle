package sk.tuke.gamestudio.service;

import sk.tuke.gamestudio.entity.Rating;

import java.sql.*;

public class RatingServiceJDBC implements RatingService {
    public static final String URL = "jdbc:postgresql://localhost:5432/gamestudio";
    public static final String USER = "postgres";
    public static final String PASSWORD = "postgres";
    private static final String SELECT_AVERAGE_RATING = "SELECT AVG(rating) FROM rating WHERE game = ?";
    private static final String SELECT_PLAYER_RATING = "SELECT rating FROM rating WHERE game = ? AND player = ?";
    public static final String DELETE = "DELETE FROM rating";
    public static final String INSERT = "INSERT INTO rating (game, player, rating, rated_on) VALUES (?, ?, ?, NOW())" +
            "ON CONFLICT (player, game) DO UPDATE SET rating = EXCLUDED.rating, rated_on = NOW()";

    @Override
    public void setRating(Rating rating) throws RatingException {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(INSERT)
        ) {
            statement.setString(1, rating.getGame());
            statement.setString(2, rating.getPlayer());
            statement.setInt(3, rating.getRating());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RatingException("Problem setting rating", e);
        }
    }

    @Override
    public int getAverageRating(String game) throws RatingException {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(SELECT_AVERAGE_RATING)
        ) {
            statement.setString(1, game);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return 0;
            }
        } catch (SQLException e) {
            throw new RatingException("Problem setting rating", e);
        }
    }

    @Override
    public int getRating(String game, String player) throws RatingException {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(SELECT_PLAYER_RATING)
        ) {
            statement.setString(1, game);
            statement.setString(2, player);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return 0;
            }
        } catch (SQLException e) {
            throw new RatingException("Problem selecting rating", e);
        }
    }

    @Override
    public void reset() {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement()
        ) {
            statement.executeUpdate(DELETE);
        } catch (SQLException e) {
            throw new RatingException("Problem deleting rating", e);
        }
    }
}
