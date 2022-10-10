package gg.scala.commons.core.plugin;

import com.google.common.collect.Lists;
import gg.scala.commons.core.plugin.annotate.PluginSerializer;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
@Repeatable(PluginLoadBeforeComposite.class)
public @interface PluginLoadBefore {

    class Serializer {

        public static final String KEY = "loadbefore";

        public static PluginSerializer<PluginLoadBefore> create() {
            return (annotation, data) -> {
                final String pluginName = annotation.value();
                data.set(PluginLoadBefore.Serializer.KEY, Lists.newArrayList(pluginName));
            };
        }

    }

    String value();

}
