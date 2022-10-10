package gg.scala.commons.core.plugin;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import gg.scala.commons.core.plugin.annotate.PluginSerializer;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
@Repeatable(PluginDependencyComposite.class)
public @interface PluginDependency {

    class Serializer {

        public static PluginSerializer<PluginDependency> create(String dependsKey, String softDependsKey) {
            Preconditions.checkNotNull(dependsKey, "dependsKey");
            Preconditions.checkNotNull(softDependsKey, "softDependsKey");

            return (annotation, data) -> {
                final String key = annotation.soft() ? softDependsKey : dependsKey;
                data.set(key, Lists.newArrayList(annotation.value()));
            };
        }

    }

    String value();

    boolean soft() default false;

}
