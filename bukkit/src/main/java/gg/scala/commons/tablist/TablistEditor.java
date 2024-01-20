package gg.scala.commons.tablist;

import gg.scala.commons.tablist.cell.CellStacking;
import gg.scala.commons.tablist.skins.SkinDataMapping;

/**
 * @author Reximian9k (https://github.com/reximian9k)
 */
public class TablistEditor {

    private final TablistHandle tablist;

    public TablistEditor(TablistHandle tablist) {
        this.tablist = tablist;
    }

    public TablistEditor setEnabled(boolean enabled, boolean fully) {
        tablist.setEnabled(enabled, fully);
        return this;
    }

    public TablistEditor setSkinMapping(SkinDataMapping mapping) {
        tablist.setSkinMapping(mapping);
        return this;
    }

    public TablistEditor setPopulator(gg.scala.commons.tablist.adapter.TablistPopulator populator) {
        tablist.setTablistPopulator(populator);
        return this;
    }

    public TablistEditor setCellStacking(CellStacking stacking) {
        tablist.setCellStacking(stacking);
        return this;
    }

    public TablistEditor setCellLength(int length) {
        tablist.setCellLength(length);
        return this;
    }

    public TablistEditor setTablistAppearance(int version, String appearance) {
        tablist.setTablistAppearance(version, appearance);
        return this;
    }

    public TablistEditor setTablistAppearance(String appearance) {
        tablist.setTablistAppearance(appearance);
        return this;
    }

}
