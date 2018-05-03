package de.dieklaut.camtool;

import java.util.Calendar;

public class Logger {

	private static boolean printStackTraces = false;
	private static Level discardIfBelow = Level.TRACE;
	
	public static Level getDiscardIfBelow() {
		return discardIfBelow;
	}

	public static void setDiscardIfBelow(Level discardIfBelow) {
		Logger.discardIfBelow = discardIfBelow;
	}

	public enum Level {
		TRACE, DEBUG, INFO, WARNING, ERROR
	}
	
	public static void log(String message, Level level) {
		if (level.compareTo(discardIfBelow) < 0) {
			return;
		}
		System.out.println(Calendar.getInstance().getTime().toInstant() + " - " + level + " - " + message);
	}

	public static void log(String message, Exception e) {
		log(message, e, Level.ERROR);
	}

	public static void log(String message, Throwable e, Level level) {
		if (!printStackTraces) {
			message += ": " + e.getClass().getName() + ", " + e.getMessage();
		}
		log(message, level);
		if (printStackTraces) {
			e.printStackTrace(System.err);
		}
	}
}