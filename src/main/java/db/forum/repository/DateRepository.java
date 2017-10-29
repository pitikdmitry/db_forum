package db.forum.repository;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateRepository {

    public String getCurrentDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date date = new Date();
        String resDate = dateFormat.format(date);
        return resDate;
    }

    public String changeDateFormat(String str) {
        if (str == null) {
            return null;
        }

        if (str.charAt(10) == 'T') {
            StringBuilder strBuild = new StringBuilder(str);
            strBuild.setCharAt(10, 'T');
            str = strBuild.toString();
        }
        str = addMilliseconds(str);
        str = changeTime(str);
        return str;
    }

    String changeTime(String str) {
        if(str.length() == 26) {
            String hours_str = str.substring(11, 13);
            Integer hours = Integer.parseInt(hours_str);
            hours -= 3;
            hours_str = String.valueOf(hours);
            String new_str = str.substring(0,10);
            new_str += hours_str;
            new_str += str.substring(14, str.length());
            return new_str;
        }
        return null;
    }

    String addMilliseconds(String str) {
        if(str == null) {
            return null;
        }
        System.out.println(str.length() + str);
        if(str.length() == 23) {
            str += str.substring(19, 20);
        }
        else if(str.length() == 24) {
            str += str.substring(19, 21);
        }
        else if(str.length() == 25) {
            str += str.substring(19, 22);
        }
        else if(str.length() == 26) {
            str += str.substring(19, 23);
        }
        else if(str.length() == 27) {
            str += str.substring(19, 23);
        }
        else if(str.length() == 28) {
            str += str.substring(19, 23);
        }
        else if(str.length() == 29) {
            str += str.substring(19, 23);
        }
        str += 'Z';
        return str;
    }
}
