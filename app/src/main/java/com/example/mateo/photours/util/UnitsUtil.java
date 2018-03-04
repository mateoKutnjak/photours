package com.example.mateo.photours.util;

import java.util.Locale;

public class UnitsUtil {

    public static String seconds2HHmmss(int seconds) {
        return String.format(Locale.getDefault(), "%02d:%02d:%02d", (seconds % 86400) / 3600, (seconds % 3600) / 60, seconds % 60);
    }
}