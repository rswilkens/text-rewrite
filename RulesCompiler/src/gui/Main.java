package gui;


import java.io.FileNotFoundException;

import java.io.FileReader;

import grammar.Lexer;
import grammar.Parser;
import java_cup.runtime.Symbol;

public class Main {

    public static void main(String[] args) throws FileNotFoundException {

        String fileName = "t1";  

        Parser parser = new Parser(new Lexer(new FileReader(fileName)));
        try {
            Symbol s = parser.parse();
            System.out.println("parsed");
            
        }       
        catch (Exception e) {
            System.err.println("\n\nerr.");
            e.printStackTrace();
        }
    }
}
