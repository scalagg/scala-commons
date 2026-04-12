package gg.scala.commons.tablist.cell;

import gg.scala.commons.tablist.TablistHandle;
import gg.scala.commons.tablist.util.data.SkinData;

import java.util.UUID;

public abstract class TablistCell {

    protected final TablistHandle tablist;
    protected final UUID id;
    protected String cellBase;
    protected int ping;

    public TablistCell(TablistHandle tablist, UUID id, String cellBase) {
        this.tablist = tablist;
        this.id = id;
        this.cellBase = cellBase;
    }

    public UUID getId() {
        return id;
    }

    public String getCellBase() {
        return cellBase;
    }

    public int getPing() {
        return ping;
    }

    public void setPing(int ping) {
        this.ping = ping;
    }

    public void setValue(String value, boolean sync) {
        setValue(value);
        if (sync) sync();
    }

    public abstract void setValue(String value);

    public abstract void sync();

}
