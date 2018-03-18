package com.dskimina;

import com.sun.istack.internal.Nullable;

import java.util.*;

public class Sudoku {

    public static final String UNKNOWN = "x";
    private SudokuElement[][] array;

    public Sudoku() {
        array = new SudokuElement[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                array[i][j] = new SudokuElement();
            }
        }
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                addNeighbours(array[i][j], i ,j);
            }
        }
    }

    private Sudoku(Sudoku s, int x, int y, String value) {
        array = new SudokuElement[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                SudokuElement elem = new SudokuElement();
                elem.setValue(s.getElement(i,j).getValue());
                array[i][j] = elem;
            }
        }
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                addNeighbours(array[i][j], i ,j);
            }
        }

        array[x][y].setValue(value);
    }

    private void addNeighbours(SudokuElement element, int x, int y){
        for (int i = 0; i < 9; i++) {
            if(i==y) continue;
            element.getNeighbours().add(array[x][i]);
        }
        for (int i = 0; i < 9; i++) {
            if(i==x) continue;
            element.getNeighbours().add(array[i][y]);
        }
        int xzone = getZone(x);
        int yzone = getZone(y);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int k = xzone*3+i;
                int l = yzone*3+j;
                if(k == x && l == y) continue;
                element.getNeighbours().add(array[k][l]);
            }
        }
    }

    public SudokuElement getElement(int x, int y){
        return array[x][y];
    }

    public boolean solve() throws UnsolvableException{
        boolean t;
        do {
            t = eliminate();
        }while (t);
        MinimalSudokuElementHolder holder = getMinimalSudokuHolder();
        if(holder == null){
            return finalCheck();
        }else {
            for (String possibility : holder.getSudokuElement().getPossibilities()) {
                Sudoku newSudoku = new Sudoku(this, holder.getX(), holder.getY(), possibility);
                try {
                    if (newSudoku.solve()) {
                        for (int i = 0; i < 9; i++) {
                            for (int j = 0; j < 9; j++) {
                                array[i][j].setValue(newSudoku.getElement(i, j).getValue());
                            }
                        }
                    }
                } catch (UnsolvableException e) {
                    //Resolution not found
                }
            }
        }
        return finalCheck();
    }

    private boolean eliminate() throws UnsolvableException{
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                SudokuElement se = array[i][j];
                String value = se.getValue();
                if(!UNKNOWN.equals(value)){
                    for (SudokuElement neighbour : se.getNeighbours()){
                        neighbour.getPossibilities().remove(value);
                    }
                }
            }
        }
        boolean filled = false;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                SudokuElement elem = array[i][j];
                String value = elem.getValue();
                if(UNKNOWN.equals(value)){
                    if(elem.getPossibilities().size() == 1) {
                        elem.setValue(elem.getPossibilities().get(0));
                        filled = true;
                    }else if(elem.getPossibilities().size() == 0){
                        throw new UnsolvableException();
                    }
                }
            }
        }
        return filled;
    }

    private int getZone(int value){
        if(value<0 || value>8) throw new IllegalStateException("Incorrect value: "+value);
        if(value ==0 || value == 1 || value == 2) return 0;
        if(value ==3 || value == 4 || value == 5) return 1;
        else return 2;
    }

    private boolean finalCheck(){
        Set<String> temp = new HashSet<>(9);

        Set<String> control = new HashSet<>(9);
        for (int i = 0; i < 9; i++) {
            control.add(Integer.toString(i+1));
        }

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                temp.add(array[i][j].getValue());
            }
            if(temp.size() != 9) return false;
            temp.removeAll(control);
            if(temp.size() > 0 ) return false;
        }

        temp.clear();

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                temp.add(array[j][i].getValue());
            }
            if(temp.size() != 9) return false;
            temp.removeAll(control);
            if(temp.size() > 0 ) return false;
        }

        temp.clear();

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 3; k++) {
                    for (int l = 0; l < 3; l++) {
                        temp.add(array[i*3+k][j*3+l].getValue());
                    }
                }
                if(temp.size() != 9) return false;
                temp.removeAll(control);
                if(temp.size() > 0 ) return false;
            }
        }

        return true;
    }


    public void print(int iteration){
        System.out.println("Iteration: "+iteration);
        int counter = 0;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                String value = array[i][j].getValue();
                System.out.print(value+" ");
                if(!UNKNOWN.equals(value)){
                    counter++;
                }
            }
            System.out.println();
        }
        System.out.println("Znanych: "+ counter);
    }

    public void printPossibilities(int iteration){
        System.out.println("Possibilities: "+iteration);
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                SudokuElement se = array[i][j];
                String value = se.getValue();
                if(UNKNOWN.equals(value)){
                    System.out.print(se.getPossibilities().size());
                }else{
                    System.out.print(0);
                }
                System.out.print(" ");
            }
            System.out.println();
        }
    }

    @Nullable
    private MinimalSudokuElementHolder getMinimalSudokuHolder(){
        List<MinimalSudokuElementHolder> list = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                SudokuElement se = array[i][j];
                if(UNKNOWN.equals(se.getValue())){
                    MinimalSudokuElementHolder holder = new MinimalSudokuElementHolder();
                    holder.setX(i);
                    holder.setY(j);
                    holder.setSudokuElement(se);
                    list.add(holder);
                }
            }
        }
        if(list.isEmpty()){
            return null;
        }else {
            return Collections.min(list);
        }
    }

    private static class MinimalSudokuElementHolder implements Comparable<MinimalSudokuElementHolder>{
        private int x;
        private int y;
        private SudokuElement sudokuElement;

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

        public SudokuElement getSudokuElement() {
            return sudokuElement;
        }

        public void setSudokuElement(SudokuElement sudokuElement) {
            this.sudokuElement = sudokuElement;
        }

        @Override
        public int compareTo(MinimalSudokuElementHolder o) {
            return this.sudokuElement.getPossibilities().size() - o.sudokuElement.getPossibilities().size();
        }
    }

}
