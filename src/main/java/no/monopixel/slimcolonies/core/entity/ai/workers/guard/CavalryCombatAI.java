package no.monopixel.slimcolonies.core.entity.ai.workers.guard;

import no.monopixel.slimcolonies.api.util.constant.Constants;
import no.monopixel.slimcolonies.core.colony.jobs.guard.JobCavalry;
import no.monopixel.slimcolonies.core.entity.citizen.EntityCitizen;
import no.monopixel.slimcolonies.api.entity.ai.statemachine.tickratestatemachine.ITickRateStateMachine;
import no.monopixel.slimcolonies.api.entity.citizen.VisibleCitizenStatus;
import no.monopixel.slimcolonies.api.equipment.registry.EquipmentTypeEntry;

import net.minecraft.resources.ResourceLocation;
import static no.monopixel.slimcolonies.api.util.constant.GuardConstants.CAVALRY_DAMAGE_MULTIPLIER;
import static no.monopixel.slimcolonies.api.util.constant.GuardConstants.CAVALRY_RANGE_MULTIPLIER;

public class CavalryCombatAI extends MeleeCombatAI
{
    /**
     * Combat icon
     */
    private final static VisibleCitizenStatus CAVALRY_COMBAT_ICON =
      new VisibleCitizenStatus(new ResourceLocation(Constants.MOD_ID, "textures/icons/work/cavalry_combat.png"), "no.monopixel.slimcolonies.gui.visiblestatus.cavalry_combat");


    public CavalryCombatAI(final EntityCitizen owner, final ITickRateStateMachine<?> stateMachine, final AbstractEntityAIGuard<?, ?> parentAI)
    {
        super(owner, stateMachine, parentAI);
    }

    @Override
    protected double getAttackDamage()
    {
        // TODO: Allow this to improve through research
        return super.getAttackDamage() * CAVALRY_DAMAGE_MULTIPLIER;
    }

    /**
     * Gets the weapon type that the AI will look for when checking if it can attack.
     *
     * @return the weapon type.
     */
    @Override
    public EquipmentTypeEntry getWeaponType()
    {
        return JobCavalry.getWeaponType();
    }


    /**
     * Get the attack distance for cavalry units.
     * 
     * @return the attack distance, increased by {@link #CAVALRY_RANGE_MULTIPLIER}.
     */
    protected double getAttackDistance()
    {
        return super.getAttackDistance() * CAVALRY_RANGE_MULTIPLIER;
    }

    /**
     * Get the icon to display when in combat.
     *
     * @return the icon.
     */
    @Override
    protected VisibleCitizenStatus getCombatStatus()
    {
        return CAVALRY_COMBAT_ICON;
    }

}