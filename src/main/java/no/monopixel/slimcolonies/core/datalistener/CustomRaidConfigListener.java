package no.monopixel.slimcolonies.core.datalistener;

import com.google.gson.*;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.Biome;
import no.monopixel.slimcolonies.api.colony.IColony;
import no.monopixel.slimcolonies.api.util.Log;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Loads custom modded/vanilla raid definitions from data/slimcolonies/raids/custom_raids.json.
 */
public class CustomRaidConfigListener extends SimpleJsonResourceReloadListener
{
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private static boolean enabled = true;
    private static final Map<String, RaidTypeDefinition> RAID_TYPES = new LinkedHashMap<>();

    public static class RaidTypeDefinition
    {
        public final String id;
        public final String displayName;
        public final BossEvent.BossBarColor bossBarColor;
        public final int weight;
        public final List<String> biomes;
        public final int minRaidLevel;
        public final List<String> normalEntities;
        public final List<String> archerEntities;
        public final List<String> bossEntities;
        public final String mountEntity;

        public RaidTypeDefinition(
            final String id,
            final String displayName,
            final BossEvent.BossBarColor bossBarColor,
            final int weight,
            final List<String> biomes,
            final int minRaidLevel,
            final List<String> normalEntities,
            final List<String> archerEntities,
            final List<String> bossEntities,
            final String mountEntity)
        {
            this.id = id;
            this.displayName = displayName;
            this.bossBarColor = bossBarColor;
            this.weight = Math.max(1, weight);
            this.biomes = biomes;
            this.minRaidLevel = minRaidLevel;
            this.normalEntities = normalEntities;
            this.archerEntities = archerEntities;
            this.bossEntities = bossEntities;
            this.mountEntity = mountEntity == null ? "" : mountEntity;
        }

        public EntityType<?> pickRandomNormal(final Random random)
        {
            return resolveRandomEntity(normalEntities, random, EntityType.ZOMBIE);
        }

        public EntityType<?> pickRandomArcher(final Random random)
        {
            return resolveRandomEntity(archerEntities, random, EntityType.SKELETON);
        }

        public EntityType<?> pickRandomBoss(final Random random)
        {
            return resolveRandomEntity(bossEntities, random, EntityType.VINDICATOR);
        }

        @Nullable
        public EntityType<?> resolveMount()
        {
            if (mountEntity == null || mountEntity.isBlank())
            {
                return null;
            }
            try
            {
                final ResourceLocation rl = ResourceLocation.parse(mountEntity.trim());
                if (BuiltInRegistries.ENTITY_TYPE.containsKey(rl))
                {
                    return BuiltInRegistries.ENTITY_TYPE.get(rl);
                }
            }
            catch (final Exception ignored)
            {
            }
            return null;
        }

        private static EntityType<?> resolveRandomEntity(final List<String> ids, final Random random, final EntityType<?> fallback)
        {
            if (ids == null || ids.isEmpty())
            {
                return fallback;
            }
            final List<EntityType<?>> valid = new ArrayList<>();
            for (final String rawId : ids)
            {
                if (rawId == null || rawId.isBlank())
                {
                    continue;
                }
                try
                {
                    final ResourceLocation rl = ResourceLocation.parse(rawId.trim());
                    if (BuiltInRegistries.ENTITY_TYPE.containsKey(rl))
                    {
                        valid.add(BuiltInRegistries.ENTITY_TYPE.get(rl));
                    }
                    else
                    {
                        Log.getLogger().warn("SlimColonies Custom Raid: Entity ID '{}' not found in registry, skipping.", rawId);
                    }
                }
                catch (final Exception e)
                {
                    Log.getLogger().warn("SlimColonies Custom Raid: Invalid entity ID '{}'", rawId);
                }
            }
            if (valid.isEmpty())
            {
                return fallback;
            }
            return valid.get(random.nextInt(valid.size()));
        }
    }

    public CustomRaidConfigListener()
    {
        super(GSON, "raids");
    }

    @Override
    protected void apply(final Map<ResourceLocation, JsonElement> jsonElementMap, final @NotNull ResourceManager resourceManager, final @NotNull ProfilerFiller profiler)
    {
        RAID_TYPES.clear();
        enabled = true;

        for (final Map.Entry<ResourceLocation, JsonElement> entry : jsonElementMap.entrySet())
        {
            if (entry.getValue().isJsonObject())
            {
                parseJson(entry.getValue().getAsJsonObject());
            }
        }

        if (RAID_TYPES.isEmpty())
        {
            ensureLoaded();
        }
    }

    public static void ensureLoaded()
    {
        if (!RAID_TYPES.isEmpty())
        {
            return;
        }
        try (InputStream is = CustomRaidConfigListener.class.getResourceAsStream("/data/slimcolonies/raids/custom_raids.json"))
        {
            if (is != null)
            {
                final JsonObject root = GSON.fromJson(new InputStreamReader(is, StandardCharsets.UTF_8), JsonObject.class);
                parseJson(root);
            }
        }
        catch (final Exception e)
        {
            Log.getLogger().warn("Failed to load fallback custom_raids.json from classpath", e);
        }

        if (RAID_TYPES.isEmpty())
        {
            final RaidTypeDefinition fallback = new RaidTypeDefinition(
                "undead_horde",
                "Serangan Pasukan Undead",
                BossEvent.BossBarColor.PURPLE,
                10,
                Collections.emptyList(),
                0,
                List.of("minecraft:zombie", "minecraft:husk"),
                List.of("minecraft:skeleton", "minecraft:stray"),
                List.of("minecraft:vindicator"),
                ""
            );
            RAID_TYPES.put(fallback.id, fallback);
        }
    }

    private static void parseJson(final JsonObject root)
    {
        if (root == null)
        {
            return;
        }
        if (root.has("enabled"))
        {
            enabled = root.get("enabled").getAsBoolean();
        }
        if (root.has("raidTypes") && root.get("raidTypes").isJsonArray())
        {
            for (final JsonElement el : root.getAsJsonArray("raidTypes"))
            {
                if (!el.isJsonObject())
                {
                    continue;
                }
                final JsonObject obj = el.getAsJsonObject();
                final String id = obj.has("id") ? obj.get("id").getAsString() : "custom_raid_" + RAID_TYPES.size();
                final String displayName = obj.has("displayName") ? obj.get("displayName").getAsString() : "Serangan Koloni";
                BossEvent.BossBarColor color = BossEvent.BossBarColor.RED;
                if (obj.has("bossBarColor"))
                {
                    try
                    {
                        color = BossEvent.BossBarColor.valueOf(obj.get("bossBarColor").getAsString().toUpperCase(Locale.ROOT));
                    }
                    catch (final Exception ignored)
                    {
                    }
                }
                final int weight = obj.has("weight") ? obj.get("weight").getAsInt() : 10;
                final int minRaidLevel = obj.has("minRaidLevel") ? obj.get("minRaidLevel").getAsInt() : 0;
                final String mountEntity = obj.has("mountEntity") ? obj.get("mountEntity").getAsString() : "";

                final List<String> biomes = readStringList(obj, "biomes");
                final List<String> normal = readStringList(obj, "normalEntities");
                final List<String> archer = readStringList(obj, "archerEntities");
                final List<String> boss = readStringList(obj, "bossEntities");

                RAID_TYPES.put(id, new RaidTypeDefinition(id, displayName, color, weight, biomes, minRaidLevel, normal, archer, boss, mountEntity));
            }
        }
    }

    private static List<String> readStringList(final JsonObject obj, final String key)
    {
        final List<String> result = new ArrayList<>();
        if (obj.has(key) && obj.get(key).isJsonArray())
        {
            for (final JsonElement el : obj.getAsJsonArray(key))
            {
                if (el.isJsonPrimitive())
                {
                    result.add(el.getAsString());
                }
            }
        }
        return result;
    }

    public static boolean isEnabled()
    {
        ensureLoaded();
        return enabled;
    }

    @NotNull
    public static RaidTypeDefinition getRaidType(@Nullable final String id)
    {
        ensureLoaded();
        if (id != null && RAID_TYPES.containsKey(id))
        {
            return RAID_TYPES.get(id);
        }
        return RAID_TYPES.values().iterator().next();
    }

    @NotNull
    public static RaidTypeDefinition selectRaidType(@NotNull final IColony colony, @Nullable final String explicitType)
    {
        ensureLoaded();
        if (explicitType != null && !explicitType.isBlank() && RAID_TYPES.containsKey(explicitType))
        {
            return RAID_TYPES.get(explicitType);
        }

        final int raidLevel = colony.getRaiderManager().getColonyRaidLevel();
        final Holder<Biome> biomeHolder = colony.getWorld() != null ? colony.getWorld().getBiome(colony.getCenter()) : null;
        final String biomeId = biomeHolder != null && biomeHolder.unwrapKey().isPresent()
            ? biomeHolder.unwrapKey().get().location().toString()
            : "";

        final List<RaidTypeDefinition> candidates = new ArrayList<>();
        int totalWeight = 0;
        for (final RaidTypeDefinition def : RAID_TYPES.values())
        {
            if (raidLevel < def.minRaidLevel)
            {
                continue;
            }
            if (!def.biomes.isEmpty() && !biomeId.isEmpty() && !def.biomes.contains(biomeId))
            {
                continue;
            }
            candidates.add(def);
            totalWeight += def.weight;
        }

        if (candidates.isEmpty())
        {
            for (final RaidTypeDefinition def : RAID_TYPES.values())
            {
                candidates.add(def);
                totalWeight += def.weight;
            }
        }

        if (candidates.isEmpty())
        {
            return getRaidType(null);
        }

        int roll = (colony.getWorld() != null ? colony.getWorld().random.nextInt(Math.max(1, totalWeight)) : new Random().nextInt(Math.max(1, totalWeight)));
        for (final RaidTypeDefinition def : candidates)
        {
            roll -= def.weight;
            if (roll < 0)
            {
                return def;
            }
        }
        return candidates.get(0);
    }

    public static Collection<RaidTypeDefinition> getAllRaidTypes()
    {
        ensureLoaded();
        return Collections.unmodifiableCollection(RAID_TYPES.values());
    }
}
