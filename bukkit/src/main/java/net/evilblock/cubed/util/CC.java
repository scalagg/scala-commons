package net.evilblock.cubed.util;

import co.aikar.commands.MessageType;
import com.google.common.collect.Lists;
import gg.scala.commons.ExtendedScalaPlugin;
import gg.scala.commons.command.ScalaCommandManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

/**
 * @author puugz, GrowlyX
 * @since 08/07/2021 17:49
 */
public class CC {
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

        // TODO: 10/10/2022 use liftgate lease system
        try {
            for (final Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
                if (plugin instanceof ExtendedScalaPlugin) {
                    try {
                        final ScalaCommandManager manager = ((ExtendedScalaPlugin) plugin).getCommandManager();

                        final ChatColor primaryCC = ChatColor.getByChar(CC.PRI.charAt(1));
                        final ChatColor secondaryCC = ChatColor.getByChar(CC.SEC.charAt(1));

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

    public static final String BLANK_LINE = "§8 §8 §1 §3 §3 §7 §8 §r";
    public static final String BL = BLANK_LINE;
    public static final String SB_BAR = GRAY + S + "-------------------";

    static {
        setup(GREEN, YELLOW);
    }
}
