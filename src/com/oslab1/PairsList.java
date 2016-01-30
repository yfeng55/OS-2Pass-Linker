package com.oslab1;


import java.lang.reflect.Array;
import java.util.ArrayList;

public class PairsList {

    private int count;
    private String symbol;
    private ArrayList<Pair> relative_addresses;




    public PairsList(int count){
        this.count = count;
        this.relative_addresses = new ArrayList<Pair>();
    }




    public void addPair(Pair newpair){
        this.relative_addresses.add(newpair);
    }


    public boolean containsSymbol(String symbol){
        for(int i=0; i<this.count; i++){

            String pairSymbol = this.relative_addresses.get(i).getSymbol();
            if(symbol.equals(pairSymbol)){
                return true;
            }

        }

        return false;
    }



    // Getters / Setters
    public int getCount(){
        return this.count;
    }


    public ArrayList getPairs(){
        return relative_addresses;
    }


}













