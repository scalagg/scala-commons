package gg.scala.commons.tablist.skins;

import gg.scala.commons.tablist.DefaultSkinData;
import gg.scala.commons.tablist.util.data.SkinData;

import java.util.Map;

public class MultiSkinDataMapping implements SkinDataMapping {

    private final Map<Integer, SkinData> mappings;

    public MultiSkinDataMapping(Map<Integer, SkinData> mappings) {
        this.mappings = mappings;
    }

    @Override
    public SkinData getMappingFor(int index) {
        SkinData exist = mappings.get(index);
        return exist == null ? DefaultSkinData.getDefaultSkin() : exist;
    }

    public Map<Integer, SkinData> getMappings() {
        return mappings;
    }

}
