package dev.caveslite.mobs;

import dev.caveslite.util.TagHelper;
import dev.caveslite.util.Text;
import dev.caveslite.util.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import java.util.Locale;

public abstract class MobBase implements CustomMob {
    private final EntityType type;
    private final String id;
    private final String scoreboardTag;
    private final int defWeight;
    private final Double defHealth;
    private final String defName;

    private int weight;
    protected Component name;
    protected Double health;

    protected MobBase(EntityType type, String id, int weight, Double health) {
        this(type, id, weight, health, "&4" + Utils.capitalize(id.replace('-', ' ')));
    }

    protected MobBase(EntityType type, String id, int weight, Double health, String name) {
        this.type = type;
        this.id = id.toLowerCase(Locale.ROOT);
        this.scoreboardTag = TagHelper.mobScoreboardTag(this.id);
        this.defWeight = weight;
        this.defHealth = health;
        this.defName = name;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public int weight() {
        return weight;
    }

    @Override
    public EntityType type() {
        return type;
    }

    @Override
    public boolean isThis(Entity entity) {
        return entity instanceof LivingEntity && entity.getScoreboardTags().contains(scoreboardTag);
    }

    @Override
    public void reload(ConfigurationSection cfg) {
        weight = cfg.getInt("priority", defWeight);

        String configuredName = cfg.getString("name", defName);
        name = configuredName == null || configuredName.isEmpty() ? null : Text.legacy(configuredName);

        // YAML "health: 20" is an Integer, so accept any number.
        health = cfg.get("health") instanceof Number number
                ? Double.valueOf(Math.max(number.doubleValue(), 1))
                : defHealth;

        configure(cfg);
    }

    protected abstract void configure(ConfigurationSection cfg);

    /** Equipment, potion effects and so on. Called right after spawning. */
    protected void prepare(LivingEntity entity) {
    }

    @Override
    public LivingEntity spawn(Location loc) {
        LivingEntity entity = (LivingEntity) loc.getWorld().spawnEntity(loc, type);
        TagHelper.setTag(entity, id);
        entity.customName(name);
        if (health != null) {
            Utils.setMaxHealth(entity, health);
        }
        prepare(entity);
        return entity;
    }
}
