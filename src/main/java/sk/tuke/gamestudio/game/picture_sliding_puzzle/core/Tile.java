package sk.tuke.gamestudio.game.picture_sliding_puzzle.core;

public class Tile {
    private final int number;
    private final TileState state;

    public Tile(int number, TileState state) {
        this.number = number;
        this.state = state;
    }

    public int getNumber() {
        return number;
    }

    public TileState getState() {
        return state;
    }

}
