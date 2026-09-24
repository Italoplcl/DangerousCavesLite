package dev.caveslite;

import dev.caveslite.util.Locations;
import dev.caveslite.util.Rng;
import dev.caveslite.util.Sounds;
import dev.caveslite.util.WorldFilter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/** Plays eerie sounds near players who are deep underground. */
public final class AmbientSounds {
    private record WrappedSound(Sound sound, float volume, float pitch) {}

    private final Plugin plugin;
    private final List<WrappedSound> sounds = new ArrayList<>();
    private final WorldFilter worlds = new WorldFilter();

    private boolean enabled;
    private double chance;
    private int yMax;
    private double radius;
    private boolean serverWide;
    private double serverWideDistanceSquared;

    public AmbientSounds(Plugin plugin) {
        this.plugin = plugin;
    }

    public void reload(ConfigurationSection cfg) {
        enabled = cfg.getBoolean("enabled", true);
        chance = cfg.getDouble("chance", 25) / 100;
        yMax = cfg.getInt("y-max", 64);
        radius = cfg.getDouble("near", 7);
        serverWide = cfg.getBoolean("server-wise", true);
        double distance = cfg.getDouble("server-wise-distance", 0);
        serverWideDistanceSquared = distance * distance;
        worlds.reload(cfg.getStringList("worlds"));

        sounds.clear();
        ConfigurationSection soundsSection = cfg.getConfigurationSection("sounds");
        if (soundsSection != null) {
            for (String name : soundsSection.getKeys(false)) {
                Sound sound = Sounds.find(name);
                if (sound == null) {
                    plugin.getLogger().log(Level.WARNING, "Unknown ambient sound in config: {0}", name);
                    continue;
                }
                sounds.add(new WrappedSound(
                        sound,
                        (float) soundsSection.getDouble(name + ".volume", 1),
                        (float) soundsSection.getDouble(name + ".pitch", 0.5)
                ));
            }
        }
    }

    /** Called periodically by the plugin's scheduler. */
    public void tick() {
        if (!enabled || sounds.isEmpty()) return;

        // With "server-wise" the sound is heard by everyone nearby, so keep sources apart.
        List<Location> sources = serverWide && serverWideDistanceSquared > 0 ? new ArrayList<>() : null;

        for (World world : Bukkit.getWorlds()) {
            if (!worlds.allows(world)) continue;
            for (Player player : world.getPlayers()) {
                Location loc = player.getLocation();
                if (loc.getBlockY() > yMax || !Locations.isCave(loc) || !Rng.chance(chance)) continue;

                if (sources != null) {
                    boolean tooClose = false;
                    for (Location source : sources) {
                        if (source.distanceSquared(loc) <= serverWideDistanceSquared) {
                            tooClose = true;
                            break;
                        }
                    }
                    if (tooClose) continue;
                    sources.add(loc);
                }

                play(Rng.randomElement(sounds), player);
            }
        }
    }

    private void play(WrappedSound wrapped, Player player) {
        Location loc = player.getEyeLocation();
        if (radius > 0) {
            loc.add(Rng.nextDouble(-radius, radius), Rng.nextDouble(-radius, radius), Rng.nextDouble(-radius, radius));
        }
        if (serverWide) {
            loc.getWorld().playSound(loc, wrapped.sound(), SoundCategory.AMBIENT, wrapped.volume(), wrapped.pitch());
        } else {
            player.playSound(loc, wrapped.sound(), SoundCategory.AMBIENT, wrapped.volume(), wrapped.pitch());
        }
    }
}
