package sk.tuke.gamestudio.game.picture_sliding_puzzle.consoleui;

import org.springframework.beans.factory.annotation.Autowired;
import sk.tuke.gamestudio.entity.Score;
import sk.tuke.gamestudio.entity.Comment;
import sk.tuke.gamestudio.entity.Rating;
import sk.tuke.gamestudio.game.picture_sliding_puzzle.core.Field;
import sk.tuke.gamestudio.game.picture_sliding_puzzle.core.GameState;
import sk.tuke.gamestudio.service.ScoreService;
import sk.tuke.gamestudio.service.CommentService;
import sk.tuke.gamestudio.service.RatingService;

import java.util.Date;
import java.util.Scanner;
import java.util.regex.Pattern;

public class ConsoleUI {
    private Field field;

    @Autowired
    private ScoreService scoreService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private RatingService ratingService;

    private final Scanner scanner = new Scanner(System.in);

    private static final Pattern REGEXP = Pattern.compile("([a-z])([0-9])");

    public static final String ANSI_RESET = "\u001B[0m";

    public static final String ANSI_RED = "\u001B[31m";

    public static final String ANSI_BLUE = "\u001B[34m";

    public static final String ANSI_YELLOW = "\u001B[33m";

    public static final String ANSI_GREEN = "\u001B[32m";

    public static final String ANSI_PURPLE = "\u001B[35m";

    public static final String ANSI_CYAN = "\u001B[36m";

    public ConsoleUI(Field field) {
        this.field = field;
    }


    public void play() {
        boolean playAgain;

        newGame();

        showTopScores();

        do {
            do {
                show();
                handleInput();
            } while (field.getState() == GameState.PLAYING);

            show();
            printResult();
            handleWinner();

            playAgain = handlePlayAgain();
            if (playAgain) {
                field = new Field(3);
            }
        }while (playAgain);

        printFarewell();
    }

    private void show() {
        printHeader();
        printField();
    }

    private void printHeader() {
        System.out.println();
        System.out.print("          ");
        System.out.print(ANSI_YELLOW + "Moves: " + field.getMoveCounter() + "/" + field.getMaxMoves() + ANSI_RESET);
        System.out.println(ANSI_PURPLE + "  Score: " + field.getScore() + ANSI_RESET);
    }

    private void printField() {
        System.out.print("               ");
        for (var column = 0; column < field.getColumnCount(); column++) {
            System.out.print(ANSI_BLUE + "   " + (column+1) + ANSI_RESET);
        }
        System.out.println();

        System.out.println("                +" + "---+".repeat(field.getColumnCount()));

        for (var row = 0; row < field.getRowCount(); row++) {
            System.out.print("             ");
            System.out.print(ANSI_BLUE + (char) ('A' + row) + ANSI_RESET + "  |");

            for (var column = 0; column < field.getColumnCount(); column++) {
                var tile = field.getTile(row, column);
                switch (tile.getState()) {
                    case COVERED:
                        System.out.print(ANSI_GREEN + " " + tile.getNumber() + ANSI_RESET + " |");
                        break;
                    case EMPTY:
                        System.out.print(ANSI_RED + " E" + ANSI_RESET + " |");
                        break;
                }
            }
            System.out.println();
            System.out.println("                +" + "---+".repeat(field.getColumnCount()));
        }
    }

    private void printResult(){
        if (field.getState() == GameState.SOLVED) {
            System.out.print(ANSI_YELLOW + "         *****" + ANSI_RESET);
            System.out.print(ANSI_GREEN + " Puzzle solved! " + ANSI_RESET);
            System.out.println(ANSI_YELLOW + "*****" + ANSI_RESET);
        } else if (field.getState() == GameState.FAILED) {
            System.out.println(ANSI_RED + "               XXX Failed! XXX" + ANSI_RESET);
        }
    }

    private void printFarewell() {
        System.out.print(ANSI_YELLOW + "      ***** THANK YOU FOR PLAYING! *****" + ANSI_RESET);
    }

    private void newGame() {
        System.out.println();
        System.out.println();
        System.out.print(ANSI_CYAN + "  ***** WELCOME TO PICTURE SLIDING PUZZLE! *****" + ANSI_RESET);
        System.out.println();

        int averageRating = ratingService.getAverageRating("PictureSlidingPuzzle");
        System.out.println(ANSI_GREEN + "             AVG Rating: " + averageRating + "/5" + ANSI_RESET);

        System.out.print(ANSI_YELLOW + "            Press <S> to start: " + ANSI_RESET);

        var line = scanner.nextLine().trim().toLowerCase();

        while (!"s".equals(line)) {
            System.out.print(ANSI_YELLOW + "            Press <S> to start: " + ANSI_RESET);
            line = scanner.nextLine().trim().toLowerCase();
        }
    }

    private void handleInput(){
        System.out.println();
        System.out.print(ANSI_CYAN + "  Please enter your command <X> EXIT, <A1> MOVE : " + ANSI_RESET);
        var line = scanner.nextLine().trim().toLowerCase();

        if("x".equals(line)) {
            printFarewell();
            System.exit(0);
        }

        var matcher = REGEXP.matcher(line);
        if(matcher.matches()) {
            var row = matcher.group(1).charAt(0) - 'a';
            var column = Integer.parseInt(matcher.group(2)) - 1;

            if (row >= 0 && row < field.getRowCount() && column >= 0 && column < field.getColumnCount()) {
                field.moveTile(row, column);
            }
        }
    }

    private boolean handlePlayAgain() {
        System.out.print(ANSI_CYAN + "         Play again? <Y> YES, <N> NO : " + ANSI_RESET);
        var line = scanner.next().trim().toLowerCase();

        while (!"y".equals(line) && !"n".equals(line)) {
            System.out.print(ANSI_CYAN + "         Play again? <Y> YES, <N> NO : " + ANSI_RESET);
            line = scanner.nextLine().trim().toLowerCase();
        }

        return "y".equals(line);
    }

    private void handleWinner() {
        System.out.print(ANSI_CYAN + "         Enter your name: " + ANSI_RESET);
        String playerName = scanner.nextLine().trim();

        scoreService.addScore(new Score("PictureSlidingPuzzle", playerName, field.getScore(), new Date()));

        handleComment(playerName);
        handleRating(playerName);
    }

    private void handleComment(String playerName) {
        showComments();

        System.out.print(ANSI_CYAN + "         Do you want to leave comment? <y> YES: " + ANSI_RESET);
        String choice = scanner.nextLine().trim().toLowerCase();

        if (choice.equals("y")) {
            System.out.print(ANSI_CYAN + "         Enter comment: " + ANSI_RESET);
            String commentText = scanner.nextLine().trim();

            commentService.addComment(new Comment("PictureSlidingPuzzle", playerName, commentText, new Date()));
        }
    }

    private void showComments() {
        System.out.println(ANSI_PURPLE + "                COMMENTS: " + ANSI_RESET);

        var comments = commentService.getComments("PictureSlidingPuzzle");

        for (int i = 0; i < comments.size(); i++) {
            Comment comment = comments.get(i);
            System.out.print("              ");
            System.out.println(ANSI_PURPLE + ((i + 1) + ". " + comment.getPlayer() + ": " + comment.getComment()) + ANSI_RESET);
        }

        System.out.println();
        System.out.println();
    }

    private void handleRating(String playerName) {
        System.out.print(ANSI_CYAN + "         Rate game? <y> YES: " + ANSI_RESET);
        String choice = scanner.nextLine().trim().toLowerCase();

        if (choice.equals("y")) {
            System.out.print(ANSI_CYAN + "         Enter rating (1-5): " + ANSI_RESET);
            int ratingValue = scanner.nextInt();
            scanner.nextLine();

            if (ratingValue < 1 || ratingValue > 5) {
                System.out.print(ANSI_CYAN + "         Invalid rating. Enter rating (1-5)." + ANSI_RESET);
            } else {
                ratingService.setRating(new Rating("PictureSlidingPuzzle", playerName, ratingValue, new Date()));
            }
        }
    }

    private void showTopScores() {
        System.out.println(ANSI_PURPLE + "                 TOP SCORES: " + ANSI_RESET);

        var scores = scoreService.getTopScores("PictureSlidingPuzzle");

        for (int i = 0; i < scores.size(); i++) {
            Score score = scores.get(i);
            System.out.print("              ");
            System.out.println(ANSI_PURPLE + ((i + 1) + ". " + score.getPlayer() + "   " + score.getPoints()) + ANSI_RESET);
        }

        System.out.println();
        System.out.println();
    }

}
