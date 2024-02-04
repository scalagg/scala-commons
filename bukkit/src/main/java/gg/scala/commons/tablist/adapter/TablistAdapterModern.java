package gg.scala.commons.tablist.adapter;

import gg.scala.commons.tablist.TablistHandle;
import gg.scala.commons.tablist.cell.CellStacking;
import gg.scala.commons.tablist.cell.TablistCellModern;
import gg.scala.commons.tablist.util.PacketUtil;
import gg.scala.commons.tablist.util.data.Profile;
import gg.scala.commons.tablist.util.TablistEntryHelper;
import gg.scala.commons.tablist.util.data.TablistEntry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


/**
 * @author Reximian9k (https://github.com/reximian9k)
 */
public class TablistAdapterModern extends TablistAdapter {

    public TablistAdapterModern(TablistHandle handle) {
        super(handle);
    }

    @Override
    public void initTablist() {
        final String emptyCellSpace = "{\"text\":\"" + handle.getCellLengthString() + "\"}";
//        List<TablistEntry> packetEntries = new ArrayList<>(80);

        if (handle.getCellStacking() == CellStacking.VERTICAL) {
            int currentIndex = 0;

            for (int f = 0; f < 8; f++) {
                for (int s = 0; s < 10; s++) {
                    String name = "!\u00A7" + f + "\u00A7" + s;

                    Profile profile = new Profile(UUID.nameUUIDFromBytes(name.getBytes()), name,
                            handle.getSkinMapping().getMappingFor(currentIndex));

//                    packetEntries.add(TablistEntryHelper.of(profile).displayName(emptyCellSpace).build());

                    handle.getCells()[currentIndex++] = new TablistCellModern(handle, profile.getId(), name, emptyCellSpace);
                }
            }
        } else {
            if (handle.getCellStacking() == CellStacking.HORIZONTAL) {
                for (int indexOffset = 0; indexOffset < 3; indexOffset++) {
                    for (int i = 0; i < 20; i++) {
                        int nameIndex = i + (indexOffset * 20);
                        char[] indexStr = (nameIndex < 10 ? "0" + nameIndex : "" + nameIndex).toCharArray();
                        String name = "!\u00A7" + indexStr[0] + "\u00A7" + indexStr[1];

                        int currentIndex = indexOffset + (i * 3);

                        Profile profile = new Profile(UUID.nameUUIDFromBytes(name.getBytes()), name,
                                handle.getSkinMapping().getMappingFor(currentIndex));

//                        packetEntries.add(TablistEntryHelper.of(profile).displayName(emptyCellSpace).build());

                        handle.getCells()[currentIndex] = new TablistCellModern(handle, profile.getId(), name, emptyCellSpace);
                    }
                }

                final String shortEmptyCellSpace = "{\"text\":\"\"}";
                for (int nameIndex = 60; nameIndex < 80; nameIndex++) {
                    char[] indexStr = ("" + nameIndex).toCharArray();
                    String name = "!\u00A7" + indexStr[0] + "\u00A7" + indexStr[1];

                    Profile profile = new Profile(UUID.nameUUIDFromBytes(name.getBytes()), name,
                            handle.getSkinMapping().getMappingFor(nameIndex));

//                    packetEntries.add(TablistEntryHelper.of(profile).displayName(shortEmptyCellSpace).build());

                    handle.getCells()[nameIndex] = new TablistCellModern(handle, profile.getId(), name, emptyCellSpace);
                }
            } else {
                for (int indexOffset = 0; indexOffset < 4; indexOffset++) {
                    for (int i = 0; i < 20; i++) {
                        int nameIndex = i + (indexOffset * 20);
                        char[] indexStr = (nameIndex < 10 ? "0" + nameIndex : "" + nameIndex).toCharArray();
                        String name = "!\u00A7" + indexStr[0] + "\u00A7" + indexStr[1];

                        int currentIndex = indexOffset + (i * 4);

                        Profile profile = new Profile(UUID.nameUUIDFromBytes(name.getBytes()), name,
                                handle.getSkinMapping().getMappingFor(currentIndex));

//                        packetEntries.add(TablistEntryHelper.of(profile).displayName(emptyCellSpace).build());

                        handle.getCells()[currentIndex] = new TablistCellModern(handle, profile.getId(), name, emptyCellSpace);
                    }
                }
            }

        }

//        sendPacketToHolder(PacketUtil.createTablistPacket(PacketUtil.TablistPacketAction.ADD_PLAYER, packetEntries));
    }

    @Override
    public void createTablist() {
        final String emptyCellSpace = "{\"text\":\"" + handle.getCellLengthString() + "\"}";
        List<TablistEntry> packetEntries = new ArrayList<>(80);

        if (handle.getCellStacking() == CellStacking.VERTICAL) {
            int currentIndex = 0;

            for (int f = 0; f < 8; f++) {
                for (int s = 0; s < 10; s++) {
                    String name = "!\u00A7" + f + "\u00A7" + s;

                    Profile profile = new Profile(UUID.nameUUIDFromBytes(name.getBytes()), name,
                            handle.getSkinMapping().getMappingFor(currentIndex));

                    packetEntries.add(TablistEntryHelper.of(profile).displayName(emptyCellSpace).build());

//                    handle.getCells()[currentIndex++] = new TablistCellModern(handle, profile.getId(), name, emptyCellSpace);
                }
            }
        } else {
            if (handle.getCellStacking() == CellStacking.HORIZONTAL) {
                for (int indexOffset = 0; indexOffset < 3; indexOffset++) {
                    for (int i = 0; i < 20; i++) {
                        int nameIndex = i + (indexOffset * 20);
                        char[] indexStr = (nameIndex < 10 ? "0" + nameIndex : "" + nameIndex).toCharArray();
                        String name = "!\u00A7" + indexStr[0] + "\u00A7" + indexStr[1];

                        int currentIndex = indexOffset + (i * 3);

                        Profile profile = new Profile(UUID.nameUUIDFromBytes(name.getBytes()), name,
                                handle.getSkinMapping().getMappingFor(currentIndex));

                        packetEntries.add(TablistEntryHelper.of(profile).displayName(emptyCellSpace).build());

//                        handle.getCells()[currentIndex] = new TablistCellModern(handle, profile.getId(), name, emptyCellSpace);
                    }
                }

                final String shortEmptyCellSpace = "{\"text\":\"\"}";
                for (int nameIndex = 60; nameIndex < 80; nameIndex++) {
                    char[] indexStr = ("" + nameIndex).toCharArray();
                    String name = "!\u00A7" + indexStr[0] + "\u00A7" + indexStr[1];

                    Profile profile = new Profile(UUID.nameUUIDFromBytes(name.getBytes()), name,
                            handle.getSkinMapping().getMappingFor(nameIndex));

                    packetEntries.add(TablistEntryHelper.of(profile).displayName(shortEmptyCellSpace).build());

//                    handle.getCells()[nameIndex] = new TablistCellModern(handle, profile.getId(), name, emptyCellSpace);
                }
            } else {
                for (int indexOffset = 0; indexOffset < 4; indexOffset++) {
                    for (int i = 0; i < 20; i++) {
                        int nameIndex = i + (indexOffset * 20);
                        char[] indexStr = (nameIndex < 10 ? "0" + nameIndex : "" + nameIndex).toCharArray();
                        String name = "!\u00A7" + indexStr[0] + "\u00A7" + indexStr[1];

                        int currentIndex = indexOffset + (i * 4);

                        Profile profile = new Profile(UUID.nameUUIDFromBytes(name.getBytes()), name,
                                handle.getSkinMapping().getMappingFor(currentIndex));

                        packetEntries.add(TablistEntryHelper.of(profile).displayName(emptyCellSpace).build());

//                        handle.getCells()[currentIndex] = new TablistCellModern(handle, profile.getId(), name, emptyCellSpace);
                    }
                }
            }

        }

        sendPacketToHolder(PacketUtil.createTablistPacket(PacketUtil.TablistPacketAction.ADD_PLAYER, packetEntries));
    }

    @Override
    public void destroyTablist() {
        sendPacketToHolder(PacketUtil.createTablistPacket(PacketUtil.TablistPacketAction.REMOVE_PLAYER,
                Arrays.stream(handle.getCells())
                        .map(cell -> TablistEntry.builder().userId(cell.getId()).build())
                        .collect(Collectors.toList())));
    }

}
