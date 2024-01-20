package gg.scala.commons.tablist.cell;

import gg.scala.commons.tablist.TablistHandle;
import gg.scala.commons.tablist.util.PacketUtil;
import gg.scala.commons.tablist.util.ScoreboardTeamUtil;
import gg.scala.commons.tablist.util.Util;
import org.bukkit.ChatColor;

import java.util.UUID;

/**
 * @author Reximian9k (https://github.com/reximian9k)
 */
public class TablistCellLegacy extends TablistCell {

    private String prefix = "";
    private String suffix = "";

    public TablistCellLegacy(TablistHandle tablist, UUID id, String slotBase) {
        super(tablist, id, slotBase);
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    @Override
    public void setValue(String value) {
        if (value == null) {
            this.prefix = "";
            this.suffix = "";
            return;
        }

        if (value.length() <= 16) {
            this.prefix = value;
            this.suffix = "";
        } else {
            this.prefix = value.substring(0, 16);
            String suffix = value.substring(16);

            if (prefix.endsWith("§")) {
                prefix = prefix.substring(0, 15);
                suffix = "§" + suffix;
            }

            ChatColor last;
            ChatColor lastColor = null, lastFormat = null;

            for (int i = prefix.length() - 2; i >= 0; i--) {
                if (prefix.charAt(i) == '§') {
                    last = ChatColor.getByChar(prefix.charAt(i + 1));

                    if (last != null) {
                        if (last.isFormat()) {
                            if (lastFormat == null) {
                                lastFormat = last;
                            }

                            if (lastColor != null) {
                                break;
                            }
                        } else {
                            if (lastColor == null) {
                                lastColor = last;
                            }

                            if (lastFormat != null) {
                                break;
                            }
                        }
                    }
                }
            }

            if (!suffix.startsWith("§")) {
                String suffixStart =
                        lastColor == null ? lastFormat == null ? "" : lastFormat.toString() :
                        lastColor + (lastFormat == null ? "" : lastFormat.toString());

                if (suffix.length() + suffixStart.length() > 16) {
                    this.suffix = suffixStart + suffix.substring(0, 16 - suffixStart.length());
                } else {
                    this.suffix = suffixStart + suffix;
                }
            } else {
                this.suffix = suffix.length() > 16 ? suffix.substring(0, 16) : suffix;
            }
        }
    }

    public void setValue(String prefix, String suffix) {
        this.prefix = prefix;
        this.suffix = suffix;
    }

    @Override
    public void sync() {
        Util.sendPacket(tablist.getHolder(), ScoreboardTeamUtil.updateTeam(cellBase, prefix, suffix, PacketUtil.TEAM_PROP_SPEC_NONE));
    }

}
