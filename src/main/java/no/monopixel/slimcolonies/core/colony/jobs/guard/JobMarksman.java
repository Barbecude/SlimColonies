package no.monopixel.slimcolonies.core.colony.jobs.guard;

import no.monopixel.slimcolonies.api.colony.ICitizenData;
import no.monopixel.slimcolonies.api.equipment.ModEquipmentTypes;
import no.monopixel.slimcolonies.api.equipment.registry.EquipmentTypeEntry;

/**
 * The Markman's Job class
 *
 * @author Asherslab
 */
public class JobMarksman extends JobRanger
{
    /**
     * Initialize citizen data.
     *
     * @param entity the citizen data.
     */
    public JobMarksman(final ICitizenData entity)
    {
        super(entity);
    }

    @Override
    public EquipmentTypeEntry getEquipmentType()
    {
        return ModEquipmentTypes.crossbow.get();
    }
}
