package no.monopixel.slimcolonies.core.colony.jobs.guard;

import no.monopixel.slimcolonies.api.equipment.ModEquipmentTypes;
import no.monopixel.slimcolonies.api.equipment.registry.EquipmentTypeEntry;
import no.monopixel.slimcolonies.core.colony.jobs.AbstractJobGuard;
import net.minecraft.resources.ResourceLocation;
import no.monopixel.slimcolonies.api.client.render.modeltype.ModModelTypes;
import no.monopixel.slimcolonies.api.colony.ICitizenData;
import no.monopixel.slimcolonies.core.entity.ai.workers.guard.EntityAIRange;

/**
 * The Ranger's Job class
 *
 * @author Asherslab
 */
public class JobRanger extends AbstractJobGuard<JobRanger>
{
    /**
     * Initialize citizen data.
     *
     * @param entity the citizen data.
     */
    public JobRanger(final ICitizenData entity)
    {
        super(entity);
    }

    @Override
    public EntityAIRange generateGuardAI()
    {
        return new EntityAIRange(this);
    }

    @Override
    public ResourceLocation getModel()
    {
        return ModModelTypes.ARCHER_GUARD_ID;
    }

    /**
     * Equipment type of this guard.
     * @return the type.
     */
    public EquipmentTypeEntry getEquipmentType()
    {
        // Default bow.
        return ModEquipmentTypes.bow.get();
    }
}
