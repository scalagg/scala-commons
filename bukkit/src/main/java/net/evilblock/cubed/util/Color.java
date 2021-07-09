package net.evilblock.cubed.util;

import org.bukkit.ChatColor;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author puugz
 * @since 08/07/2021 18:11
 */

public class Color {

	public static String translate(String message) {
		return ChatColor.translateAlternateColorCodes('&', message);
	}

	public static List<String> translate(List<String> list) {
		return list.stream().map(Color::translate).collect(Collectors.toList());
	}
}
