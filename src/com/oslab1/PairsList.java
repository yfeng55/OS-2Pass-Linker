package com.oslab1;


import java.lang.reflect.Array;
import java.util.ArrayList;

public class PairsList {

    private int count;
    private int base_address;
    private ArrayList<Pair> relative_addresses;




    public PairsList(int count){
        this.count = count;
        this.base_address = 0;
        this.relative_addresses = new ArrayList<Pair>();
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

    public int getBaseAddress(){
        return this.base_address;
    }
    public void setBaseAddress(int newaddress){
        this.base_address = newaddress;
    }


    public int getCount(){
        return this.count;
    }
    public void setCount(int newcount){
        this.count = newcount;
    }


    public ArrayList getPairs(){
        return relative_addresses;
    }
    public void addPair(Pair newpair){
        this.relative_addresses.add(newpair);
    }





}













