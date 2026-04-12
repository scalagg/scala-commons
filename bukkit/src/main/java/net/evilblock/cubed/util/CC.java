package net.evilblock.cubed.util;

import co.aikar.commands.MessageType;
import com.google.common.collect.Lists;
import gg.scala.commons.ExtendedScalaPlugin;
import gg.scala.commons.command.ScalaCommandManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CC {
    public static final char COLOR_CHAR = ChatColor.COLOR_CHAR;
    private static final Pattern HEX_PATTERN = Pattern.compile("#[a-fA-F0-9]{6}");
    public static final String NORMAL_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String SMALL_ALPHABET = "ᴀʙᴄᴅᴇꜰɢʜɪᴊᴋʟᴍɴᴏᴘǫʀѕᴛᴜᴠᴡxʏᴢ";

    public static void setup(
        @NotNull String primary,
        @NotNull String secondary
    ) {
        PRI = primary;
        SEC = secondary;

        B_PRI = primary + BOLD;
        B_SEC = secondary + BOLD;

        UB_PRI = primary + BOLD + UNDERLINE;
        UB_SEC = secondary + BOLD + UNDERLINE;

        try {
            for (final Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
                if (plugin instanceof ExtendedScalaPlugin) {
                    try {
                        final ScalaCommandManager manager = ((ExtendedScalaPlugin) plugin).getCommandManager();

                        final ChatColor primaryCC = nearestChatColor(CC.PRI);
                        final ChatColor secondaryCC = nearestChatColor(CC.SEC);

                        Lists.newArrayList(
                            MessageType.HELP,
                            MessageType.INFO,
                            MessageType.SYNTAX
                        ).forEach(type -> {
                            manager.getFormat(type).setColor(3, ChatColor.GRAY);
                            manager.getFormat(type).setColor(2, secondaryCC);
                            manager.getFormat(type).setColor(1, primaryCC);
                        });
                    } catch (Exception ignored) {

                    }
                }
            }
        } catch (Exception ignored) {

        }
    }

    public static String PRI;
    public static String SEC;

    public static String B_PRI;
    public static String B_SEC;

    public static String UB_PRI;
    public static String UB_SEC;

    public static final String BLUE = ChatColor.BLUE.toString();
    public static final String AQUA = ChatColor.AQUA.toString();
    public static final String YELLOW = ChatColor.YELLOW.toString();
    public static final String RED = ChatColor.RED.toString();
    public static final String GRAY = ChatColor.GRAY.toString();
    public static final String GOLD = ChatColor.GOLD.toString();
    public static final String GREEN = ChatColor.GREEN.toString();
    public static final String WHITE = ChatColor.WHITE.toString();
    public static final String BLACK = ChatColor.BLACK.toString();
    public static final String BOLD = ChatColor.BOLD.toString();
    public static final String ITALIC = ChatColor.ITALIC.toString();
    public static final String STRIKE_THROUGH = ChatColor.STRIKETHROUGH.toString();
    public static final String UNDERLINE = ChatColor.UNDERLINE.toString();
    public static final String RESET = ChatColor.RESET.toString();
    public static final String MAGIC = ChatColor.MAGIC.toString();

    public static final String OBFUSCATED = MAGIC;
    public static final String U = UNDERLINE;
    public static final String B = BOLD;
    public static final String M = MAGIC;
    public static final String O = MAGIC;
    public static final String I = ITALIC;
    public static final String S = STRIKE_THROUGH;
    public static final String R = RESET;

    public static final String DARK_BLUE = ChatColor.DARK_BLUE.toString();
    public static final String DARK_AQUA = ChatColor.DARK_AQUA.toString();
    public static final String DARK_GRAY = ChatColor.DARK_GRAY.toString();
    public static final String DARK_GREEN = ChatColor.DARK_GREEN.toString();
    public static final String DARK_PURPLE = ChatColor.DARK_PURPLE.toString();
    public static final String DARK_RED = ChatColor.DARK_RED.toString();
    public static final String D_BLUE = DARK_BLUE;
    public static final String D_AQUA = DARK_AQUA;
    public static final String D_GRAY = DARK_GRAY;
    public static final String D_GREEN = DARK_GREEN;
    public static final String D_PURPLE = DARK_PURPLE;
    public static final String D_RED = DARK_RED;
    public static final String LIGHT_PURPLE = ChatColor.LIGHT_PURPLE.toString();
    public static final String L_PURPLE = LIGHT_PURPLE;
    public static final String PINK = L_PURPLE;

    public static final String B_BLUE = BLUE + B;
    public static final String UB_BLUE = BLUE + B + U;
    public static final String U_BLUE = BLUE + U;

    public static final String B_AQUA = AQUA + B;
    public static final String UB_AQUA = AQUA + B + U;
    public static final String U_AQUA = AQUA + U;

    public static final String B_YELLOW = YELLOW + B;
    public static final String UB_YELLOW = YELLOW + B + U;
    public static final String U_YELLOW = YELLOW + U;

    public static final String B_RED = RED + B;
    public static final String UB_RED = RED + B + U;
    public static final String U_RED = RED + U;

    public static final String B_GRAY = GRAY + B;
    public static final String UB_GRAY = GRAY + B + U;
    public static final String U_GRAY = GRAY + U;

    public static final String B_GOLD = GOLD + B;
    public static final String UB_GOLD = GOLD + B + U;
    public static final String U_GOLD = GOLD + U;

    public static final String B_GREEN = GREEN + B;
    public static final String UB_GREEN = GREEN + B + U;
    public static final String U_GREEN = GREEN + U;

    public static final String UB_PINK = PINK + B + U;
    public static final String B_PINK = PINK + B;
    public static final String U_PINK = PINK + U;

    public static final String B_WHITE = WHITE + B;
    public static final String UB_WHITE = WHITE + B + U;
    public static final String U_WHITE = WHITE + U;

    public static final String B_BLACK = BLACK + B;
    public static final String UB_BLACK = BLACK + B + U;
    public static final String U_BLACK = BLACK + U;

    public static final String UBD_BLUE = D_BLUE + B + U;
    public static final String BD_BLUE = D_BLUE + B;
    public static final String UD_BLUE = D_BLUE + U;

    public static final String UBD_AQUA = D_AQUA + B + U;
    public static final String BD_AQUA = D_AQUA + B;
    public static final String UD_AQUA = D_AQUA + U;

    public static final String UBD_GRAY = D_GRAY + B + U;
    public static final String BD_GRAY = D_GRAY + B;
    public static final String UD_GRAY = D_GRAY + U;

    public static final String UBD_GREEN = D_GREEN + B + U;
    public static final String BD_GREEN = D_GREEN + B;
    public static final String UD_GREEN = D_GREEN + U;

    public static final String UBD_PURPLE = D_PURPLE + B + U;
    public static final String BD_PURPLE = D_PURPLE + B;
    public static final String UD_PURPLE = D_PURPLE + U;

    public static final String UBD_RED = D_RED + B + U;
    public static final String BD_RED = D_RED + B;
    public static final String UD_RED = D_RED + U;

    public static final String UBL_PURPLE = L_PURPLE + B + U;
    public static final String BL_PURPLE = L_PURPLE + B;
    public static final String UL_PURPLE = L_PURPLE + U;

    public static final String I_BLUE = BLUE + I;
    public static final String I_AQUA = AQUA + I;
    public static final String I_YELLOW = YELLOW + I;
    public static final String I_RED = RED + I;
    public static final String I_GRAY = GRAY + I;
    public static final String I_GOLD = GOLD + I;
    public static final String I_GREEN = GREEN + I;
    public static final String I_WHITE = WHITE + I;
    public static final String I_BLACK = BLACK + I;

    public static final String ID_RED = D_RED + I;
    public static final String ID_BLUE = D_BLUE + I;
    public static final String ID_AQUA = D_AQUA + I;
    public static final String ID_GRAY = D_GRAY + I;
    public static final String ID_GREEN = D_GREEN + I;
    public static final String ID_PURPLE = D_PURPLE + I;
    public static final String IL_PURPLE = L_PURPLE + I;

    public static final String BLANK_LINE = "\u00A78 \u00A78 \u00A71 \u00A73 \u00A73 \u00A77 \u00A78 \u00A7r";
    public static final String BL = BLANK_LINE;
    public static final String SB_BAR = GRAY + S + "-------------------";

    public static final String LEFT_ARROW = "«";
    public static final String RIGHT_ARROW = "»";
    public static final String HEART = DARK_RED + "❤";

    public static String translate(String message) {
        if (message == null) {
            return null;
        }

        Matcher matcher = HEX_PATTERN.matcher(message);
        while (matcher.find()) {
            String hexCode = message.substring(matcher.start(), matcher.end());
            message = message.replace(hexCode, hexToLegacy(hexCode));
            matcher = HEX_PATTERN.matcher(message);
        }

        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static String toSmallCaps(String message) {
        if (message == null || message.isEmpty()) {
            return message;
        }

        StringBuilder convertedText = new StringBuilder();
        String upperCase = message.toUpperCase(Locale.ROOT);

        for (int index = 0; index < upperCase.length(); index++) {
            char character = upperCase.charAt(index);
            if (character == ' ') {
                convertedText.append(character);
                continue;
            }

            int alphabetIndex = NORMAL_ALPHABET.indexOf(character);
            convertedText.append(alphabetIndex == -1 ? character : SMALL_ALPHABET.charAt(alphabetIndex));
        }

        return convertedText.toString();
    }

    public static String reverseTranslate(String colorCode) {
        if (colorCode == null || !colorCode.startsWith("§x")) {
            throw new IllegalArgumentException("Invalid color code format");
        }

        StringBuilder hexColor = new StringBuilder("#");
        for (int i = 3; i < colorCode.length(); i += 2) {
            hexColor.append(colorCode.charAt(i));
        }

        return hexColor.toString();
    }

    public static String getHexColor(String color) {
        if (color == null || color.isEmpty()) {
            return "#FFFFFF";
        }

        if (color.contains("#")) {
            return color;
        }

        if (color.contains("§x")) {
            return reverseTranslate(color);
        }

        if (color.length() > 1 && color.charAt(0) == COLOR_CHAR) {
            ChatColor chatColor = ChatColor.getByChar(color.charAt(1));
            return CHAT_COLOR_TO_HEX.getOrDefault(chatColor, "#FFFFFF");
        }

        return color;
    }

    public static ChatColor getNearestChatColorByHex(String hex) {
        java.awt.Color color = java.awt.Color.decode(hex);
        ChatColor nearestColor = ChatColor.WHITE;
        double nearestDistance = Double.MAX_VALUE;

        for (Map.Entry<ChatColor, String> entry : CHAT_COLOR_TO_HEX.entrySet()) {
            java.awt.Color chatColor = java.awt.Color.decode(entry.getValue());
            double distance = getColorDistance(color, chatColor);

            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearestColor = entry.getKey();
            }
        }

        return nearestColor;
    }

    public static ChatColor nearestChatColor(String input) {
        if (input == null || input.isEmpty()) {
            return ChatColor.WHITE;
        }

        if (input.startsWith("#") && input.length() == 7) {
            return getNearestChatColorByHex(input);
        }

        if (input.startsWith("§x") && input.length() >= 14) {
            return getNearestChatColorByHex(reverseTranslate(input.substring(0, 14)));
        }

        ChatColor chatColor = input.length() > 1 ? ChatColor.getByChar(input.charAt(1)) : null;
        return chatColor == null ? ChatColor.WHITE : chatColor;
    }

    public static ChatColor toChatColor(Color color) {
        return COLOR_TO_CHAT_COLOR.getOrDefault(color, ChatColor.WHITE);
    }

    public static String getColorName(Color color) {
        return COLOR_TO_NAME.getOrDefault(color, "Custom");
    }

    public static Material toWoolData(ChatColor color) {
        return CHAT_COLOR_TO_WOOL_DATA.getOrDefault(color, wool("WHITE_WOOL"));
    }

    public static Set<Color> getPredefinedColors() {
        return COLOR_TO_NAME.keySet();
    }

    public static final Map<ChatColor, String> CHAT_COLOR_TO_HEX = new HashMap<ChatColor, String>() {{
        put(ChatColor.DARK_RED, "#AA0000");
        put(ChatColor.RED, "#FF5555");
        put(ChatColor.GOLD, "#FFAA00");
        put(ChatColor.YELLOW, "#FFFF55");
        put(ChatColor.GREEN, "#55FF55");
        put(ChatColor.DARK_GREEN, "#00AA00");
        put(ChatColor.DARK_AQUA, "#00AAAA");
        put(ChatColor.AQUA, "#55FFFF");
        put(ChatColor.BLUE, "#5555FF");
        put(ChatColor.DARK_PURPLE, "#AA00AA");
        put(ChatColor.LIGHT_PURPLE, "#FF55FF");
        put(ChatColor.WHITE, "#FFFFFF");
        put(ChatColor.GRAY, "#AAAAAA");
        put(ChatColor.DARK_GRAY, "#555555");
        put(ChatColor.BLACK, "#000000");
    }};

    public static final Map<Color, String> COLOR_TO_NAME = new HashMap<Color, String>() {{
        put(Color.WHITE, "WHITE");
        put(Color.RED, "RED");
        put(Color.BLUE, "BLUE");
        put(Color.AQUA, "AQUA");
        put(Color.PURPLE, "DARK_PURPLE");
        put(Color.FUCHSIA, "LIGHT_PURPLE");
        put(Color.ORANGE, "GOLD");
        put(Color.YELLOW, "YELLOW");
        put(Color.LIME, "GREEN");
        put(Color.GREEN, "DARK_GREEN");
        put(Color.SILVER, "GRAY");
        put(Color.GRAY, "DARK_GRAY");
        put(Color.MAROON, "DARK_RED");
        put(Color.NAVY, "DARK_AQUA");
        put(Color.BLACK, "BLACK");
    }};

    public static final Map<Color, ChatColor> COLOR_TO_CHAT_COLOR = new HashMap<Color, ChatColor>() {{
        put(Color.WHITE, ChatColor.WHITE);
        put(Color.RED, ChatColor.RED);
        put(Color.BLUE, ChatColor.BLUE);
        put(Color.AQUA, ChatColor.AQUA);
        put(Color.PURPLE, ChatColor.DARK_PURPLE);
        put(Color.FUCHSIA, ChatColor.LIGHT_PURPLE);
        put(Color.ORANGE, ChatColor.GOLD);
        put(Color.YELLOW, ChatColor.YELLOW);
        put(Color.LIME, ChatColor.GREEN);
        put(Color.GREEN, ChatColor.DARK_GREEN);
        put(Color.SILVER, ChatColor.GRAY);
        put(Color.GRAY, ChatColor.DARK_GRAY);
        put(Color.MAROON, ChatColor.DARK_RED);
        put(Color.NAVY, ChatColor.DARK_AQUA);
        put(Color.BLACK, ChatColor.BLACK);
    }};

    public static final Map<ChatColor, Material> CHAT_COLOR_TO_WOOL_DATA = new HashMap<ChatColor, Material>() {{
        put(ChatColor.DARK_RED, wool("RED_WOOL"));
        put(ChatColor.RED, wool("RED_WOOL"));
        put(ChatColor.GOLD, wool("ORANGE_WOOL"));
        put(ChatColor.YELLOW, wool("YELLOW_WOOL"));
        put(ChatColor.GREEN, wool("LIME_WOOL"));
        put(ChatColor.DARK_GREEN, wool("GREEN_WOOL"));
        put(ChatColor.DARK_AQUA, wool("CYAN_WOOL"));
        put(ChatColor.AQUA, wool("LIGHT_BLUE_WOOL"));
        put(ChatColor.BLUE, wool("BLUE_WOOL"));
        put(ChatColor.DARK_PURPLE, wool("PURPLE_WOOL"));
        put(ChatColor.LIGHT_PURPLE, wool("MAGENTA_WOOL"));
        put(ChatColor.WHITE, wool("WHITE_WOOL"));
        put(ChatColor.GRAY, wool("LIGHT_GRAY_WOOL"));
        put(ChatColor.DARK_GRAY, wool("GRAY_WOOL"));
        put(ChatColor.BLACK, wool("BLACK_WOOL"));
    }};

    private static String hexToLegacy(String hexCode) {
        String normalized = hexCode.replace('#', 'x');
        StringBuilder builder = new StringBuilder();
        for (char character : normalized.toCharArray()) {
            builder.append('&').append(character);
        }
        return builder.toString();
    }

    private static double getColorDistance(java.awt.Color first, java.awt.Color second) {
        int redDiff = first.getRed() - second.getRed();
        int greenDiff = first.getGreen() - second.getGreen();
        int blueDiff = first.getBlue() - second.getBlue();
        return Math.sqrt(redDiff * redDiff + greenDiff * greenDiff + blueDiff * blueDiff);
    }

    private static Material wool(String modernName) {
        Material material = Material.matchMaterial(modernName);
        return material != null ? material : Material.matchMaterial("WOOL");
    }

    static {
        setup(GOLD, YELLOW);
    }
}
