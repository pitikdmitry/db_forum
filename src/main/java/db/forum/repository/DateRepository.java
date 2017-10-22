package db.forum.repository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateRepository {

    public String changeDateFormat(String str) {
//        System.out.println("LENGTHHHH: " + str.length());
//        System.out.println(str);
        String OLD_FORMAT = null;
        String NEW_FORMAT = null;
        int lengt = str.length();
        String str2 = null;

        if(str.length() == 25) {
            OLD_FORMAT = "yyyy-MM-dd HH:mm:ss.SS";
            NEW_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SS'Z'";
        }
        else if(str.length() == 26) {
            OLD_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
            NEW_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
        }
        else if(str.length() == 29) {
            str2 = str.substring(0, 23);
            str2 += str.substring(26, 29);
            OLD_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
            NEW_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
            str = str2;
        }

        String oldDateString = str;
        String newDateString = null;

        SimpleDateFormat sdf = new SimpleDateFormat(OLD_FORMAT);

        Date d = null;
        int offset = 0;
        try {
            d = sdf.parse(oldDateString);
            offset = d.getTimezoneOffset();
//            d.setMinutes(d.getMinutes() + offset);
            d.setHours(d.getHours() + offset / 60);
        }
        catch (ParseException e) {
            System.out.println("[EXc format date]");
        }
        sdf.applyPattern(NEW_FORMAT);
        newDateString = sdf.format(d);
        return newDateString;
    }
}
