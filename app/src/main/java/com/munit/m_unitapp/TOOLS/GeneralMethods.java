package com.munit.m_unitapp.TOOLS;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class GeneralMethods {
    //Decrepting & Encripting
    /* encrypt and decrypt a text using a simple algorithm of offsetting the letters */

    static char[] chars = {
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h',
            'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p',
            'q', 'r', 's', 't', 'u', 'v', 'w', 'x',
            'y', 'z', '0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'A', 'B', 'C', 'D',
            'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L',
            'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
            'U', 'V', 'W', 'X', 'Y', 'Z', '!', '@',
            '#', '$', '%', '^', '&', '(', ')', '+',
            '-', '*', '/', '[', ']', '{', '}', '=',
            '<', '>', '?', '_', '"', '.', ',', ' '
    };
// Caesar cipher

    public static String encrypt(String text, int offset) {
        char[] plain = text.toCharArray();

        for (int i = 0; i < plain.length; i++) {
            for (int j = 0; j < chars.length; j++) {
                if (j <= chars.length - offset) {
                    if (plain[i] == chars[j]) {
                        plain[i] = chars[j + offset];
                        break;
                    }
                } else if (plain[i] == chars[j]) {
                    plain[i] = chars[j - (chars.length - offset + 1)];
                }
            }
        }
        return String.valueOf(plain);
    }

    public static String decrypt(String cip, int offset) {
        try {
            char[] cipher = cip.toCharArray();
            for (int i = 0; i < cipher.length; i++) {
                for (int j = 0; j < chars.length; j++) {
                    if (j >= offset && cipher[i] == chars[j]) {
                        cipher[i] = chars[j - offset];
                        break;
                    }
                    if (cipher[i] == chars[j] && j < offset) {
                        cipher[i] = chars[(chars.length - offset + 1) + j];
                        break;
                    }
                }
            }
            return String.valueOf(cipher);
        } catch (Exception e) {
//           mmmmmmmmmmmmmmmmmmmmmm
        }
        return null;
    }

    public static int getWeekNumber(String sDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Calendar cal = Calendar.getInstance();
        cal.setMinimalDaysInFirstWeek(1);
        try {
            cal.setTime(sdf.parse(sDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        int number = cal.get(Calendar.WEEK_OF_YEAR);
        if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
//            number = number - 1;
        }
        return number;
    }
    public static String ChangeDateToSimpleFormat(String DateToConvert) {
        String newDate = "";
        try {
            String FOR = "dd/M/yyyy";
            DateFormat gmtFormat = new SimpleDateFormat("EEE, dd MMM, yyyy");
            SimpleDateFormat sdf = new SimpleDateFormat(FOR);
            newDate = gmtFormat.format(sdf.parse(DateToConvert));
            return newDate;
        } catch (Exception e) {

        }
        return newDate;
    }

    public String getDateParts(String sDate, String getWhat) {
        if (getWhat.equalsIgnoreCase("dd")) {
            return sDate.substring(0, sDate.indexOf("/"));
        } else if (getWhat.equalsIgnoreCase("mm")) {
            return sDate.substring(sDate.indexOf("/") + 1, sDate.lastIndexOf("/"));
        } else {
            return sDate.substring(sDate.lastIndexOf("/") + 1);
        }

    }

    public String getMonthName(int monthNumber) {
        String monthName;
        switch (monthNumber) {
            case 1:
                monthName = "Jan";
                break;
            case 2:
                monthName = "Feb";
                break;
            case 3:
                monthName = "Mar";
                break;
            case 4:
                monthName = "Apr";
                break;
            case 5:
                monthName = "May";
                break;
            case 6:
                monthName = "Jun";
                break;
            case 7:
                monthName = "Jul";
                break;
            case 8:
                monthName = "Aug";
                break;
            case 9:
                monthName = "Sep";
                break;
            case 10:
                monthName = "Oct";
                break;
            case 11:
                monthName = "Nov";
                break;
            case 12:
                monthName = "Dec";
                break;
            default:
                monthName = "Invalid month";
                break;
        }

        return monthName;
    }

    public int getDifferenceDays(Date d1, Date d2) {
        int daysdiff = 0;
        long diff = d2.getTime() - d1.getTime();
        long diffDays = diff / (24 * 60 * 60 * 1000) + 1;
        daysdiff = (int) diffDays;
        return daysdiff;
    }

    public static int getCarwashCommission(int amount) {
        int commission = 0;

        if (amount > 499) {
            commission = 130;
        } else if (amount > 399) {
            commission = 100;
        } else if (amount > 299) {
            commission = 70;
        }else if (amount > 199) {
            commission = 50;
        }else if (amount > 99) {
            commission = 30;
        } else if (amount > 49) {
            commission = 20;
        }
        return commission;

    }
}
