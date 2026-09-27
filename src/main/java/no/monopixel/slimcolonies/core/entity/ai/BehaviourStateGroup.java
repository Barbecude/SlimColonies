package no.monopixel.slimcolonies.core.entity.ai;

import no.monopixel.slimcolonies.api.entity.ai.statemachine.states.AIWorkerState;
import no.monopixel.slimcolonies.api.entity.ai.statemachine.states.IAIState;

import java.util.List;

/**
 * Class holding various groups of AI States which define a behaviour set
 */
public final class BehaviourStateGroup
{
    /**
     * States in which guards should switch to fighting when there is an enemy
     */
    public static final List<IAIState> GUARD_ABORT_AND_FIGHT = List.of(AIWorkerState.NEEDS_ITEM, AIWorkerState.INVENTORY_FULL);
}
