package de.dieklaut.camtool;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Logger {
	
	private static DateFormat format = new SimpleDateFormat();
	
	public enum Level {
		INFO, ERROR
	}
	
	public static void log(String message, Level level) {
		System.out.println(format.format(Calendar.getInstance().toInstant()) + " - " + level + " - " + message);
	}

	public static void log(String message, Exception e) {
		log(message, Level.ERROR);
		e.printStackTrace(System.err);
	}
}