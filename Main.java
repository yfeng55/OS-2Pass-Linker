
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;


public class Main {

    private static final int TARGET_MACHINE_MEMORY = 600;
    private static final int MAX_SYMBOL_LENGTH = 8;
//    private static final String INPUT_FILE = "test_input1.txt";

    private static ArrayList<PairsList> pairslists = null;
    private static Hashtable<String, Integer> symbol_table = null;

    private static ArrayList<Integer> memorymap = null;
    private static int size; //track the size of memorymap

    //error tracking
    private static Hashtable<String, String> symbol_errors = new Hashtable<>();
    private static Hashtable<Integer, String> memorymap_errors = new Hashtable<>();



    // main
    public static void main(String[] args) {

        //scan input file and store in pairslists
        //File file = new File(args[0]);
        pairslists = readInputFromFile();


        // (1) FIRST PASS: //
        updateBaseAddresses(pairslists);

        symbol_table = produceSymbolTable(pairslists);
        System.out.println("\nSymbol Table");

        ArrayList<String> tableKeys = new ArrayList(symbol_table.keySet());
        Collections.sort(tableKeys);

        for(String key:tableKeys){
            System.out.print(key + "=" + symbol_table.get(key));

            if(symbol_errors.containsKey(key)){
                System.out.print("  " + symbol_errors.get(key));
            }

            System.out.print("\n");
        }


        // (2) SECOND PASS: //

        //initialize memorymap
        memorymap = new ArrayList<>();
        size=0;
        for(int i=2; i<pairslists.size(); i+=3){
            for(int j=0; j < pairslists.get(i).getPairs().size(); j++){
                memorymap.add(size, 0);
                size++;
            }
        }

        produceMemoryMap(pairslists);


        // print output
        System.out.println("\nMemory Map");
        for(int i=0; i<size; i++){
            System.out.format("%-4s%4s", i + ":", memorymap.get(i));

            if(memorymap_errors.containsKey(i)){
                System.out.print("  " + memorymap_errors.get(i));
            }
            System.out.print("\n");
        }



        // (3) PRINT WARNINGS: //

        //iterate through all definition lists
        System.out.print("\n");

        for(String key:tableKeys) {

            boolean isUsed = false;
            int moduleNum=0;


            for (int i = 0; i < pairslists.size(); i += 3) {

                PairsList definitionlist = pairslists.get(i);
                PairsList uselist = pairslists.get(i+1);

                if(uselist.containsSymbol(key) == true){
                    isUsed = true;
                }
                if(definitionlist.containsSymbol(key)){
                    moduleNum = i;
                }
            }



            if(isUsed == false){
                System.out.println("Warning: " + key + " was defined in module " + (moduleNum / 3) + " but never used.");
            }

        }





    }



    // read input from file
    private static ArrayList readInputFromFile(){

        ArrayList<PairsList> lists = new ArrayList<>();

        //try {
            Scanner input = new Scanner(System.in);


            //add all PairsLists to the list
            while(input.hasNext()){

                ArrayList<String> input_array = new ArrayList();

               //get the first character and use this as the # of items to add
                int listSize = input.nextInt();

//                System.out.print("LISTSIZE:  ");
//                System.out.println(listSize);

                for(int i=0; i<listSize*2; i++){
                    input_array.add(input.next());
                }

                PairsList pairslist = new PairsList(listSize);

                //for each pair of items in input_array, create a pair and store in pairslist
                for(int i=0; i<listSize*2; i+=2){

                    //ERROR CHECK: check that symbol doesn't exceed MAX_SYMBOL_LENGTH
                    if(input_array.get(i).length() > MAX_SYMBOL_LENGTH){
                        System.out.println("Error: " + input_array.get(i) + " exceeds the maximum symbol size");
                    }

                    Pair newpair = new Pair(input_array.get(i), Integer.parseInt(input_array.get(i+1)));
                    pairslist.addPair(newpair);
                }

                lists.add(pairslist);
            }


            input.close();
        //} catch (FileNotFoundException e) {
        //    e.printStackTrace();
        //}


        //return an ArrayList of PairLists
        return lists;
    }


    // return a symbol table for the given ParisLists
    private static Hashtable produceSymbolTable(ArrayList<PairsList> pairslists){

        Hashtable<String, Integer> symbols = new Hashtable<>();



        //for each DefinitionList
        for(int i=0; i<pairslists.size(); i+=3) {


            // add symbols from the DefinitionList to the symbol table
            ArrayList<Pair> definitionlist = pairslists.get(i).getPairs();
            ArrayList<Pair> programtextlist = pairslists.get(i+2).getPairs();

            for(int j=0; j<definitionlist.size(); j++){

                //check if symbol already has been defined
                if(symbols.containsKey(definitionlist.get(j).getSymbol()) == false){

                    //ERROR CHECK(INPUT6): check that the address of the definition doesn't exceed module size

                    if(definitionlist.get(j).getAddress() < programtextlist.size()) {
                        symbols.put(definitionlist.get(j).getSymbol(), definitionlist.get(j).getAddress() + pairslists.get(i).getBaseAddress());
                    }else{
                        //System.out.println("Error: The value of " + definitionlist.get(j).getSymbol() + " is outside module " + (i/3) + "; zero (relative) used.");
                        symbol_errors.put(definitionlist.get(j).getSymbol(), "Error: The value of " + definitionlist.get(j).getSymbol() + " is outside module " + (i/3) + "; zero (relative) used.");
                        symbols.put(definitionlist.get(j).getSymbol(), 0 + pairslists.get(i).getBaseAddress());
                    }

                }else{
                    //System.out.println("Error: This variable is multiply defined; first value used.");
                    symbol_errors.put(definitionlist.get(j).getSymbol(), "Error: This variable is multiply defined; first value used.");
                }


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

        // for each programtext list in the program, create a memorymap hashtable
        for(int i=2; i<pairslists.size(); i+=3) {

            ArrayList<Pair> programtextlist = pairslists.get(i).getPairs();
            int baseAddress = pairslists.get(i).getBaseAddress();

            //for each pair in the programtextlist
            for(int j=0; j<programtextlist.size(); j++){

                String symbol = programtextlist.get(j).getSymbol();
                int oldaddress = programtextlist.get(j).getAddress();
                int newaddress;


                switch(symbol){

                    case "R":
                        newaddress = oldaddress + baseAddress;
                        memorymap.add(j + baseAddress, newaddress);
                        break;

                    case "I":
                        newaddress = oldaddress;
                        memorymap.add(j + baseAddress, newaddress);
                        break;

                    case "A":
                        if(Validation.extractAddress(oldaddress) > TARGET_MACHINE_MEMORY){
                            newaddress = 0;
                            System.out.println("ERROR: address exceeds size of program's memory");
                        }else{
                            newaddress = oldaddress;
                        }
                        memorymap.add(j + baseAddress, newaddress);
                        break;

                }

            }


        }


        // RESOLVE EXTERNAL REFERENCES AFTER MAPPING ALL OF THE OTHER TYPES
        // for each programtextlist
        for(int i=2; i<pairslists.size(); i+=3) {

            ArrayList<Pair> programtextlist = pairslists.get(i).getPairs();
            ArrayList<Pair> uselist = pairslists.get(i - 1).getPairs();

            int baseAddress = pairslists.get(i).getBaseAddress();


            //for each pair in the uselist, follow a chain to resolve external references
            for(Pair use:uselist){

                resolveExternalRef(programtextlist, use, baseAddress);

            }

        }



        // ERROR CHECK (INPUT8): check the programtext for 'E' references that are not in the uselist
        // (since all pairs are initialized to 0, we look through all programtext lists for
        // E references with an address of 0)
        for(int i=2; i<pairslists.size(); i+=3) {

            ArrayList<Pair> programtextlist = pairslists.get(i).getPairs();
            int baseAddress = pairslists.get(i).getBaseAddress();

            //for each pair in the programtextlist
            for(int j=0; j<programtextlist.size(); j++){

                int oldaddress = programtextlist.get(j).getAddress();

                if(memorymap.get(j + baseAddress) == 0){
//                    System.out.println("Error: E type address not on use chain; treated as I type.");
                    memorymap_errors.put(j + baseAddress, "Error: E type address not on use chain; treated as I type.");
                    memorymap.set(j + baseAddress, oldaddress);
                }
            }
        }





    } //end produceMemoryMap()





    // follow a chain of references to get the absolute address of an external reference
    private static void resolveExternalRef(ArrayList<Pair> programtextlist, Pair use, int baseAddress){

            int location = Validation.extractAddress(use.getAddress());

            while(location != 777){

                // ERROR CHECK (INPUT7): check that the next location is within the bounds of the module
                if(location < programtextlist.size()) {

                    Pair nextref = programtextlist.get(location);

                    //map the address of nextref
                    int oldaddress = nextref.getAddress();
                    int newaddress;


                    // ERROR CHECK (INPUT5): check that the use is defined in the symbol table
                    if(symbol_table.containsKey(use.getSymbol())) {
                        newaddress = Validation.extractOpcode(oldaddress) * 1000 + symbol_table.get(use.getSymbol());
                        //System.out.print(newaddress + "   ");
                    }else{
                        //System.out.println("Error: " + use.getSymbol() + " is not defined; zero used.");
                        memorymap_errors.put(location + baseAddress, "Error: " + use.getSymbol() + " is not defined; zero used.");
                        newaddress = Validation.extractOpcode(oldaddress) * 1000 + 0;
                    }


                    // ERROR CHECK (INPUT9): print an error if symbol is an I type
                    if(nextref.getSymbol().equals("I")){
//                        System.out.println("Error: I type address on use chain; treated as E type.");
                        memorymap_errors.put(location + baseAddress, "Error: I type address on use chain; treated as E type.");
                    }
                    // ERROR CHECK (INPUT9): print an error if symbol is an A type
                    if(nextref.getSymbol().equals("A")){
//                        System.out.println("Error: A type address on use chain; treated as E type.");
                        memorymap_errors.put(location + baseAddress, "Error: A type address on use chain; treated as E type.");
                    }


                    memorymap.set(location + baseAddress, newaddress);

                    //change the location
                    location = Validation.extractAddress(nextref.getAddress());

                }else{
//                    System.out.println("Error: Pointer in use chain exceeds module size; chain terminated.");
                    memorymap_errors.put(baseAddress, "Error: Pointer in use chain exceeds module size; chain terminated.");
                    location = 777;
                }



            }

        }

    }





























