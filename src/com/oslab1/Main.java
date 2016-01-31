package com.oslab1;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Hashtable;



public class Main {

    private static final int TARGET_MACHINE_MEMORY = 600;
    private static final String INPUT_FILE = "test_input4.txt";

    private static ArrayList<PairsList> pairslists = null;
    private static Hashtable<String, Integer> symbol_table = null;
    private static ArrayList<Hashtable<Integer, Integer>> memory_maps = null;




    // main
    public static void main(String[] args) {

        //scan input file and store in pairslists
        File file = new File(INPUT_FILE);
        pairslists = readInputFromFile(file);


        // (1) FIRST PASS:
        System.out.println("\n-------------- FIRST PASS ---------------");
        symbol_table = produceSymbolTable(pairslists);
        System.out.println(symbol_table.toString());

        updateBaseAddresses(pairslists);
        for(int i=0; i<pairslists.size(); i++){
            System.out.print(pairslists.get(i).getBaseAddress() + ", ");
        }


        // (2) SECOND PASS:
        System.out.println("\n\n-------------- SECOND PASS ---------------");
        memory_maps = produceMemoryMap(pairslists);
        System.out.println(memory_maps.toString());


        // (3) PRINT OUTPUT:
        System.out.println("\n\n-------------- PRINT OUTPUT ---------------");
        for(int i=2; i<pairslists.size(); i+=3){
            for(Pair pair:pairslists.get(i).getPairs()){
                System.out.print(pair.getSymbol() + " ");
                System.out.print(pair.getAddress() + " -->  ");

                int mapped_val = memory_maps.get(i/3).get(pair.getAddress());

//                mapped_val
                System.out.println(mapped_val);
            }

            System.out.println();
        }

    }






    // read input from file
    private static ArrayList readInputFromFile(File file){

        ArrayList<PairsList> lists = new ArrayList<>();


        try {
            Scanner input = new Scanner(file);
            String[] input_array;

            //add all PairsLists to the list
            while(input.hasNextLine()){

                //remove leading/trailing spaces
                String line = input.nextLine().trim();

                if(!line.isEmpty()){
                    input_array = line.split(" +");
                    System.out.println(Arrays.toString(input_array));

                    //create new PairsList
                    PairsList pairslist = new PairsList(Integer.parseInt(input_array[0]));


                    //for each item in the input_array, create a pair and store in input_pairslist
                    for(int i=1; i<input_array.length; i+=2){
                        Pair newpair = new Pair(input_array[i], Integer.parseInt(input_array[i+1]));
                        pairslist.addPair(newpair);
                    }

                    lists.add(pairslist);
                }

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

                //check if symbol already has been defined
                if(symbols.containsKey(definitionlist.get(j).getSymbol()) == false){
                    symbols.put(definitionlist.get(j).getSymbol(), definitionlist.get(j).getAddress());
                    defLocation.put(definitionlist.get(j).getSymbol(), i);
                }else{
                    System.out.print("  Error: This variable is multiply defined; first value used.");
                }


            }

        }
        System.out.println(defLocation.toString());



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
    private static ArrayList<Hashtable<Integer, Integer>> produceMemoryMap(ArrayList<PairsList> pairslists){


        ArrayList<Hashtable<Integer, Integer>> memorymaps = new ArrayList<>();

        // for each programtext list in the program, create a memorymap hashtable
        for(int i=2; i<pairslists.size(); i+=3) {

            Hashtable<Integer, Integer> memorymap = new Hashtable<>();

            ArrayList<Pair> programtextlist = pairslists.get(i).getPairs();
            ArrayList<Pair> uselist = pairslists.get(i - 1).getPairs();



            //for each pair in the programtextlist
            for(int j=0; j<programtextlist.size(); j++){

                String symbol = programtextlist.get(j).getSymbol();
                int oldaddress = programtextlist.get(j).getAddress();
                int newaddress;


                switch(symbol){

                    case "R":
                        newaddress = oldaddress + pairslists.get(i).getBaseAddress();
                        memorymap.put(oldaddress, newaddress);
                        break;

                    case "I":
                        newaddress = oldaddress;
                        memorymap.put(oldaddress, newaddress);
                        break;

                    case "A":
                        if(Validation.extractAddress(oldaddress) > 600){
                            newaddress = 0;
                            System.out.println("ERROR: address exceeds size of program's memory");
                        }else{
                            newaddress = oldaddress;
                        }
                        memorymap.put(oldaddress, newaddress);
                        break;

                    case "E":
                        memorymap = resolveExternalRef(programtextlist, uselist, memorymap);
                        break;

                    default:
                        newaddress = 0;
                }


            }
            memorymaps.add(memorymap);

        }


        return memorymaps;
    }




    // follow a chain of references to get the absolute address of an external reference
    private static Hashtable resolveExternalRef(ArrayList<Pair> programtextlist, ArrayList<Pair> uselist, Hashtable memorymap){

        //for each pair in the uselist
        for(Pair pair:uselist){

            int location = Validation.extractAddress(pair.getAddress());

            //refer to the location of pair and map addresses in the programtextlist
            while(location != 777){

                Pair nextref = programtextlist.get(location);

                //map the address of nextref
                int oldaddress = nextref.getAddress();
//                System.out.print(oldaddress + "   ");

                int newaddress = Validation.extractOpcode(oldaddress)*1000 + symbol_table.get(pair.getSymbol());
//                System.out.print(newaddress + "   ");


                memorymap.put(oldaddress, newaddress);


                //change the location
                location = Validation.extractAddress(nextref.getAddress());
            }

        }

        return memorymap;
    }








}




















