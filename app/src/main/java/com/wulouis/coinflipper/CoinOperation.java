package com.wulouis.coinflipper;

import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;

/**
 * Created by louis on 2015/11/7.
 */
public class CoinOperation{
    public static boolean getCoinState()
    {
        Random random=new Random();
        int result=0;
        result=random.nextInt(100);
        if(result<50){
            Log.e("GetNumber", "Get Number:" + Integer.toString(result) + " And return false");
            return false;
        }
        else {
            Log.e("GetNumber","Get Number:"+Integer.toString(result)+" And return true");
            return true;
        }
    }

    public static String getCPUState(){
        String[] cpuInfos = null;
        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream("/proc/stat")), 1000);
            String load = reader.readLine();
            reader.close();
            cpuInfos = load.split(" ");
        }catch(IOException ex){
            Log.i("Warning", "IOException" + ex.toString());
            return "";
        }
        long totalCpu = 0;
        try{
            totalCpu = Long.parseLong(cpuInfos[2])
                    + Long.parseLong(cpuInfos[3]) + Long.parseLong(cpuInfos[4])
                    + Long.parseLong(cpuInfos[6]) + Long.parseLong(cpuInfos[5])
                    + Long.parseLong(cpuInfos[7]) + Long.parseLong(cpuInfos[8]);
        }catch(ArrayIndexOutOfBoundsException e){
            Log.i("Warning", "ArrayIndexOutOfBoundsException" + e.toString());
            return "";
        }
        return Integer.toString((int)totalCpu*100)+"%";
    }

    public static int getProbabilityA(int a, int b) {
        return a/(a+b);
    }
}
