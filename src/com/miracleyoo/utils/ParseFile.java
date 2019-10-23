package com.miracleyoo.utils;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParseFile {
    public Map< String, List<Object[]>> parseFile(File selectedFile) throws IOException {
        InputStream selectedFileStream = new FileInputStream(selectedFile);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(selectedFileStream));
        List<Object[]> dataList = new ArrayList<>();
        List<Object[]> textList = new ArrayList<>();

        Map< String, List<Object[]>> listFlagMap = new HashMap< String, List<Object[]>>();
        Map< String, Integer> listCounter = new HashMap<String, Integer>();
        listFlagMap.put("dataList", dataList);
        listFlagMap.put("textList", textList);
        listCounter.put("dataList", 0);
        listCounter.put("textList", 0);
        String listFlag = "dataList";

        String str = null;
        while((str = bufferedReader.readLine()) != null) {
            if(!str.startsWith(";") && !str.isEmpty()){
                if(str.strip().equals(".data")){
                    listFlag = "dataList";
                }
                else if(str.strip().equals(".text")){
                    listFlag = "textList";
                }
                else{
                    listFlagMap.get(listFlag).add(new Object[]{Integer.toHexString(listCounter.get(listFlag)), str});
                }
            }
            System.out.println(str);
        }
        return listFlagMap;
    }
}
