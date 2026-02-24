package sk.tuke.gamestudio.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.WebApplicationContext;
import sk.tuke.gamestudio.entity.Comment;
import sk.tuke.gamestudio.entity.Rating;
import sk.tuke.gamestudio.entity.Score;
import sk.tuke.gamestudio.entity.User;
import sk.tuke.gamestudio.game.picture_sliding_puzzle.core.Field;
import sk.tuke.gamestudio.game.picture_sliding_puzzle.core.Tile;
import sk.tuke.gamestudio.game.picture_sliding_puzzle.core.TileState;
import sk.tuke.gamestudio.service.CommentService;
import sk.tuke.gamestudio.service.RatingService;
import sk.tuke.gamestudio.service.ScoreService;

import java.util.Date;

@Controller
@RequestMapping("/puzzle")
@Scope(WebApplicationContext.SCOPE_SESSION)
public class PictureSlidingPuzzleController {
    @Autowired
    private UserController userController;

    @Autowired
    private ScoreService scoreService;

    @Autowired
    private RatingService ratingService;

    @Autowired
    private CommentService commentService;

    private int fieldSize = 3;

    private Field field = new Field(fieldSize);

    @RequestMapping
    public String pictureSlidingPuzzle(@RequestParam(required = false) Integer row,@RequestParam(required = false) Integer column, Model model) {
        if(row != null && column != null && !field.isSolved()) {
            field.moveTile(row, column);
            if(field.isSolved() && userController.isLogged()){
                scoreService.addScore(new Score("PictureSlidingPuzzle", userController.getLoggedUser().getLogin(), field.getScore(), new Date()));
            }
        }
        model.addAttribute("htmlField", getHtmlField());
        model.addAttribute("state", getState());
        model.addAttribute("score", getScore());
        model.addAttribute("move", getMoveCounter());
        model.addAttribute("maxMoves", getMaxMoves());
        model.addAttribute("scores", scoreService.getTopScores("PictureSlidingPuzzle"));
        return "pictureSlidingPuzzle";
    }

    @RequestMapping("/new")
    public String newGame(@RequestParam(defaultValue = "3") int size, Model model) {
        this.fieldSize = size;
        field = new Field(fieldSize);
        model.addAttribute("htmlField", getHtmlField());
        model.addAttribute("state", getState());
        model.addAttribute("score", getScore());
        model.addAttribute("move", getMoveCounter());
        model.addAttribute("maxMoves", getMaxMoves());
        model.addAttribute("scores", scoreService.getTopScores("PictureSlidingPuzzle"));
        return "pictureSlidingPuzzle";
    }

    @RequestMapping("/rateandcomment")
    public String rateAndComment(Model model) {
        model.addAttribute("avgrating", ratingService.getAverageRating("PictureSlidingPuzzle"));
        model.addAttribute("comments", commentService.getComments("PictureSlidingPuzzle"));
        if (userController.isLogged()) {
            model.addAttribute("rating", ratingService.getRating("PictureSlidingPuzzle", userController.getLoggedUser().getLogin()));
        }
        return "rateAndComment";
    }

    @RequestMapping("/rate")
    public String rate(@RequestParam("rating") Integer rating) {
        if(userController.isLogged()){
            ratingService.setRating(new Rating("PictureSlidingPuzzle", userController.getLoggedUser().getLogin(), rating, new Date()));
        }

        return "redirect:/puzzle/rateandcomment";
    }

    @RequestMapping("/comment")
    public String comment(@RequestParam("comment") String comment) {
        if(userController.isLogged()){
            commentService.addComment(new Comment("PictureSlidingPuzzle", userController.getLoggedUser().getLogin(), comment, new Date()));
        }

        return "redirect:/puzzle/rateandcomment";
    }

    private String getState() {
        return field.getState().toString();
    }

    private String getScore() {
        return String.valueOf(field.getScore());
    }

    private String getMoveCounter() {
        return String.valueOf(field.getMoveCounter());
    }

    private String getMaxMoves() {
        return String.valueOf(field.getMaxMoves());
    }

    private String getHtmlField() {
        StringBuilder sb = new StringBuilder();
        sb.append("<table class='puzzle'>\n");
        for (var row = 0; row < field.getRowCount(); row++) {
            sb.append("<tr>\n");
            for (var column = 0; column < field.getColumnCount(); column++) {
                var tile = field.getTile(row, column);
                String tileClass = tile.getState() == TileState.EMPTY ? "tile empty" : "tile covered";
                String tileText = tile.getState() == TileState.EMPTY ? "" : String.valueOf(tile.getNumber());

                sb.append("<td>\n");
                sb.append("<a href='/puzzle?row=" + row + "&column=" + column + "'>\n");
                sb.append("<div class='" + tileClass + "'>" + tileText + "</div>");
                sb.append("</a>\n");
                sb.append("</td>\n");
            }
            sb.append("</tr>\n");
        }

        sb.append("</table>");
        return sb.toString();
    }

    /*private String getImageName(Tile tile) {
        switch (tile.getState()) {
            case COVERED:
                return "open" + tile.getNumber();
            case EMPTY:
                return "mine";
            default:
                throw new RuntimeException("Unexpected tile state: ");
        }
    }*/
}
