package no.monopixel.slimcolonies.core.colony.events.raid;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import no.monopixel.slimcolonies.api.colony.IColony;
import no.monopixel.slimcolonies.api.colony.colonyEvents.EventStatus;
import no.monopixel.slimcolonies.api.util.BlockPosUtil;
import no.monopixel.slimcolonies.api.util.CompatibilityUtils;
import no.monopixel.slimcolonies.api.util.constant.Constants;
import no.monopixel.slimcolonies.core.datalistener.CustomRaidConfigListener;
import no.monopixel.slimcolonies.core.datalistener.CustomRaidConfigListener.RaidTypeDefinition;
import no.monopixel.slimcolonies.core.event.ModdedRaiderAIHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

import static no.monopixel.slimcolonies.core.colony.events.raid.RaiderConstants.WHOLE_CIRCLE;

/**
 * Custom raid event that spawns modded or vanilla entities configured in custom_raids.json
 * and marches them into the colony using dynamic AI injection.
 */
public class CustomModRaidEvent extends HordeRaidEvent
{
    public static final ResourceLocation CUSTOM_MOD_RAID_EVENT_TYPE_ID = new ResourceLocation(Constants.MOD_ID, "custom_mod_raid");

    public static final String TAG_CUSTOM_RAID_TYPE = "customRaidTypeId";
    public static final String TAG_RAIDER_ROLE = "slimcolonies:raider_role";
    public static final String TAG_COLONY_ID_KEY = "slimcolonies:colony_id";
    public static final String TAG_EVENT_ID_KEY = "slimcolonies:event_id";
    public static final String RAIDER_TAG = "slimcolonies_raider";

    private String customRaidTypeId = "undead_horde";
    private final Random random = new Random();

    public CustomModRaidEvent(final IColony colony)
    {
        super(colony);
    }

    public void setCustomRaidTypeId(final String customRaidTypeId)
    {
        if (customRaidTypeId != null && !customRaidTypeId.isBlank())
        {
            this.customRaidTypeId = customRaidTypeId;
        }
    }

    public String getCustomRaidTypeId()
    {
        return customRaidTypeId;
    }

    public RaidTypeDefinition getDefinition()
    {
        return CustomRaidConfigListener.getRaidType(customRaidTypeId);
    }

    @Override
    public ResourceLocation getEventTypeID()
    {
        return CUSTOM_MOD_RAID_EVENT_TYPE_ID;
    }

    @Override
    protected void updateRaidBar()
    {
        final RaidTypeDefinition def = getDefinition();
        raidBar.setColor(def.bossBarColor);
        super.updateRaidBar();
    }

    @Override
    protected MutableComponent getDisplayName()
    {
        return Component.literal(getDefinition().displayName);
    }

    @Override
    protected void spawnHorde(
        final BlockPos spawnPos,
        final IColony colony,
        final int id,
        final int numberOfBosses,
        final int numberOfArchers,
        final int numberOfRaiders)
    {
        final RaidTypeDefinition def = getDefinition();
        final EntityType<?> mountType = def.resolveMount();

        for (int i = 0; i < numberOfRaiders; i++)
        {
            spawnSingleModdedRaider(def.pickRandomNormal(random), "normal", mountType, spawnPos, colony, id, i);
        }
        for (int i = 0; i < numberOfArchers; i++)
        {
            spawnSingleModdedRaider(def.pickRandomArcher(random), "archer", mountType, spawnPos, colony, id, i);
        }
        for (int i = 0; i < numberOfBosses; i++)
        {
            spawnSingleModdedRaider(def.pickRandomBoss(random), "boss", mountType, spawnPos, colony, id, i);
        }
    }

    public static void spawnSingleModdedRaider(
        final EntityType<?> entityType,
        final String role,
        final EntityType<?> mountType,
        final BlockPos spawnLocation,
        final IColony colony,
        final int eventId,
        final int index)
    {
        final Level world = colony.getWorld();
        if (spawnLocation == null || entityType == null || world == null)
        {
            return;
        }

        final Entity entity = entityType.create(world);
        if (entity == null)
        {
            return;
        }

        final int dx = (index / 6) % 6;
        final int dz = index % 6;
        BlockPos spawnpos = BlockPosUtil.findAround(world, spawnLocation.offset(dx, 0, dz), 5, 5, BlockPosUtil.SOLID_AIR_POS_SELECTOR);
        if (spawnpos == null)
        {
            spawnpos = spawnLocation.above();
        }

        entity.absMoveTo(spawnpos.getX() + 0.5D, spawnpos.getY(), spawnpos.getZ() + 0.5D, (float) Mth.wrapDegrees(world.random.nextDouble() * WHOLE_CIRCLE), 0.0F);
        entity.addTag(RAIDER_TAG);
        entity.getPersistentData().putInt(TAG_COLONY_ID_KEY, colony.getID());
        entity.getPersistentData().putInt(TAG_EVENT_ID_KEY, eventId);
        entity.getPersistentData().putString(TAG_RAIDER_ROLE, role);

        if (entity instanceof Mob mob)
        {
            mob.setPersistenceRequired();
            if (world instanceof ServerLevelAccessor serverLevel)
            {
                try
                {
                    final DifficultyInstance diff = world.getCurrentDifficultyAt(spawnpos);
                    mob.finalizeSpawn(serverLevel, diff, MobSpawnType.EVENT, null);
                }
                catch (final Exception ignored)
                {
                }
            }

            final AttributeInstance followAttr = mob.getAttribute(Attributes.FOLLOW_RANGE);
            if (followAttr != null && followAttr.getBaseValue() < 48.0D)
            {
                followAttr.setBaseValue(48.0D);
            }

            ModdedRaiderAIHandler.injectRaiderGoals(mob, colony, eventId);
        }

        CompatibilityUtils.addEntity(world, entity);
        colony.getEventManager().registerEntity(entity, eventId);

        if (mountType != null && ("boss".equals(role) || world.random.nextFloat() < 0.25f))
        {
            final Entity mount = mountType.create(world);
            if (mount != null)
            {
                mount.absMoveTo(spawnpos.getX() + 0.5D, spawnpos.getY(), spawnpos.getZ() + 0.5D, entity.getYRot(), 0.0F);
                mount.addTag(RAIDER_TAG);
                mount.getPersistentData().putInt(TAG_COLONY_ID_KEY, colony.getID());
                mount.getPersistentData().putInt(TAG_EVENT_ID_KEY, eventId);
                if (mount instanceof Mob mountMob)
                {
                    mountMob.setPersistenceRequired();
                }
                CompatibilityUtils.addEntity(world, mount);
                entity.startRiding(mount, true);
            }
        }
    }

    @Override
    public void registerEntity(final Entity entity)
    {
        if (entity == null || !entity.isAlive())
        {
            if (entity != null)
            {
                entity.remove(Entity.RemovalReason.DISCARDED);
            }
            return;
        }

        if (boss.containsKey(entity) || archers.containsKey(entity) || normal.containsKey(entity))
        {
            return;
        }

        entity.addTag(RAIDER_TAG);
        entity.getPersistentData().putInt(TAG_COLONY_ID_KEY, getColony().getID());
        entity.getPersistentData().putInt(TAG_EVENT_ID_KEY, getID());

        final String role = entity.getPersistentData().getString(TAG_RAIDER_ROLE);
        if ("boss".equals(role) && boss.size() < horde.numberOfBosses)
        {
            boss.put(entity, entity.getUUID());
            return;
        }
        if ("archer".equals(role) && archers.size() < horde.numberOfArchers)
        {
            archers.put(entity, entity.getUUID());
            return;
        }
        if (normal.size() < horde.numberOfRaiders)
        {
            entity.getPersistentData().putString(TAG_RAIDER_ROLE, "normal");
            normal.put(entity, entity.getUUID());
            return;
        }
        if (archers.size() < horde.numberOfArchers)
        {
            entity.getPersistentData().putString(TAG_RAIDER_ROLE, "archer");
            archers.put(entity, entity.getUUID());
            return;
        }
        if (boss.size() < horde.numberOfBosses)
        {
            entity.getPersistentData().putString(TAG_RAIDER_ROLE, "boss");
            boss.put(entity, entity.getUUID());
            return;
        }

        entity.remove(Entity.RemovalReason.DISCARDED);
    }

    @Override
    public void onEntityDeath(final LivingEntity entity)
    {
        super.onEntityDeath(entity);

        boolean matched = false;
        if (boss.remove(entity) != null)
        {
            horde.numberOfBosses = Math.max(0, horde.numberOfBosses - 1);
            matched = true;
        }
        else if (archers.remove(entity) != null)
        {
            horde.numberOfArchers = Math.max(0, horde.numberOfArchers - 1);
            matched = true;
        }
        else if (normal.remove(entity) != null)
        {
            horde.numberOfRaiders = Math.max(0, horde.numberOfRaiders - 1);
            matched = true;
        }
        else
        {
            final String role = entity.getPersistentData().getString(TAG_RAIDER_ROLE);
            if ("boss".equals(role) && horde.numberOfBosses > 0)
            {
                horde.numberOfBosses--;
                matched = true;
            }
            else if ("archer".equals(role) && horde.numberOfArchers > 0)
            {
                horde.numberOfArchers--;
                matched = true;
            }
            else if (horde.numberOfRaiders > 0)
            {
                horde.numberOfRaiders--;
                matched = true;
            }
        }

        if (matched)
        {
            if (!(entity instanceof no.monopixel.slimcolonies.api.entity.mobs.AbstractEntityMinecoloniesRaider))
            {
                getColony().getRaiderManager().onRaiderDeath(null);
            }
            horde.hordeSize = Math.max(0, horde.numberOfBosses + horde.numberOfArchers + horde.numberOfRaiders);
            if (horde.hordeSize == 0)
            {
                status = EventStatus.DONE;
            }
            sendHordeMessage();
        }
    }

    @Override
    public EntityType<?> getNormalRaiderType()
    {
        return getDefinition().pickRandomNormal(random);
    }

    @Override
    public EntityType<?> getArcherRaiderType()
    {
        return getDefinition().pickRandomArcher(random);
    }

    @Override
    public EntityType<?> getBossRaiderType()
    {
        return getDefinition().pickRandomBoss(random);
    }

    @Override
    public CompoundTag serializeNBT(@NotNull final HolderLookup.Provider provider)
    {
        final CompoundTag compound = super.serializeNBT(provider);
        compound.putString(TAG_CUSTOM_RAID_TYPE, customRaidTypeId);
        return compound;
    }

    @Override
    public void deserializeNBT(@NotNull final HolderLookup.Provider provider, final CompoundTag compound)
    {
        super.deserializeNBT(provider, compound);
        if (compound.contains(TAG_CUSTOM_RAID_TYPE))
        {
            customRaidTypeId = compound.getString(TAG_CUSTOM_RAID_TYPE);
        }
    }

    public static CustomModRaidEvent loadFromNBT(final IColony colony, final CompoundTag compound, @NotNull final HolderLookup.Provider provider)
    {
        final CustomModRaidEvent event = new CustomModRaidEvent(colony);
        event.deserializeNBT(provider, compound);
        return event;
    }
}
