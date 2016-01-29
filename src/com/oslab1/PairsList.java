package com.oslab1;


import java.lang.reflect.Array;
import java.util.ArrayList;

public class PairsList {

    private static int count;
    private static String symbol;
    private static ArrayList relative_addresses;


    public PairsList(String symbol){
        this.count = 0;
        this.symbol = symbol;

        this.relative_addresses = new ArrayList<Integer>();
    }






    // Getters / Setters

    public static int getCount(){
        return count;
    }


    public static ArrayList getRelativeAddress(){
        return relative_addresses;
    }


}
