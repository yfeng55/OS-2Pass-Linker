package com.oslab1;
import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Arrays;
import java.util.Hashtable;

public class Main {

    private static final int TARGET_MACHINE_MEMORY = 600;

    private static ArrayList<PairsList> pairslists = null;
    private static Hashtable<String, Integer> symbol_table = null;





    // main
    public static void main(String[] args) {

        //scan input file and store in pairslists
        File file = new File("test_input.txt");
        pairslists = readInputFromFile(file);


        // (1) FIRST PASS:
        symbol_table = produceSymbolTable(pairslists);
        System.out.println(symbol_table.toString());

        updateBaseAddresses(pairslists);
        for(int i=0; i<pairslists.size(); i++){
            System.out.println(pairslists.get(i).getBaseAddress());
        }


        System.out.println("-------------- SECOND PASS ---------------");

        // (2) SECOND PASS:
        produceMemoryMap(pairslists);


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
                //System.out.println(Arrays.toString(input_array));

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


    // return a symbol table for the given ParisLists
    private static Hashtable produceSymbolTable(ArrayList<PairsList> pairslists){

        Hashtable<String, Integer> symbols = new Hashtable<>();

        //keep track of which list# the symbol is defined in
        Hashtable<String, Integer> defLocation = new Hashtable<>();



        //add symbols from DefinitionLists to the symbol table
        for(int i=0; i<pairslists.size(); i+=3) {


            // add symbols from the DefinitionList to the symbol table
            ArrayList<Pair> definitionlist = pairslists.get(i).getPairs();
            for(int j=0; j<definitionlist.size(); j++){
                symbols.put(definitionlist.get(j).getSymbol(), definitionlist.get(j).getAddress());
                defLocation.put(definitionlist.get(j).getSymbol(), i);

            }

        }
        //System.out.println(defLocation.toString());



        //convert relative addresses to absolute addresses in the symbol table
        // (iterate through the UseLists)
        for(int i=1; i<pairslists.size(); i+=3){

            String refSymbol;

            ArrayList<Pair> uselist = pairslists.get(i).getPairs();
            for(int j=0; j<uselist.size(); j++){
                refSymbol = uselist.get(j).getSymbol();

                //System.out.print(" " + refSymbol + " ");

                //if the current list comes before the definition location of the symbol
                if(i < defLocation.get(refSymbol)){
                    symbols.put(refSymbol, symbols.get(refSymbol) + pairslists.get(i + 1).getCount());
                }

                //System.out.println(symbols.toString());
            }

        }


        return symbols;
    }


    // update base addresses for all PairsLists
    private static void updateBaseAddresses(ArrayList<PairsList> pairslists){

        for(int i=3; i<pairslists.size(); i+=3) {
            int newbaseaddress = pairslists.get(i-1).getCount() + pairslists.get(i-3).getBaseAddress();

            pairslists.get(i).setBaseAddress(newbaseaddress);
            pairslists.get(i+1).setBaseAddress(newbaseaddress);
            pairslists.get(i+2).setBaseAddress(newbaseaddress);
        }

    }


    // produce memory-map by relocating relative addresses and resolving external references
    private static void produceMemoryMap(ArrayList<PairsList> pairslists){


        for(int i=2; i<pairslists.size(); i+=3) {

            ArrayList<Pair> programtextlist = pairslists.get(i).getPairs();

            for(int j=0; j<programtextlist.size(); j++){

                String symbol = programtextlist.get(j).getSymbol();
                int oldaddress = programtextlist.get(j).getAddress();
                int newaddress;


                switch(symbol){

                    case "R":
                        newaddress = oldaddress + pairslists.get(i).getBaseAddress();
                        break;

                    case "I":
                        newaddress = oldaddress;
                        break;

                    case "A":
                        if(Validation.extractAddress(oldaddress) > 600){
                            newaddress = 0;
                            System.out.println("ERROR: address exceeds size of program's memory");
                        }else{
                            newaddress = oldaddress;
                        }
                        break;

                    case "E":
                        newaddress = 4;
                        break;

                    default:
                        newaddress = 0;
                }



                System.out.print(symbol + " " + oldaddress + "  -->     ");
                System.out.println(newaddress);

            }

        }


    }










}




















