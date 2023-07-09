package com.solexgames.lib.commons.hologram;

import com.google.common.base.Preconditions;
import com.google.gson.JsonParser;
import me.lucko.helper.Services;
import me.lucko.helper.hologram.Hologram;
import me.lucko.helper.hologram.HologramFactory;
import me.lucko.helper.scheduler.threadlock.ServerThreadLock;
import me.lucko.helper.serialize.Position;
import net.evilblock.cubed.ScalaCommonsSpigot;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author GrowlyX
 * @since 6/26/2021
 */
public class HologramManager {

    private final Map<String, CommonsHologram> hologramMap = new HashMap<>();

    public HologramFactory getHologramFactory() {
        return hologramFactory;
    }

    public Map<String, CommonsHologram> getHologramMap() {
        return hologramMap;
    }

    private HologramFactory hologramFactory;

    public void saveHologramService(HologramFactory factory) {
        this.hologramFactory = factory;
    }

    public HologramFactory loadHologramService() {
        return Services.load(HologramFactory.class);
    }

    public void formHologram(String identifier, Position position, List<String> lines) {
        Preconditions.checkNotNull(identifier, "id");
        Preconditions.checkNotNull(this.hologramFactory, "hologramFactory");

        final CommonsHologram hologram = new CommonsHologram(identifier, this.hologramFactory.newHologram(position, lines));
        hologram.setLines(lines);
        hologram.setPosition(position);
        hologram.spawn();

        this.hologramMap.put(identifier, hologram);
    }

    public void deleteIfPresent(String identifier) {
        Preconditions.checkNotNull(identifier, "id");
        Preconditions.checkNotNull(this.hologramFactory, "hologramFactory");

        final CommonsHologram hologram = this.hologramMap.remove(identifier);

        hologram.remove();
    }

    public void loadAllAsync() {
        Preconditions.checkNotNull(this.hologramFactory, "hologramFactory");
        ScalaCommonsSpigot.getInstance().saveDefaultConfig();

        final FileConfiguration configuration = ScalaCommonsSpigot.getInstance().getConfig();
        final ConfigurationSection section = configuration.getConfigurationSection("holograms");
        final JsonParser jsonParser = new JsonParser();

        if (section != null) {
            section.getKeys(false).forEach(identifier -> {
                final String serialized = section.getString(identifier + ".position");
                final Position position = Position.deserialize(jsonParser.parse(serialized));
                final Hologram helperHologram = this.hologramFactory.newHologram(position, section.getStringList(identifier + ".lines"));

                final CommonsHologram commonsHologram = new CommonsHologram(identifier, helperHologram);
                commonsHologram.setLines(section.getStringList(identifier + ".lines"));
                commonsHologram.setPosition(position);

                try (ServerThreadLock lock = ServerThreadLock.obtain()) {
                    commonsHologram.spawn();
                }

                this.hologramMap.put(identifier, commonsHologram);
            });
        }
    }

    public void saveAllSync() {
        final ScalaCommonsSpigot commonLibsBukkit = ScalaCommonsSpigot.getInstance();

        Preconditions.checkNotNull(this.hologramFactory, "hologramFactory");

        final FileConfiguration configuration = commonLibsBukkit.getConfig();

        this.hologramMap.forEach((identifier, commonsHologram) -> {
            configuration.set("holograms." + identifier + ".position", commonsHologram.getPosition().serialize().toString());
            configuration.set("holograms." + identifier + ".lines", commonsHologram.getLines());
        });

        commonLibsBukkit.saveConfig();
    }

    public CommonsHologram fetchHologram(String identifier) {
        Preconditions.checkNotNull(identifier, "identifier");
        Preconditions.checkNotNull(this.hologramFactory, "hologramFactory");

        return this.hologramMap.getOrDefault(identifier, null);
    }
}
