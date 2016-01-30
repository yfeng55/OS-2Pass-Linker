package com.oslab1;


import java.lang.reflect.Array;
import java.util.ArrayList;

public class PairsList {

    private int count;
    private String symbol;
    private ArrayList relative_addresses;




    public PairsList(){
        this.count = 0;
        this.symbol = symbol;
        this.relative_addresses = new ArrayList<Integer>();
    }




    public void addPair(Pair newpair){
        this.relative_addresses.add(newpair);
        this.count++;
    }




    // Getters / Setters
    public int getCount(){
        return this.count;
    }


    public ArrayList getRelativeAddress(){
        return relative_addresses;
    }


}













