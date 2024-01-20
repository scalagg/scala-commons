package gg.scala.commons.tablist.adapter;

import gg.scala.commons.tablist.TablistHandle;
import gg.scala.commons.tablist.cell.TablistCellModern;
import gg.scala.commons.tablist.util.TablistNavigation;
import gg.scala.commons.tablist.util.data.SkinData;
import io.github.nosequel.tab.shared.entry.TabElement;
import io.github.nosequel.tab.shared.entry.TabEntry;

import java.util.Collections;
import java.util.List;

public class TabElementProxy extends TabElement {

    private TablistHandle activeHandle;

    public void setActiveHandle(TablistHandle activeHandle) {
        this.activeHandle = activeHandle;
    }

    @Override
    public void add(int x, int y, String text, int ping) {
       var cell = activeHandle.getCell(TablistNavigation.getIndex(activeHandle, x, y));
       cell.setValue(text);
       cell.setPing(ping);
       cell.sync();
    }

    @Override
    public void add(int x, int y, String text, int ping, String[] skinData) {
        var cell = activeHandle.getCell(TablistNavigation.getIndex(activeHandle, x, y));
        cell.setValue(text);
        cell.setPing(ping);

        if (cell instanceof TablistCellModern mCell) {
            mCell.setSkinData(new SkinData(skinData[0], skinData[1]));
        }

        cell.sync();
    }

    @Override
    public List<TabEntry> getEntries() {
        return Collections.emptyList();
    }

    @Override
    public String getHeader() {
        return "";
    }

    @Override
    public String getFooter() {
        return "";
    }

    @Override
    public void setHeader(String header) { }

    @Override
    public void setFooter(String footer) { }

}
