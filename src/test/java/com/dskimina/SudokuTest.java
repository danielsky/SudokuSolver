package com.dskimina;

import org.junit.Assert;
import org.junit.Test;

public class SudokuTest {

    @Test
    public void testGetZone(){
        Sudoku sudoku = new Sudoku();
        Assert.assertEquals(0, sudoku.getZone(0));
        Assert.assertEquals(0, sudoku.getZone(1));
        Assert.assertEquals(0, sudoku.getZone(2));

        Assert.assertEquals(1, sudoku.getZone(3));
        Assert.assertEquals(1, sudoku.getZone(4));
        Assert.assertEquals(1, sudoku.getZone(5));

        Assert.assertEquals(2, sudoku.getZone(6));
        Assert.assertEquals(2, sudoku.getZone(7));
        Assert.assertEquals(2, sudoku.getZone(8));
    }

}
