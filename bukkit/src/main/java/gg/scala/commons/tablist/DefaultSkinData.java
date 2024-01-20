package gg.scala.commons.tablist;

import gg.scala.commons.tablist.skins.SingleSkinDataMapping;
import gg.scala.commons.tablist.util.data.SkinData;

import javax.annotation.Nonnull;

/**
 * @author Reximian9k (https://github.com/reximian9k)
 */
public class DefaultSkinData {

    private static SkinData DEFAULT_SKIN = new SkinData(
            "eyJ0aW1lc3RhbXAiOjE1NDk3MDQ3MTA3NDYsInByb2ZpbGVJZCI6ImVmYWY1NzU3NzgxZTQ3YWViYzE0Y2Q4M" +
                    "mM5MWM3ZjgyIiwicHJvZmlsZU5hbWUiOiJNaW5lU2tpbiIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0" +
                    "ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmU" +
                    "vZjEyN2E4YzYwMzAzYTJlM2I5MzUxOTJlMzFhM2EzZjA2NzNiZDZkMDdmMGQ0OTViMmY3MTY4ZGI5ZjcwYm" +
                    "M0MyJ9fX0=",
            "qCkQgDlUaQ2PYAdRaxVvZOYCPu5Z++qQoLYAvOZSK4wbUuhwwtJ+MWia2FcYf2IM5pjdJ7MdEcbj5q4m3/F" +
                    "S6IQw9mhFdk/Fl1O4cUDqTJCTzOybLv0u94qloAfgEysVI/cnUzyhWLjIwV+GB/eXb1GDmxsZLkgad3azeX" +
                    "Gi4yuyhI4y4uNjO/JDBc7OTnfCukDXq+EeQ3KYzlmyjdQWFisJqm3b/gS398H6HGg6Bx9Sv6hNkiJEQFfre" +
                    "lWyDIMlBPsMGW8p3CI0H/l0EpWBkmr+ltDh0TcSiyBsOxOHK+UitOmpC/UFoEfjtIhnMLMlH2eMtw0L8EPyI" +
                    "uQfuV/C78/inxm1ai5luPg4FG7s0zDQK4WTBnLdJOeMK49T2dIOT1U119t2yh+cb9eBq62PGmYbPBI/FRcka" +
                    "/ET1rV3eyRFL8BbMoJFC+n8VGvbhCML0Rf8wnHroEiFYZzr8oSVWJPXvNJU8PgP2EcXH1xFFvPeO2D8QtOKf" +
                    "rNgc9jFA++igSwsRCNkKFO1tF4M+zyfVxyIm858ZzHCuBKQ7d2O1xJQ7z+u9/syDBxLxw0kSzwfPq8Vrp3Cw" +
                    "sUSID0vusauUh1m/RpgvUcIOoek5XSAFGnIN7MQrKo/bcwNFK8lhe/5BswUJ6sByFO1cLHHuzR5CfPuALlF5" +
                    "l/znpJb4ElaBac="
    );

    public static SingleSkinDataMapping DEFAULT_MAPPING = new SingleSkinDataMapping(getDefaultSkin());

    public static SkinData getDefaultSkin() {
        return DEFAULT_SKIN;
    }

    public static void setDefaultSkin(@Nonnull SkinData defaultSkin) {
        DEFAULT_SKIN = defaultSkin;
    }

    public static SingleSkinDataMapping getDefaultSkinMapping() {
        return DEFAULT_MAPPING;
    }

    public static void setDefaultMapping(@Nonnull SingleSkinDataMapping defaultMapping) {
        DEFAULT_MAPPING = defaultMapping;
    }

}
