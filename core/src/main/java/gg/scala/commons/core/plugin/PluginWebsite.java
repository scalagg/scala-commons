package gg.scala.commons.core.plugin;

import gg.scala.commons.core.plugin.annotate.PluginSerializer;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface PluginWebsite {

    class Serializer {

        public static final String KEY = "website";

        public static PluginSerializer<PluginWebsite> create() {
            return (annotation, data) -> {
                final String website = annotation.value();
                data.set(PluginWebsite.Serializer.KEY, website);
            };
        }

    }

    String value();

}
