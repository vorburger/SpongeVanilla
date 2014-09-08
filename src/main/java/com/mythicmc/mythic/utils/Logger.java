package com.mythicmc.mythic.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
	public static boolean showThread = true;
	private static SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    public static void info(String format, Object...objects) {
        logf("info", format, objects);
    }

    public static void warn(String format, Object...objects) {
        logf("warn", format, objects);
    }

    public static void error(String format, Object...objects) {
        logf("error", format, objects);
    }
    
    public static void debug(String format, Object...objects) {
    	logf("debug", format, objects);
    }
    
    public static void logf(String level, String format, Object...objects) {
    	String tName = "Mythic Main Thread";
    	if(showThread)
    		 tName = Thread.currentThread().getName();

    	System.out.printf(String.format("[%s] [%s/%s] %s\n",sdf.format(new Date(System.currentTimeMillis())),tName, level.toUpperCase(), format), objects);
    }
}