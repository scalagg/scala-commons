package gg.scala.commons.core.plugin.annotate;

import java.lang.annotation.Annotation;

@FunctionalInterface
public interface PluginSerializer<T extends Annotation> {

    void serializeIntoDescription(T annotation, PluginDescription data);

}
