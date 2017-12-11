package mine.fanjh.utils;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Loggers {
	public static final String TAG = "default";
	public static void log(String text) {
		Logger logger = Logger.getLogger(TAG);
        logger.log(Level.INFO, null == text?"":text);
	}
}
