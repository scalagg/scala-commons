package gg.scala.commons.tablist.skins;

import gg.scala.commons.tablist.util.data.SkinData;

public class SingleSkinDataMapping implements SkinDataMapping {

    private final SkinData icon;

    public SingleSkinDataMapping(SkinData icon) {
        this.icon = icon;
    }

    @Override
    public SkinData getMappingFor(int index) {
        return icon;
    }

    public SkinData getIcon() {
        return icon;
    }

}
