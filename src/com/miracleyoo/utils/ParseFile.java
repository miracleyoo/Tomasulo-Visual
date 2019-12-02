/**
 * Parse a *.s File object and put the data and code in separate lists.
 * At last, return a map which contain both lists.
 * */
package com.miracleyoo.utils;

import com.miracleyoo.Logic.*;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParseFile {
    public static Map< String, List<Object[]>> parseFile(File selectedFile) throws IOException {
        // Wrap the file to a BufferedReader
        InputStream selectedFileStream = new FileInputStream(selectedFile);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(selectedFileStream));

        // Initialize containers to hold data
        List<Object[]> dataList = new ArrayList<>(); // List to hold data
        List<Object[]> textList = new ArrayList<>(); // List to hold text
        Map< String, List<Object[]>> listFlagMap = new HashMap<>(); // Map to wrap lists above
        Map< String, Integer> listCounter = new HashMap<>(); // Counters for data and code

        listFlagMap.put("dataList", dataList);
        listFlagMap.put("textList", textList);
        listCounter.put("dataList", 0);
        listCounter.put("textList", -4);
        String listFlag = "dataList"; // A flag which denote which part of file is under parsing

        String str;
        while((str = bufferedReader.readLine()) != null) {
            if(!str.startsWith(";") && !str.isEmpty()){
                if(str.strip().equals(".data")){
                    listFlag = "dataList";
                }
                else if(str.strip().equals(".text")){
                    listFlag = "textList";
                }
                else{
                    if(listFlag.equals("textList")){
                        listCounter.put(listFlag, listCounter.get(listFlag) + 4);
                        MainLogic.InstructionFullList.add(str.strip());
                    }
                    else{
                        //////////////////////////////////////////////////////////////////////
                        //////////////////        TODO       /////////////////////////////////
                        //////////////////////////////////////////////////////////////////////

                        //   Write a data parser here and send the parsed result to MainLogic
                        //   and store it in a map MainLogic.dataMap
                        //   Need to read all *.s files to make sure they all work

                        //////////////////////////////////////////////////////////////////////
                        //////////////////       END TODO       //////////////////////////////
                        //////////////////////////////////////////////////////////////////////
                    }
                    listFlagMap.get(listFlag).add(new Object[]{String.format("%04X", listCounter.get(listFlag)), str.strip()});
                }
            }
        }
        return listFlagMap;
    }
}
