package sk.tuke.gamestudio.game.picture_sliding_puzzle;

import sk.tuke.gamestudio.game.picture_sliding_puzzle.consoleui.ConsoleUI;
import sk.tuke.gamestudio.game.picture_sliding_puzzle.core.Field;

public class PictureSlidingPuzzle {
    public static void main(String[] args) {
        var field = new Field(3);
        var ui = new ConsoleUI(field);
        ui.play();
    }
}
