package com.oslab1;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Arrays;


public class Main {

    private static PairsList definitionList;
    private static PairsList useList;
    private static PairsList programtextList;



    public static void main(String[] args) {


        System.out.println("----- SCANNING INPUT FILE -----");
        File file = new File("test_input.txt");
        readInputFromFile(file);



    }


    // read input from file
    private static ArrayList readInputFromFile(File file){

        ArrayList<PairsList> lists = new ArrayList<>();


        try {
            Scanner input = new Scanner(file);
            String[] input_array;

            //add all PairsLists to the list
            while(input.hasNextLine()){

                String line = input.nextLine();
                input_array = line.split(" +");
                System.out.println(Arrays.toString(input_array));

                //create new PairsList
                PairsList pairslist = new PairsList(Integer.parseInt(input_array[1]));


                //for each item in the input_array, create a pair and store in input_pairslist
                for(int i=2; i<input_array.length; i+=2){
                    Pair newpair = new Pair(input_array[i], Integer.parseInt(input_array[i+1]));
                    pairslist.addPair(newpair);
                }

                lists.add(pairslist);
            }


            input.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        //return an ArrayList of PairLists
        return lists;
    }




}




















