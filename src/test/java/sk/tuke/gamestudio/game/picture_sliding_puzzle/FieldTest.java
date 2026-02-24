package sk.tuke.gamestudio.game.picture_sliding_puzzle;

import sk.tuke.gamestudio.game.picture_sliding_puzzle.core.Tile;
import sk.tuke.gamestudio.game.picture_sliding_puzzle.core.TileState;
import org.junit.jupiter.api.Test;
import sk.tuke.gamestudio.game.picture_sliding_puzzle.core.Field;

import static org.junit.jupiter.api.Assertions.*;

public class FieldTest {
    private Field field;
    private int puzzleSize;

    public FieldTest() {
        this.puzzleSize = 3;
        this.field = new Field(puzzleSize);
    }

    @Test
    public void testFieldInitialization() {
        assertEquals(puzzleSize, field.getRowCount(), "Row count should be equal to puzzle size.");
        assertEquals(puzzleSize, field.getColumnCount(), "Column count should be equal to puzzle size.");
    }

    @Test
    public void testEmptyTilePresence() {
        int emptyTileCount = 0;

        for (int row = 0; row < field.getRowCount(); row++) {
            for (int column = 0; column < field.getColumnCount(); column++) {
                if (field.getTile(row, column).getState() == TileState.EMPTY) {
                    emptyTileCount++;
                }
            }
        }
        assertEquals(1, emptyTileCount, "One empty tile should be in the field.");
    }

    @Test
    public void testFieldShuffle() {
        assertFalse(field.isSolved(), "Field should not be solved after shuffling.");
    }

    @Test
    public void testPossibleMoveTile() {
        int emptyTileRow = 0;
        int emptyTileColumn = 0;

        for (int row = 0; row < field.getRowCount(); row++) {
            for (int column = 0; column < field.getColumnCount(); column++) {
                if (field.getTile(row, column).getState() == TileState.EMPTY) {
                    emptyTileRow = row;
                    emptyTileColumn = column;
                    break;
                }
            }
        }

        int neighbourTileRow = 0;
        int neighbourTileColumn = 0;

        if (emptyTileRow != 0) {
            neighbourTileRow = emptyTileRow - 1;
            neighbourTileColumn = emptyTileColumn;
        }else {
            neighbourTileRow = emptyTileRow + 1;
            neighbourTileColumn = emptyTileColumn;
        }

        Tile neighbourTile = field.getTile(neighbourTileRow, neighbourTileColumn);

        field.moveTile(neighbourTileRow, neighbourTileColumn);

        assertEquals(neighbourTile, field.getTile(emptyTileRow, emptyTileColumn), "Tile should have moved to the empty space.");
    }

    @Test
    public void testImpossibleMoveTile() {
        int emptyTileRow = 0;
        int emptyTileColumn = 0;

        for (int row = 0; row < field.getRowCount(); row++) {
            for (int column = 0; column < field.getColumnCount(); column++) {
                if (field.getTile(row, column).getState() == TileState.EMPTY) {
                    emptyTileRow = row;
                    emptyTileColumn = column;
                    break;
                }
            }
        }

        Tile emptyTile = field.getTile(emptyTileRow, emptyTileColumn);

        int neighbourTileRow = 0;
        int neighbourTileColumn = 0;

        if (emptyTileRow != 0) {
            neighbourTileRow = emptyTileRow - 1;
            if (emptyTileColumn != 0) {
                neighbourTileColumn = emptyTileColumn - 1;
            }else {
                neighbourTileColumn = emptyTileColumn + 1;
            }

        }else {
            neighbourTileRow = emptyTileRow + 1;
            if (emptyTileColumn != 0) {
                neighbourTileColumn = emptyTileColumn - 1;
            }else {
                neighbourTileColumn = emptyTileColumn + 1;
            }
        }

        field.moveTile(neighbourTileRow, neighbourTileColumn);

        assertEquals(emptyTile, field.getTile(emptyTileRow, emptyTileColumn), "Empty tile should be in same position.");
    }
}
