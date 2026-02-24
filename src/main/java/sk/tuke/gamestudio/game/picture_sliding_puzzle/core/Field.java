package sk.tuke.gamestudio.game.picture_sliding_puzzle.core;

import java.util.Random;

public class Field {
    private final int rowCount;

    private final int columnCount;

    private final Tile[][] tiles;

    private GameState state;

    private final int moves;

    private final int maxMoves;

    private int moveCounter;

    private int score;

    public Field(int puzzleSize) {
        if (puzzleSize < 3 || puzzleSize > 6)
            throw new IllegalArgumentException("Please enter puzzle size between 3 and 6!");

        this.rowCount = puzzleSize;
        this.columnCount = puzzleSize;
        this.state = GameState.PLAYING;
        this.score = 1000;
        this.tiles = new Tile[rowCount][columnCount];
        this.moves = 3;//(puzzleSize - 2) * 10;
        this.maxMoves = moves + score/20 - 1;
        this.moveCounter = 0;
        generate();
    }

    public Tile getTile(int row, int column) { return tiles[row][column]; }

    public int getRowCount() { return rowCount; }

    public int getColumnCount() { return columnCount; }

    public GameState getState() { return state; }

    public  int getScore() { return score; }

    public int getMaxMoves() { return maxMoves; }

    public int getMoveCounter() { return moveCounter; }

    private void generate() {
        generatePuzzle();
        while (isSolved()) {
            shufflePuzzle();
        }
    }

    private void generatePuzzle() {
        int number = 1;
        for (var row = 0; row < rowCount; row++ ) {
            for (var column = 0; column < columnCount; column++ ) {
                if (row == rowCount - 1 && column == columnCount - 1) {
                    tiles[row][column] = new Tile(0, TileState.EMPTY);
                }else {
                    tiles[row][column] = new Tile(number, TileState.COVERED);
                    number++;
                }
            }
        }
    }

    private void shufflePuzzle() {
        var random = new Random();
        int emptyTileRow = rowCount - 1;
        int emptyTileColumn = columnCount - 1;
        int previousEmptyTileRow = emptyTileRow;
        int previousEmptyTileColumn = emptyTileColumn;

        for (int move = 1; move <= moves; move++) {
            boolean isValidMove = false;
            int randomRow = 0;
            int randomColumn = 0;

            while (!isValidMove) {
                int randomNumber = random.nextInt(4) + 1;

                if ( randomNumber == 1 && emptyTileRow > 0 && emptyTileRow - 1 != previousEmptyTileRow) {
                    isValidMove = true;
                    randomRow = emptyTileRow - 1;
                    randomColumn = emptyTileColumn;
                }else if (randomNumber == 2 && emptyTileRow < rowCount - 1 && emptyTileRow + 1 != previousEmptyTileRow) {
                    isValidMove = true;
                    randomRow = emptyTileRow + 1;
                    randomColumn = emptyTileColumn;
                }else if (randomNumber == 3 && emptyTileColumn > 0 && emptyTileColumn - 1 != previousEmptyTileColumn) {
                    isValidMove = true;
                    randomRow = emptyTileRow;
                    randomColumn = emptyTileColumn - 1;
                }else if (randomNumber == 4 && emptyTileColumn < columnCount - 1 && emptyTileColumn + 1 != previousEmptyTileColumn) {
                    isValidMove = true;
                    randomRow = emptyTileRow;
                    randomColumn = emptyTileColumn + 1;
                }
            }

            swapTiles(randomRow, randomColumn, emptyTileRow, emptyTileColumn);

            previousEmptyTileRow = emptyTileRow;
            previousEmptyTileColumn = emptyTileColumn;
            emptyTileRow = randomRow;
            emptyTileColumn = randomColumn;
        }
    }

    public void moveTile(int row, int column) {
        if (state == GameState.PLAYING) {
            if(tiles[row][column].getState() == TileState.EMPTY) {
                return;
            }

            boolean isMoved = false;

            if (row > 0 && tiles[row-1][column].getState() == TileState.EMPTY) {
                swapTiles(row, column, row-1, column);
                isMoved = true;
            }else if (row < rowCount-1 && tiles[row+1][column].getState() == TileState.EMPTY) {
                swapTiles(row, column, row+1, column);
                isMoved = true;
            }else if (column > 0 && tiles[row][column-1].getState() == TileState.EMPTY) {
                swapTiles(row, column, row, column-1);
                isMoved = true;
            }else if (column < columnCount-1 && tiles[row][column+1].getState() == TileState.EMPTY) {
                swapTiles(row, column, row, column+1);
                isMoved = true;
            }

            if (isMoved) {
                moveCounter++; // zmena
                if(isSolved()) {
                    state = GameState.SOLVED;
                    return;
                }
                //moveCounter++; zmena
                updateScore();
            }
        }
    }

    private void swapTiles(int row, int column, int emptyTileRow, int emptyTileColumn) {
        Tile movableTile = tiles[row][column];
        tiles[row][column] = tiles[emptyTileRow][emptyTileColumn];
        tiles[emptyTileRow][emptyTileColumn] = movableTile;
    }

    public boolean isSolved() {
        int counter = 1;
        for (var row = 0; row < rowCount; row++ ) {
            for (var column = 0; column < columnCount; column++ ) {
                if (tiles[row][column].getNumber() != counter) {
                    if(!(row == rowCount - 1 && column == columnCount - 1)) {
                        return false;
                    }
                }
                counter++;
            }
        }
        return true;
    }

    private void updateScore() {
        if (moveCounter >= moves) {
            score = score - 20;
            if (score == 0) {
                state = GameState.FAILED;
            }
        }
    }
}
