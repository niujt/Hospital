package com.hospital.uitls;

import java.util.Iterator;
import java.util.Map;

public class DrugsUtils {
    public static String vaild(Map map){
        Iterator entries = map.entrySet().iterator();
        String ids="";
        while (entries.hasNext()) {
            Map.Entry entry = (Map.Entry) entries.next();
            String key = (String)entry.getKey();
            String value = (String)entry.getValue();
            if(key.split("_")[0].equals("drugid")){
                ids+=value+",";
            }
        }
           return ids.substring(0,ids.length()-1);
        }
}
