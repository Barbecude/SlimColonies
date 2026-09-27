package no.monopixel.slimcolonies.core.event;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import no.monopixel.slimcolonies.api.colony.IColony;
import no.monopixel.slimcolonies.api.colony.IColonyManager;
import no.monopixel.slimcolonies.api.colony.colonyEvents.EventStatus;
import no.monopixel.slimcolonies.api.colony.colonyEvents.IColonyCampFireRaidEvent;
import no.monopixel.slimcolonies.api.colony.colonyEvents.IColonyEvent;
import no.monopixel.slimcolonies.api.colony.colonyEvents.IColonyRaidEvent;
import no.monopixel.slimcolonies.api.entity.mobs.AbstractEntityMinecoloniesRaider;
import no.monopixel.slimcolonies.core.colony.events.raid.CustomModRaidEvent;
import no.monopixel.slimcolonies.core.colony.events.raid.HordeRaidEvent;
import no.monopixel.slimcolonies.core.colony.events.raid.pirateEvent.ShipBasedRaiderUtils;
import no.monopixel.slimcolonies.core.entity.citizen.EntityCitizen;
import no.monopixel.slimcolonies.core.entity.mobs.EntityMercenary;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.List;

/**
 * Dynamically injects colony raid pathfinding and target goals into any modded or vanilla entity
 * spawned by CustomModRaidEvent, and synchronizes entity lifecycle events with Colony EventManager.
 */
public class ModdedRaiderAIHandler
{
    public static void injectRaiderGoals(@NotNull final Mob mob, @NotNull final IColony colony, final int eventId)
    {
        if (mob instanceof AbstractEntityMinecoloniesRaider)
        {
            return;
        }

        final boolean alreadyInjected = mob.goalSelector.getAvailableGoals()
            .stream()
            .map(WrappedGoal::getGoal)
            .anyMatch(g -> g instanceof ModdedRaiderMarchGoal);

        if (alreadyInjected)
        {
            return;
        }

        mob.goalSelector.addGoal(3, new ModdedRaiderMarchGoal(mob, colony.getID(), eventId));

        if (mob instanceof PathfinderMob pathfinderMob)
        {
            final boolean hasAttackGoal = mob.goalSelector.getAvailableGoals()
                .stream()
                .map(WrappedGoal::getGoal)
                .anyMatch(g -> g.getFlags().contains(Goal.Flag.LOOK) && g.getFlags().contains(Goal.Flag.MOVE));
            if (!hasAttackGoal)
            {
                mob.goalSelector.addGoal(2, new MeleeAttackGoal(pathfinderMob, 1.2D, false));
            }
            mob.targetSelector.addGoal(1, new HurtByTargetGoal(pathfinderMob));
        }

        mob.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(mob, EntityCitizen.class, true, citizen -> !citizen.isInvisible()));
        mob.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(mob, EntityMercenary.class, true));
        mob.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(mob, Player.class, true, player -> !(player instanceof Player p && p.isCreative()) && !player.isSpectator()));
    }

    @SubscribeEvent
    public static void onEntityJoinLevel(@NotNull final EntityJoinLevelEvent event)
    {
        if (event.getLevel().isClientSide() || !(event.getLevel() instanceof Level level))
        {
            return;
        }

        final Entity entity = event.getEntity();
        if (entity instanceof AbstractEntityMinecoloniesRaider)
        {
            return;
        }

        if (!entity.getPersistentData().contains(CustomModRaidEvent.TAG_COLONY_ID_KEY))
        {
            return;
        }

        final int colonyId = entity.getPersistentData().getInt(CustomModRaidEvent.TAG_COLONY_ID_KEY);
        final int eventId = entity.getPersistentData().getInt(CustomModRaidEvent.TAG_EVENT_ID_KEY);
        if (colonyId <= 0 || eventId <= 0)
        {
            return;
        }

        final IColony colony = IColonyManager.getInstance().getColonyByWorld(colonyId, level);
        if (colony == null || colony.getEventManager().getEventByID(eventId) == null)
        {
            entity.remove(Entity.RemovalReason.DISCARDED);
            return;
        }

        entity.addTag(CustomModRaidEvent.RAIDER_TAG);
        colony.getEventManager().registerEntity(entity, eventId);

        if (entity instanceof Mob mob)
        {
            injectRaiderGoals(mob, colony, eventId);
        }
    }

    @SubscribeEvent
    public static void onLivingDeath(@NotNull final LivingDeathEvent event)
    {
        final LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide() || entity instanceof AbstractEntityMinecoloniesRaider)
        {
            return;
        }

        if (!entity.getPersistentData().contains(CustomModRaidEvent.TAG_COLONY_ID_KEY))
        {
            return;
        }

        final int colonyId = entity.getPersistentData().getInt(CustomModRaidEvent.TAG_COLONY_ID_KEY);
        final int eventId = entity.getPersistentData().getInt(CustomModRaidEvent.TAG_EVENT_ID_KEY);
        final IColony colony = IColonyManager.getInstance().getColonyByWorld(colonyId, entity.level());
        if (colony != null && eventId > 0)
        {
            colony.getEventManager().onEntityDeath(entity, eventId);
        }
    }

    @SubscribeEvent
    public static void onEntityLeaveLevel(@NotNull final EntityLeaveLevelEvent event)
    {
        if (event.getLevel().isClientSide() || !(event.getLevel() instanceof Level level))
        {
            return;
        }

        final Entity entity = event.getEntity();
        if (!entity.isAlive() || entity instanceof AbstractEntityMinecoloniesRaider)
        {
            return;
        }

        if (!entity.getPersistentData().contains(CustomModRaidEvent.TAG_COLONY_ID_KEY))
        {
            return;
        }

        final Entity.RemovalReason reason = entity.getRemovalReason();
        if (reason == Entity.RemovalReason.DISCARDED || reason == Entity.RemovalReason.KILLED)
        {
            return;
        }

        final int colonyId = entity.getPersistentData().getInt(CustomModRaidEvent.TAG_COLONY_ID_KEY);
        final int eventId = entity.getPersistentData().getInt(CustomModRaidEvent.TAG_EVENT_ID_KEY);
        final IColony colony = IColonyManager.getInstance().getColonyByWorld(colonyId, level);
        if (colony != null && eventId > 0)
        {
            colony.getEventManager().unregisterEntity(entity, eventId);
        }
    }

    @SubscribeEvent
    public static void onRaiderDamaged(@NotNull final LivingIncomingDamageEvent event)
    {
        final LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide() || entity instanceof AbstractEntityMinecoloniesRaider)
        {
            return;
        }

        if (!entity.getPersistentData().contains(CustomModRaidEvent.TAG_COLONY_ID_KEY))
        {
            return;
        }

        if (event.getSource().getEntity() instanceof LivingEntity)
        {
            final int colonyId = entity.getPersistentData().getInt(CustomModRaidEvent.TAG_COLONY_ID_KEY);
            final int eventId = entity.getPersistentData().getInt(CustomModRaidEvent.TAG_EVENT_ID_KEY);
            final IColony colony = IColonyManager.getInstance().getColonyByWorld(colonyId, entity.level());
            if (colony != null)
            {
                final IColonyEvent raidEvent = colony.getEventManager().getEventByID(eventId);
                if (raidEvent instanceof IColonyCampFireRaidEvent campFireRaidEvent)
                {
                    campFireRaidEvent.setCampFireTime(0);
                }
            }
        }
    }

    /**
     * Goal that guides modded/vanilla entities along the colony raid waypoints into the colony buildings.
     */
    public static class ModdedRaiderMarchGoal extends Goal
    {
        private final Mob mob;
        private final int colonyId;
        private final int eventId;
        private BlockPos targetBuilding = null;
        private long nextRetargetTick = 0;
        private int repathCooldown = 0;

        public ModdedRaiderMarchGoal(final Mob mob, final int colonyId, final int eventId)
        {
            this.mob = mob;
            this.colonyId = colonyId;
            this.eventId = eventId;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse()
        {
            return mob.isAlive() && (mob.getTarget() == null || !mob.getTarget().isAlive());
        }

        @Override
        public boolean canContinueToUse()
        {
            return canUse();
        }

        @Override
        public void tick()
        {
            if (repathCooldown > 0)
            {
                repathCooldown--;
                return;
            }
            repathCooldown = 30;

            final IColony colony = IColonyManager.getInstance().getColonyByWorld(colonyId, mob.level());
            if (colony == null)
            {
                return;
            }

            final IColonyEvent event = colony.getEventManager().getEventByID(eventId);
            if (!(event instanceof IColonyRaidEvent raidEvent))
            {
                return;
            }

            if (event.getStatus() == EventStatus.PREPARING && event instanceof HordeRaidEvent hordeRaidEvent)
            {
                final BlockPos campfire = hordeRaidEvent.getRandomCampfire();
                if (campfire != null && mob.blockPosition().distSqr(campfire) > 36)
                {
                    mob.getNavigation().moveTo(campfire.getX() + 0.5D, campfire.getY(), campfire.getZ() + 0.5D, 1.0D);
                }
                return;
            }

            final long gameTime = mob.level().getGameTime();
            if (targetBuilding == null || gameTime > nextRetargetTick || mob.blockPosition().distSqr(targetBuilding) < 36)
            {
                targetBuilding = colony.getRaiderManager().getRandomBuilding();
                nextRetargetTick = gameTime + 20 * 45;
            }

            if (targetBuilding != null)
            {
                final List<BlockPos> wayPoints = raidEvent.getWayPoints();
                final BlockPos nextWaypoint = (wayPoints != null && !wayPoints.isEmpty())
                    ? ShipBasedRaiderUtils.chooseWaypointFor(wayPoints, mob.blockPosition(), targetBuilding)
                    : targetBuilding;
                final BlockPos dest = (nextWaypoint == null || nextWaypoint.equals(BlockPos.ZERO)) ? targetBuilding : nextWaypoint;
                mob.getNavigation().moveTo(dest.getX() + 0.5D, dest.getY(), dest.getZ() + 0.5D, 1.15D);
            }
        }
    }
}
