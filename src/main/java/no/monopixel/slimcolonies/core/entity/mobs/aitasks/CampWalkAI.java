package no.monopixel.slimcolonies.core.entity.mobs.aitasks;

import no.monopixel.slimcolonies.api.entity.ai.IStateAI;
import no.monopixel.slimcolonies.api.entity.ai.combat.CombatAIStates;
import no.monopixel.slimcolonies.api.entity.ai.statemachine.states.IState;
import no.monopixel.slimcolonies.api.entity.ai.statemachine.tickratestatemachine.ITickRateStateMachine;
import no.monopixel.slimcolonies.api.entity.ai.statemachine.tickratestatemachine.TickingTransition;
import no.monopixel.slimcolonies.api.entity.mobs.AbstractEntityMinecoloniesMonster;
import no.monopixel.slimcolonies.api.entity.pathfinding.IPathJob;
import no.monopixel.slimcolonies.core.entity.pathfinding.navigation.EntityNavigationUtils;
import no.monopixel.slimcolonies.core.entity.pathfinding.pathresults.PathResult;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Tuple;

import static no.monopixel.slimcolonies.api.util.constant.Constants.TICKS_SECOND;

/**
 * AI for handling the raiders walking directions
 */
public class CampWalkAI implements IStateAI
{
    /**
     * The entity using this AI
     */
    private final AbstractEntityMinecoloniesMonster entity;

    /**
     * Random path result.
     */
    private PathResult<? extends IPathJob> randomPathResult;

    /**
     * Spawn center box cache.
     */
    private Tuple<BlockPos, BlockPos> spawnCenterBoxCache = null;

    public CampWalkAI(final AbstractEntityMinecoloniesMonster raider, final ITickRateStateMachine<IState> stateMachine)
    {
        this.entity = raider;
        stateMachine.addTransition(new TickingTransition<>(CombatAIStates.NO_TARGET, this::walk, () -> null, TICKS_SECOND * 30));
    }

    /**
     * Walk camp mob randomly
     */
    private boolean walk()
    {
        if (spawnCenterBoxCache == null)
        {
            final BlockPos startPos = entity.getSpawnPos() == null ? entity.blockPosition() : entity.getSpawnPos();
            spawnCenterBoxCache = new Tuple<>(startPos.offset(-10, -5, -10), startPos.offset(10, 5, 10));
        }

        EntityNavigationUtils.walkToRandomPosWithin(entity, 10, 0.6, spawnCenterBoxCache);
        return false;
    }
}
