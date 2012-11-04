package to.joe.bungee;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {
    private static final Pattern ipattern = Pattern.compile("(?<=(^|[(\\p{Space}|\\p{Punct})]))((1?[0-9]{1,2}|2[0-4][0-9]|25[0-5])\\.){3}(1?[0-9]{1,2}|2[0-4][0-9]|25[0-5])(?=([(\\p{Space}|\\p{Punct})]|$))");

    public static String combineSplit(int startIndex, String[] string, String seperator) {
        final StringBuilder builder = new StringBuilder();
        for (int i = startIndex; i < string.length; i++) {
            builder.append(string[i]);
            builder.append(seperator);
        }
        builder.deleteCharAt(builder.length() - seperator.length());
        return builder.toString();
    }

    public static boolean isIP(String string) {
        final Matcher matcher = Util.ipattern.matcher(string);
        if (matcher.find()) {
            return true;
        }
        return false;
    }
}
