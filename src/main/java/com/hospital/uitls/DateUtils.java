package com.hospital.uitls;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
    public static String date2String(Date date){
        SimpleDateFormat sdf=new SimpleDateFormat("YYYY-MM-dd");
        return sdf.format(date);
    }
}
