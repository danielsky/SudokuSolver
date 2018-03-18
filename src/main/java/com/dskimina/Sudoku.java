package com.dskimina;

import com.sun.istack.internal.Nullable;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class Sudoku {

    private static final int BLOCK_SIZE = 3;
    private static final int GRID_SIZE = BLOCK_SIZE * BLOCK_SIZE;

    private static final List<String> ALPHABET;
    private static final String UNKNOWN = "x";
    private static final List<String> ALLOWED_VALES;

    static{
        ALPHABET = new ArrayList<>(Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9"));

        List<String> l = new ArrayList<>(ALPHABET);
        l.add(UNKNOWN);
        ALLOWED_VALES = Collections.unmodifiableList(l);
    }


    private SudokuElement[][] array;

    Sudoku(){
        //for test only
    }

    public Sudoku(InputStream is) throws IOException{
        array = new SudokuElement[GRID_SIZE][GRID_SIZE];

        List<String> lines;
        try (InputStream reader = is){
            lines = IOUtils.readLines(reader, "UTF8");
        } catch (IOException e) {
            throw new IOException("Cannot read input file");
        }

        if(lines.size() != GRID_SIZE) throw new IllegalStateException("Length must be equal to 9. Current: "+lines.size());

        for (int i = 0; i < GRID_SIZE; i++) {
            String[] chars = lines.get(i).split(" ");
            if(chars.length != GRID_SIZE){
                throw new IllegalStateException("Length must be equal to 9. Current: "+lines.size());
            }
            for (int j = 0; j < GRID_SIZE; j++) {
                String val = chars[j];
                if(!ALLOWED_VALES.contains(val)){
                    throw new IllegalStateException("Illegal element in grid: " + val);
                }
                array[i][j] = new SudokuElement(val, ALPHABET);
            }
        }
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                addNeighbours(array[i][j], i ,j);
            }
        }
    }

    private Sudoku(Sudoku s, int x, int y, String value) {
        array = new SudokuElement[GRID_SIZE][GRID_SIZE];
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                String val = s.getElement(i,j).getValue();
                SudokuElement elem = new SudokuElement(val, ALPHABET);
                array[i][j] = elem;
            }
        }
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                addNeighbours(array[i][j], i ,j);
            }
        }

        array[x][y].setValue(value);
    }

    private void addNeighbours(SudokuElement element, int x, int y){
        for (int i = 0; i < GRID_SIZE; i++) {
            if(i==y) continue;
            element.getNeighbours().add(array[x][i]);
        }
        for (int i = 0; i < GRID_SIZE; i++) {
            if(i==x) continue;
            element.getNeighbours().add(array[i][y]);
        }
        int xzone = getZone(x);
        int yzone = getZone(y);

        for (int i = 0; i < BLOCK_SIZE; i++) {
            for (int j = 0; j < BLOCK_SIZE; j++) {
                int k = xzone*BLOCK_SIZE+i;
                int l = yzone*BLOCK_SIZE+j;
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
                        for (int i = 0; i < GRID_SIZE; i++) {
                            for (int j = 0; j < GRID_SIZE; j++) {
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
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
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
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
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

    int getZone(int value){
        if(value<0 || value>=GRID_SIZE) throw new IllegalStateException("Incorrect value: "+value);
        for(int i = 0;i<BLOCK_SIZE;i++){
            int min = i * BLOCK_SIZE;
            int max = min + BLOCK_SIZE;
            if(value>= min && value<max) return i;
        }
        throw new IllegalStateException("");
    }

    private boolean finalCheck(){
        Set<String> temp = new HashSet<>(GRID_SIZE);

        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                temp.add(array[i][j].getValue());
            }
            if(temp.size() != GRID_SIZE) return false;
            temp.removeAll(ALPHABET);
            if(temp.size() > 0 ) return false;
        }

        temp.clear();

        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                temp.add(array[j][i].getValue());
            }
            if(temp.size() != GRID_SIZE) return false;
            temp.removeAll(ALPHABET);
            if(temp.size() > 0 ) return false;
        }

        temp.clear();

        for (int i = 0; i < BLOCK_SIZE; i++) {
            for (int j = 0; j < BLOCK_SIZE; j++) {
                for (int k = 0; k < BLOCK_SIZE; k++) {
                    for (int l = 0; l < BLOCK_SIZE; l++) {
                        temp.add(array[i*BLOCK_SIZE+k][j*BLOCK_SIZE+l].getValue());
                    }
                }
                if(temp.size() != GRID_SIZE) return false;
                temp.removeAll(ALPHABET);
                if(temp.size() > 0 ) return false;
            }
        }

        return true;
    }


    public void print(int iteration){
        System.out.println("Iteration: "+iteration);
        int counter = 0;
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
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
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
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
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
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
