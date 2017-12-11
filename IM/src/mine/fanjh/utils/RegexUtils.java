package mine.fanjh.utils;

public class RegexUtils {

	public static boolean checkNumberAndChar(String text) {
		String regex = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{8,16}$";
		return text.matches(regex);
	}
	
}
