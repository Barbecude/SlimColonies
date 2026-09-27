package no.monopixel.slimcolonies.core.entity.mobs.aitasks;

import no.monopixel.slimcolonies.api.entity.ai.combat.threat.IThreatTableEntity;
import no.monopixel.slimcolonies.api.entity.ai.statemachine.states.IState;
import no.monopixel.slimcolonies.api.entity.ai.statemachine.tickratestatemachine.ITickRateStateMachine;
import no.monopixel.slimcolonies.api.entity.mobs.AbstractEntityMinecoloniesMonster;
import no.monopixel.slimcolonies.api.entity.mobs.ICustomAttackSound;
import no.monopixel.slimcolonies.api.entity.mobs.IRangedMobEntity;
import no.monopixel.slimcolonies.api.util.BlockPosUtil;
import no.monopixel.slimcolonies.api.util.EntityUtils;
import no.monopixel.slimcolonies.core.colony.events.raid.RaiderConstants;
import no.monopixel.slimcolonies.core.entity.ai.combat.AttackMoveAI;
import no.monopixel.slimcolonies.core.entity.ai.combat.CombatUtils;
import no.monopixel.slimcolonies.core.entity.citizen.EntityCitizen;
import no.monopixel.slimcolonies.core.entity.other.CustomArrowEntity;
import no.monopixel.slimcolonies.core.entity.pathfinding.navigation.EntityNavigationUtils;
import no.monopixel.slimcolonies.core.entity.pathfinding.pathresults.PathResult;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;

import static no.monopixel.slimcolonies.api.entity.mobs.RaiderMobUtils.MOB_ATTACK_DAMAGE;

/**
 * Raider AI for shooting arrows at a target
 */
public class RaiderRangedAI<T extends AbstractEntityMinecoloniesMonster & IThreatTableEntity & IRangedMobEntity> extends AttackMoveAI<T>
{
    /**
     * Max delay between attacks is 3s, aka 60 ticks.
     */
    private static final int ATTACK_DELAY = 60;

    /**
     * How many ticks we activate the bow before shooting
     */
    private static final int BOW_HOLDING_DELAY = 40;

    /**
     * Difficulty level at which arrows do pierce
     */
    private static final double ARROW_PIERCE_DIFFICULTY = 3.0d;

    /**
     * Movement speed
     */
    private static final double COMBAT_MOVEMENT_SPEED = 1.1;

    /**
     * Attack distance
     */
    private static final double MAX_ATTACK_DISTANCE = 20.0D;

    /**
     * Sound variance
     */
    private static final double PITCH_MULTIPLIER = 0.4;
    private static final double BASE_PITCH       = 0.8D;
    private static final double PITCH_DIVIDER = 1.0D;

    /**
     * Counter for flying time
     */
    private int flightCounter = 0;

    public RaiderRangedAI(
      final T owner,
      final ITickRateStateMachine<IState> stateMachine)
    {
        super(owner, stateMachine);
    }

    @Override
    protected boolean isInDistanceForAttack(final LivingEntity target)
    {
        if (EntityUtils.isFlying(target))
        {
            flightCounter++;
        }
        else
        {
            flightCounter = 0;
        }

        if (flightCounter > 5)
        {
            // Always allowed to try attacking flying targets
            return true;
        }

        return super.isInAttackDistance(target);
    }

    @Override
    protected void doAttack(final LivingEntity target)
    {
        user.getNavigation().stop();

        // Setup arrow
        AbstractArrow arrowEntity = CombatUtils.createArrowForShooter(user);
        if (this.user.penetrateFluids() && arrowEntity instanceof CustomArrowEntity customArrowEntity )
        {
            customArrowEntity.setWaterInertia(0.99f);
        }

        arrowEntity.setBaseDamage(user.getAttribute(MOB_ATTACK_DAMAGE).getValue());
        if (flightCounter > 5 && arrowEntity instanceof CustomArrowEntity)
        {
            ((CustomArrowEntity) arrowEntity).setPlayerArmorPierce();
            arrowEntity.setRemainingFireTicks(10 * 20);
            arrowEntity.setBaseDamage(10);
        }

        if (user.getDifficulty() > ARROW_PIERCE_DIFFICULTY)
        {
            arrowEntity.setPierceLevel((byte) 2);
        }

        // Shoot arrow
        CombatUtils.shootArrow(arrowEntity, target, 10.0f);

        // Visuals
        user.swing(InteractionHand.MAIN_HAND);
        user.stopUsingItem();
        SoundEvent attackSound = SoundEvents.SKELETON_SHOOT;
        if (arrowEntity instanceof ICustomAttackSound)
        {
            attackSound = ((ICustomAttackSound) arrowEntity).getAttackSound();
        }
        user.playSound(attackSound, (float) 1.0D, (float) getRandomPitch());
    }

    /**
     * Random pitch generator
     *
     * @return A random double to act as a pitch value
     */
    private double getRandomPitch()
    {
        return PITCH_DIVIDER / (user.getRandom().nextDouble() * PITCH_MULTIPLIER + BASE_PITCH);
    }

    @Override
    protected double getAttackDistance()
    {
        return MAX_ATTACK_DISTANCE * Math.max(user.getDifficulty(), 2.0d);
    }

    @Override
    protected int getAttackDelay()
    {
        if (flightCounter > 5)
        {
            return 10;
        }

        return ATTACK_DELAY;
    }

    @Override
    public boolean canAttack()
    {
        if (nextAttackTime - BOW_HOLDING_DELAY >= user.level().getGameTime() && !user.isUsingItem() && !user.getMainHandItem().isEmpty())
        {
            user.startUsingItem(InteractionHand.MAIN_HAND);
        }

        return true;
    }

    @Override
    protected boolean checkForTarget()
    {
        final boolean validTarget = super.checkForTarget();

        if (!validTarget && user.isUsingItem())
        {
            user.stopUsingItem();
        }

        return validTarget;
    }

    @Override
    protected PathResult moveInAttackPosition(final LivingEntity target)
    {
        EntityNavigationUtils.walkToPos(user, target.blockPosition(), (int) getAttackDistance(), false, COMBAT_MOVEMENT_SPEED);
        return user.getNavigation().getPathResult();
    }

    @Override
    protected boolean isAttackableTarget(final LivingEntity target)
    {
        return ((target instanceof EntityCitizen || target instanceof AbstractVillager || target instanceof IronGolem) && !target.isInvisible())
            || (target instanceof Player && !((Player) target).isCreative() && !target.isSpectator());
    }

    @Override
    protected boolean isWithinPersecutionDistance(final LivingEntity target)
    {
        return BlockPosUtil.getDistanceSquared(user.blockPosition(), target.blockPosition()) <= RaiderConstants.MAX_ARCHER_RAIDER_PERSECUTION_DISTANCE * RaiderConstants.MAX_ARCHER_RAIDER_PERSECUTION_DISTANCE;
    }
}
