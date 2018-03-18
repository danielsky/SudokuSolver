package com.dskimina;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SudokuElement {

    private String value;
    private List<String> possibilities = new ArrayList<>();
    private Set<SudokuElement> neighbours = new HashSet<>();

    public SudokuElement() {
        this.value = Sudoku.UNKNOWN;
        for(int i=1;i<=9;i++){
            this.possibilities.add(Integer.toString(i));
        }
    }

    public Set<SudokuElement> getNeighbours() {
        return neighbours;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public List<String> getPossibilities() {
        return possibilities;
    }

    public void setPossibilities(List<String> possibilities) {
        this.possibilities = possibilities;
    }
}
