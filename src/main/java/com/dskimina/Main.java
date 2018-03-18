package com.dskimina;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Main {

    public static void main(String[] args) {

        Sudoku sudoku;
        try(InputStream is = getInputFile(args)){
            sudoku = new Sudoku(is);
        }catch(IOException ex){
            throw new IllegalStateException("cannot read input file", ex);
        }

        sudoku.print(1);
        try {
            long t1 = System.currentTimeMillis();
            boolean result = sudoku.solve();
            long t2 = System.currentTimeMillis();

            if(result){
                System.out.println("Success in "+(t2-t1)+" ms");
                sudoku.print(2);
            }else{
                System.err.println("Failure");
            }
        } catch (UnsolvableException e) {
            System.err.println("Cannot solve");
        }
    }

    private static InputStream getInputFile(String[] args) throws IOException{
        if(args != null && args.length == 1){
            return new FileInputStream(args[0]);
        }

        System.out.println("Reading file from classpath");
        //return Main.class.getResourceAsStream("/init.dat");
        return Main.class.getResourceAsStream("/input_very_hard.dat");
    }
}
