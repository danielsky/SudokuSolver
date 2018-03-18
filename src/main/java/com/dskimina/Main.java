package com.dskimina;

import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        Sudoku sudoku = new Sudoku();
        try(InputStream is = getInputFile(args)){
            List<String> lines = IOUtils.readLines(is, "UTF8");
            for (int i = 0; i < 9; i++) {
                String[] tab = lines.get(i).split(" ");
                if(tab.length != 9) throw new IllegalStateException("Length must be equal to 9. Current: "+tab.length);
                for (int j = 0; j < 9; j++) {
                    if(!"x".equals(tab[j])) {
                        sudoku.getElement(i, j).setValue(tab[j]);
                    }
                }
            }
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
        return Main.class.getResourceAsStream("/init.dat");
        //return Main.class.getResourceAsStream("/input_very_hard.dat");
    }
}
