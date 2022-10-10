package gg.scala.commons.core.plugin;

import gg.scala.commons.core.plugin.annotate.PluginSerializer;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
@Repeatable(PluginAuthorComposite.class)
public @interface PluginAuthor {

    class Serializer {

        public static final String KEY = "author";

        public static PluginSerializer<PluginAuthor> create() {
            return (annotation, data) -> {
                final String authorName = annotation.value();
                data.set(PluginAuthor.Serializer.KEY, authorName);
            };
        }

    }

    String value();

}
