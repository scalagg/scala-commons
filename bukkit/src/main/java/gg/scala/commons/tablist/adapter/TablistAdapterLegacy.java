package gg.scala.commons.tablist.adapter;

import gg.scala.commons.tablist.TablistHandle;
import gg.scala.commons.tablist.cell.CellStacking;
import gg.scala.commons.tablist.cell.TablistCell;
import gg.scala.commons.tablist.cell.TablistCellLegacy;
import gg.scala.commons.tablist.util.PacketUtil;
import gg.scala.commons.tablist.util.ScoreboardTeamUtil;
import gg.scala.commons.tablist.util.Util;
import gg.scala.commons.tablist.util.data.TablistEntry;

import java.util.Collections;
import java.util.UUID;

/**
 * @author Reximian9k (https://github.com/reximian9k)
 */
public class TablistAdapterLegacy extends TablistAdapter {

    public TablistAdapterLegacy(TablistHandle handle) {
        super(handle);
    }

    @Override
    public void createTablist() {
        if (handle.getCellStacking() == CellStacking.VERTICAL) {
            int[] columnIndex = new int[] { 0, 20, 40 };
            int currIndex = 0;

            for (int f = 0; f < 6; f++) {
                for (int s = 0; s < 10; s++) {
                    String base = "§" + f + "§" + s + "§r";
                    UUID fakeId = UUID.nameUUIDFromBytes(("OfflinePlayer:" + base).getBytes());
                    TablistCellLegacy cell = new TablistCellLegacy(handle, fakeId, base);

                    handle.getCells()[columnIndex[currIndex++]++] = cell;
                    sendPacketToHolder(PacketUtil.createTablistPacket(PacketUtil.TablistPacketAction.ADD_PLAYER,
                            TablistEntry.builder().userId(fakeId).name(cell.getCellBase()).ping(-1).build()));
                    sendPacketToHolder(ScoreboardTeamUtil.createTeam(
                            cell.getCellBase(), cell.getPrefix(), cell.getSuffix(),
                            Collections.singletonList(cell.getCellBase()), PacketUtil.TEAM_PROP_SPEC_NONE));

                    if (currIndex == 3) {
                        currIndex = 0;
                    }
                }
            }
        } else {
            for (int f = 0; f < 6; f++) {
                for (int s = 0; s < 10; s++) {
                    int i = Integer.parseInt(f + "" + s);
                    String base = "§" + f + "§" + s + "§r";
                    UUID fakeId = UUID.nameUUIDFromBytes(("OfflinePlayer:" + base).getBytes());
                    TablistCellLegacy cell = new TablistCellLegacy(handle, fakeId, base);

                    handle.getCells()[i] = cell;
                    sendPacketToHolder(PacketUtil.createTablistPacket(PacketUtil.TablistPacketAction.ADD_PLAYER,
                            TablistEntry.builder().userId(fakeId).name(cell.getCellBase()).ping(-1).build()));
                    sendPacketToHolder(ScoreboardTeamUtil.createTeam(
                            cell.getCellBase(), cell.getPrefix(), cell.getSuffix(),
                            Collections.singletonList(cell.getCellBase()), PacketUtil.TEAM_PROP_SPEC_NONE));
                }
            }
        }
    }

    @Override
    public void destroyTablist() {
        for (TablistCell cell : handle.getCells()) {
            sendPacketToHolder(PacketUtil.createTablistPacket(PacketUtil.TablistPacketAction.REMOVE_PLAYER,
                    TablistEntry.builder().name(cell.getCellBase()).build()));
            sendPacketToHolder(ScoreboardTeamUtil.deleteTeam(cell.getCellBase()));
        }
    }

}
