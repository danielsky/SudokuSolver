package com.dskimina;

import java.util.*;

public class SudokuElement {

    private String value;
    private List<String> possibilities;
    private Set<SudokuElement> neighbours = new HashSet<>();

    public SudokuElement(String value, List<String> alphabet) {
        this.value = value;
        this.possibilities = new ArrayList<>(alphabet);
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
