package sk.tuke.gamestudio.service;

import sk.tuke.gamestudio.entity.Score;

import java.util.List;

public interface ScoreService {
    void addScore(Score score) throws sk.tuke.gamestudio.service.ScoreException;
    List<Score> getTopScores(String game) throws sk.tuke.gamestudio.service.ScoreException;
    void reset() throws sk.tuke.gamestudio.service.ScoreException;
}
