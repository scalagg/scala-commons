package gg.scala.commons.plugin.annotate;

import gg.scala.commons.core.plugin.*;
import gg.scala.commons.core.plugin.annotate.PluginProcessorAbstract;
import gg.scala.commons.core.plugin.annotate.PluginSerializerMap;

import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes({"gg.scala.commons.core.plugin.Plugin"})
public class SpigotPluginProcessor extends PluginProcessorAbstract {

    public static final String DEPEND_KEY = "depend";
    public static final String SOFT_DEPEND_KEY = "softdepend";
    public static final String PROPERTIES_FILE = "app.properties";

    private static final PluginSerializerMap SERIALIZER_MAP = PluginSerializerMap.builder()
        .put(Plugin.class, Plugin.Serializer.create())
        .put(PluginDependencyComposite.class,
            PluginDependencyComposite.Serializer.create(SpigotPluginProcessor.DEPEND_KEY, SpigotPluginProcessor.SOFT_DEPEND_KEY))
        .put(PluginDependency.class,
            PluginDependency.Serializer.create(SpigotPluginProcessor.DEPEND_KEY, SpigotPluginProcessor.SOFT_DEPEND_KEY))
        .put(PluginAuthor.class, PluginAuthor.Serializer.create())
        .put(PluginAuthorComposite.class, PluginAuthorComposite.Serializer.create())
        .put(PluginLoadState.class, PluginLoadState.Serializer.create())
        .put(PluginWebsite.class, PluginWebsite.Serializer.create())
        .put(PluginLoadBefore.class, PluginLoadBefore.Serializer.create())
        .put(PluginLoadBeforeComposite.class, PluginLoadBeforeComposite.Serializer.create())
        .build();

    public SpigotPluginProcessor() {
        super(SpigotPluginProcessor.SERIALIZER_MAP, SpigotPluginProcessor.PROPERTIES_FILE);
    }

}
