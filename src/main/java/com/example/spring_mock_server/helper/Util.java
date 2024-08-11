package com.example.spring_mock_server.helper;

public class Util {
    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            Double.parseDouble(strNum);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }
}
