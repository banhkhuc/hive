package fpt.edu.capstone.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeUtils {
    private static final String DATETIME_PATTERN = "yyyy-MM-dd HH:mm";

    public static String convertLocalDateTimeToString(String s){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATETIME_PATTERN);
        LocalDateTime dateTime = LocalDateTime.parse(s, formatter);
        return dateTime.toString();
    }

    public static boolean checkExpireTime(LocalDateTime expiredDate) {
        LocalDateTime now = LocalDateTime.now();
        int check = now.compareTo(expiredDate);
        if (check >= 0) {
            return true;
        }
        return false;
    }
}
