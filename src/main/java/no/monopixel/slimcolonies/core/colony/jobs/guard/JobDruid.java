package no.monopixel.slimcolonies.core.colony.jobs.guard;

import no.monopixel.slimcolonies.api.client.render.modeltype.ModModelTypes;
import no.monopixel.slimcolonies.api.colony.ICitizenData;
import no.monopixel.slimcolonies.api.entity.citizen.AbstractEntityCitizen;
import no.monopixel.slimcolonies.api.entity.citizen.Skill;
import no.monopixel.slimcolonies.core.colony.jobs.AbstractJobGuard;
import no.monopixel.slimcolonies.core.entity.ai.workers.guard.EntityAIDruid;
import no.monopixel.slimcolonies.core.util.AttributeModifierUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import static no.monopixel.slimcolonies.api.util.constant.CitizenConstants.GUARD_HEALTH_MOD_LEVEL_NAME;
import static no.monopixel.slimcolonies.api.util.constant.GuardConstants.DRUID_HP_BONUS;

/**
 * The Druid's Job class
 *
 */
public class JobDruid extends AbstractJobGuard<JobDruid>
{
    /**
     * Initialize citizen data.
     *
     * @param entity the citizen data.
     */
    public JobDruid(final ICitizenData entity)
    {
        super(entity);
    }

    @Override
    public EntityAIDruid generateGuardAI()
    {
        return new EntityAIDruid(this);
    }

    @Override
    public void onLevelUp()
    {
        // Bonus Health for druids(gets reset upon Firing)
        if (getCitizen().getEntity().isPresent())
        {
            final AbstractEntityCitizen citizen = getCitizen().getEntity().get();

            // +1 Heart every 4 level
            final AttributeModifier healthModLevel =
              new AttributeModifier(GUARD_HEALTH_MOD_LEVEL_NAME,
                getCitizen().getCitizenSkillHandler().getLevel(Skill.Mana) / 2.0 + DRUID_HP_BONUS,
                AttributeModifier.Operation.ADD_VALUE);
            AttributeModifierUtils.addHealthModifier(citizen, healthModLevel);
        }
    }

    @Override
    public ResourceLocation getModel()
    {
        return ModModelTypes.DRUID_ID;
    }
}
