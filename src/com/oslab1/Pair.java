package com.oslab1;





public class Pair {

    private static String symbol;
    private static int address;



    public Pair(String symbol, int address){
        this.symbol = symbol;
        this.address = address;
    }


    // getters / setters
    public static String getSymbol(){
        return symbol;
    }

    public static int getAddress(){
        return address;
    }

}
