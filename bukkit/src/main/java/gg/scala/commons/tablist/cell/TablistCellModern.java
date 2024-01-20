package gg.scala.commons.tablist.cell;

import gg.scala.commons.tablist.TablistHandle;
import gg.scala.commons.tablist.util.PacketUtil;
import gg.scala.commons.tablist.util.Util;
import gg.scala.commons.tablist.util.data.SkinData;
import gg.scala.commons.tablist.util.data.TablistEntry;
import org.bukkit.GameMode;

import java.util.UUID;

/**
 * @author Reximian9k (https://github.com/reximian9k)
 */
public class TablistCellModern extends TablistCell {

    private static final String EMPTY_DISPLAY_NAME = "{\"text\":\"\"}";

    private String displayName;
    private boolean requiresSync;
    protected SkinData skinData;
    protected boolean requiresSkinUpdate;

    public TablistCellModern(TablistHandle tablist, UUID id, String slotBase, String displayName) {
        super(tablist, id, slotBase);
        this.displayName = displayName;
    }

    @Override
    public void setValue(String value) {
        this.requiresSync = true;
        this.displayName = value;
    }

    public void setSkinData(SkinData skinData) {
        this.skinData = skinData;
        this.requiresSync = this.requiresSkinUpdate = true;
    }

    @Override
    public void sync() {
        if (!requiresSync) return;
        requiresSync = false;

        if (displayName != null) {
            displayName = "{\"text\":\"" + displayName + "\"}";
        }

        if (requiresSkinUpdate) {
            Util.sendPacket(tablist.getHolder(), PacketUtil.createTablistPacket(PacketUtil.TablistPacketAction.REMOVE_PLAYER,
                    TablistEntry.builder()
                            .userId(id)
                            .build()));
            Util.sendPacket(tablist.getHolder(), PacketUtil.createTablistPacket(PacketUtil.TablistPacketAction.ADD_PLAYER,
                    TablistEntry.builder().userId(id)
                            .name(cellBase)
                            .icon(skinData)
                            .displayName(displayName == null ? EMPTY_DISPLAY_NAME : displayName)
                            .gameMode(GameMode.SURVIVAL).build()));
        } else {
            Util.sendPacket(tablist.getHolder(), PacketUtil.createTablistPacket(PacketUtil.TablistPacketAction.UPDATE_DISPLAY_NAME,
                    TablistEntry.builder()
                            .userId(id)
                            .name(cellBase)
                            .displayName(displayName == null ? EMPTY_DISPLAY_NAME : displayName)
                            .build()));
        }
    }

}
