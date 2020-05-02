package githubsearch.util;

public class Log {
    public static void d(String tag, String msg) {
        System.out.println("DEBUG: " + tag + ":  " + msg);
    }
    public static void i(String tag, String msg) {
        System.out.println("INFO: " + tag + ":  " + msg);
    }
}
