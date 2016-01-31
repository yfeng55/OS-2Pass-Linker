package com.oslab1;


public class Validation {

    public static int extractAddress(int x){
        return x % 1000;
    }


    public static int extractOpcode(int x) {
        while (x < -9 || 9 < x) x /= 10;
        return Math.abs(x);
    }


}

