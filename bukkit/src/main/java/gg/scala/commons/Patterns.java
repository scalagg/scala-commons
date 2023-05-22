package gg.scala.commons;

import org.jetbrains.annotations.NotNull;

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

    public static @NotNull Matcher matchPlaceholderSyntax(@NotNull String expression) {
        return PATTERN.matcher(expression);
    }
}
