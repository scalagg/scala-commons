package gg.scala.commons.scripts;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import gg.scala.commons.ExtendedScalaPlugin;
import me.lucko.helper.menu.scheme.MenuScheme;
import me.lucko.helper.menu.scheme.SchemeMapping;
import me.lucko.helper.terminable.composite.CompositeTerminable;
import me.lucko.helper.text3.Text;
import me.lucko.scriptcontroller.bindings.BindingsBuilder;
import me.lucko.scriptcontroller.bindings.BindingsSupplier;
import me.lucko.scriptcontroller.environment.script.Script;
import org.bukkit.Bukkit;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author GrowlyX
 * @since 4/22/2022
 */
public class ScriptBindings
{
    public static final class GeneralScriptBindings implements BindingsSupplier {
        private static final Supplier<ArrayList> ARRAY_LIST = ArrayList::new;
        private static final Supplier<LinkedList> LINKED_LIST = LinkedList::new;
        private static final Supplier<HashSet> HASH_SET = HashSet::new;
        private static final Supplier<HashMap> HASH_MAP = HashMap::new;
        private static final Supplier<CopyOnWriteArrayList> COPY_ON_WRITE_ARRAY_LIST = CopyOnWriteArrayList::new;
        private static final Supplier<Set> CONCURRENT_HASH_SET = ConcurrentHashMap::newKeySet;
        private static final Supplier<ConcurrentHashMap> CONCURRENT_HASH_MAP = ConcurrentHashMap::new;
        private static final Function<Object[], ArrayList> LIST_OF = objects -> new ArrayList<>(Arrays.asList(objects));
        private static final Function<Object[], HashSet> SET_OF = objects -> new HashSet<>(Arrays.asList(objects));
        private static final Function<Object[], ImmutableList> IMMUTABLE_LIST_OF = ImmutableList::copyOf;
        private static final Function<Object[], ImmutableSet> IMMUTABLE_SET_OF = ImmutableSet::copyOf;
        private static final Function<String, UUID> PARSE_UUID = s -> {
            try {
                return UUID.fromString(s);
            } catch (IllegalArgumentException e) {
                return null;
            }
        };

        @Override
        public void supplyBindings(Script script, BindingsBuilder bindings) {
            bindings.put("newArrayList", ARRAY_LIST);
            bindings.put("newLinkedList", LINKED_LIST);
            bindings.put("newHashSet", HASH_SET);
            bindings.put("newHashMap", HASH_MAP);
            bindings.put("newCopyOnWriteArrayList", COPY_ON_WRITE_ARRAY_LIST);
            bindings.put("newConcurrentHashSet", CONCURRENT_HASH_SET);
            bindings.put("newConcurrentHashMap", CONCURRENT_HASH_MAP);
            bindings.put("listOf", LIST_OF);
            bindings.put("setOf", SET_OF);
            bindings.put("immutableListOf", IMMUTABLE_LIST_OF);
            bindings.put("immutableSetOf", IMMUTABLE_SET_OF);
            bindings.put("parseUuid", PARSE_UUID);
        }
    }

    /**
     * Script bindings for helper utilities
     */
    public static class HelperScriptBindings implements BindingsSupplier {
        private final ExtendedScalaPlugin plugin;

        HelperScriptBindings(ExtendedScalaPlugin plugin) {
            this.plugin = plugin;
        }

        @Override
        public void supplyBindings(Script script, BindingsBuilder bindings) {
            // provide a terminable registry
            CompositeTerminable registry = CompositeTerminable.create();
            script.getClosables().bind(registry);
            bindings.put("registry", registry);

            // provide core server classes
            bindings.put("server", Bukkit.getServer());
            bindings.put("plugin", this.plugin);
            bindings.put("services", Bukkit.getServicesManager());

            // some util functions
            bindings.put("colorize", (Function<Object, String>) HelperScriptBindings::colorize);
            bindings.put("newEmptyScheme", (Supplier<MenuScheme>) HelperScriptBindings::newScheme);
            bindings.put("newScheme", (Function<SchemeMapping, MenuScheme>) HelperScriptBindings::newScheme);
        }

        private static String colorize(Object object) {
            return Text.colorize(object.toString());
        }

        private static MenuScheme newScheme() {
            return new MenuScheme();
        }

        private static MenuScheme newScheme(SchemeMapping mapping) {
            return new MenuScheme(mapping);
        }
    }
}
