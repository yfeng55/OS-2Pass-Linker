package com.oslab1;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;


public class Main {


    public static void main(String[] args) {

        System.out.println("----- SCANNING INPUT FILE -----");
        File file = new File("test_input.txt");
        readInputFromFile(file);


    }



    // read input from file
    private static void readInputFromFile(File file){

        try {

            Scanner input = new Scanner(file);

            while(input.hasNextLine()){
                String line = input.nextLine();
                System.out.println(line);
            }
            input.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }




}















/*

SAMPLE INPUT:
----------------------------------

  1 xy 2
  1 z  4
  5 R  1004     I 5678   E 2777   R 8002    E 7002
  0
  1 z  3
  6 R  8001     E 1777   E 1001   E 3002    R 1002    A 1010
  0
  1 z  1
  2 R  5001     E 4777
  1 z  2
  1 xy 2
  3 A  8000     E 1777   E 2001



ADDRESS TYPE:
----------------------------------
Immediate, Absolute, Relative, External.

*/





















