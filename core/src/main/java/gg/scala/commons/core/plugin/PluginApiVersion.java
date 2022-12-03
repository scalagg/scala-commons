package gg.scala.commons.core.plugin;

import gg.scala.commons.core.plugin.annotate.PluginSerializer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface PluginApiVersion {

    class Serializer {

        public static final String KEY = "api-version";

        public static PluginSerializer<PluginApiVersion> create() {
            return (annotation, data) -> {
                final String apiVersion = annotation.value();
                data.set(PluginApiVersion.Serializer.KEY, apiVersion);
            };
        }

    }

    String value();

}
