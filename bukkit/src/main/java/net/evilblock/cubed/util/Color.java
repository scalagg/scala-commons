package net.evilblock.cubed.util;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Color {
    private static final Pattern HEX_PATTERN = Pattern.compile("#[a-fA-F0-9]{6}");

	public static String translate(String message) {
		if (message == null) {
			return null;
		}

		Matcher matcher = HEX_PATTERN.matcher(message);
		while (matcher.find()) {
			String hexCode = message.substring(matcher.start(), matcher.end());
			StringBuilder builder = new StringBuilder();
			for (char c : hexCode.replace('#', 'x').toCharArray()) {
				builder.append('&').append(c);
			}
			message = message.replace(hexCode, builder.toString());
			matcher = HEX_PATTERN.matcher(message);
		}

		return ChatColor.translateAlternateColorCodes('&', message);
	}

	public static List<String> translate(List<String> list) {
		final List<String> translated = new ArrayList<>();

		for (String s : list) {
			translated.add(translate(s));
		}

		return translated;
	}
}
