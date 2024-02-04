package gg.scala.commons.tablist;


import gg.scala.commons.tablist.adapter.TablistAdapter;
import gg.scala.commons.tablist.adapter.TablistAdapterLegacy;
import gg.scala.commons.tablist.adapter.TablistAdapterModern;
import gg.scala.commons.tablist.cell.CellStacking;
import gg.scala.commons.tablist.cell.TablistCell;
import gg.scala.commons.tablist.skins.SkinDataMapping;
import gg.scala.commons.tablist.util.PacketUtil;
import gg.scala.commons.tablist.util.TablistEntryHelper;
import gg.scala.commons.tablist.util.Util;
import gg.scala.commons.tablist.util.data.TablistEntry;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

/**
 * @author Reximian9k (https://github.com/reximian9k)
 */
public class TablistHandle {

    private final Player holder;

    public TablistHandle(Player holder) {
        this.holder = holder;
        this.stacking = Util.getClientVersion(holder) < 47 ? CellStacking.HORIZONTAL : CellStacking.VERTICAL;
    }

    public Player getHolder() {
        return holder;
    }

    // ======================================================================================================== //

    private boolean enabled;
    private boolean cancelVanillaPackets;
    private boolean extendedFeaturesEnabled;

    private TablistCell[] cells;
    private TablistAdapter extension;

    private CellStacking stacking;
    private String cellLength = "           ";
    private String tablistAppearanceLegacy, tablistAppearanceModern;

    private SkinDataMapping skinMapping = DefaultSkinData.getDefaultSkinMapping();
    private gg.scala.commons.tablist.adapter.TablistPopulator tablistPopulator;

    // ======================================================================================================== //

    public boolean isEnabled() {
        return enabled;
    }

    public void init() {
        boolean v17 = Util.getClientVersion(holder) < 47;

        if (cells == null) {
            this.extension = v17 ? new TablistAdapterLegacy(this) : new TablistAdapterModern(this);
            this.cells = new TablistCell[v17 ? 60 : 80];
            this.extendedFeaturesEnabled = !v17;
            extension.initTablist();
        }
    }

    public void setEnabled(boolean enabled) {
        boolean v17 = Util.getClientVersion(holder) < 47;

        if (this.enabled == enabled) return;
        this.enabled = enabled;

        if (enabled) {
            if (v17) {
                for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                    Util.sendPacket(holder, PacketUtil.createTablistPacket(PacketUtil.TablistPacketAction.REMOVE_PLAYER,
                            TablistEntry.builder().userId(player.getUniqueId()).build()));
                }
            }

            extension.createTablist();
        } else {
            extension.destroyTablist();

            if (v17) {
                for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                    Util.sendPacket(holder, PacketUtil.createTablistPacket(PacketUtil.TablistPacketAction.ADD_PLAYER,
                            TablistEntryHelper.forPlayer(player).build()));
                }
            }
        }
    }

    public int getSize() {
        return cells.length;
    }

    public CellStacking getCellStacking() {
        return stacking;
    }

    public void setCellStacking(CellStacking stacking) {
        this.stacking = stacking;
    }

    public TablistCell getCell(int index) {
        return cells[index];
    }

    public void updateCell(int index) {
        cells[index].sync();
    }

    public void updateCells(int... indices) {
        for (int index : indices) {
            cells[index].sync();
        }
    }

    public void updateCell(List<Integer> indices) {
        for (int index : indices) {
            cells[index].sync();
        }
    }

    public int getCellLength() {
        return cellLength.length();
    }

    public String getCellLengthString() {
        return cellLength;
    }

    public void setCellLength(int length) {
        if (length == 0) {
            cellLength = "";
            return;
        }

        char[] c = new char[Math.max(1, length)];

        Arrays.fill(c, ' ');

        cellLength = new String(c);
    }

    public String getTablistAppearance(int version) {
        return version < 47 ? tablistAppearanceLegacy : tablistAppearanceModern;
    }

    public void setTablistAppearance(int version, String appearance) {
        switch (version) {
            case 4:
            case 5:
                tablistAppearanceLegacy = appearance;
                break;
            default:
                tablistAppearanceModern = appearance;
                break;
        }
    }

    public void setTablistAppearance(String appearance) {
        tablistAppearanceLegacy = tablistAppearanceModern = appearance;
    }

    public SkinDataMapping getSkinMapping() {
        return skinMapping;
    }

    public void setSkinMapping(SkinDataMapping skinMapping) {
        this.skinMapping = skinMapping == null ? DefaultSkinData.getDefaultSkinMapping() : skinMapping;
    }

    public gg.scala.commons.tablist.adapter.TablistPopulator getTablistPopulator() {
        return tablistPopulator;
    }

    public void setTablistPopulator(gg.scala.commons.tablist.adapter.TablistPopulator tablistPopulator) {
        this.tablistPopulator = tablistPopulator;
    }

    // ======================================================================================================== //

    public boolean cancelVanillaPackets() {
        return cancelVanillaPackets;
    }

    public boolean extendedFeaturesEnabled() {
        return extendedFeaturesEnabled;
    }

    public int getCellCount() {
        return cells.length;
    }

    public TablistCell[] getCells() {
        return cells;
    }

    public void onTick() {
        if (enabled && tablistPopulator != null) {
            tablistPopulator.onTick();
        }
    }

}
