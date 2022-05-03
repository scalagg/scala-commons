package gg.scala.commons;

import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author GrowlyX
 * @since 5/2/2022
 */
public class Patterns {

    private static final Pattern PATTERN = Pattern.compile(
        new String(
            Base64.getDecoder()
                .decode("XCUoKFthLXpBLVowLTldKyktKSgoW2EtekEtWi0wLTldKylfKShbXiUlXSspXCU=")
        )
    );

    public static Matcher matcher(String expression)
    {
        return PATTERN.matcher(expression);
    }

    public static void main(String[] args) {
        test("%tangerine-scoreboard-display_hub-1%");
        test("§bPlayers: §f%tangerine-scoreboard-display_practice%");
    }

    public static void test(String expression)
    {
        System.out.println(expression + ": " + PATTERN.matcher(expression).find());
    }
}
