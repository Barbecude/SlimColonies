package no.monopixel.slimcolonies.core.entity.ai.workers.crafting;

import no.monopixel.slimcolonies.api.colony.requestsystem.request.IRequest;
import no.monopixel.slimcolonies.api.crafting.IRecipeStorage;
import no.monopixel.slimcolonies.api.util.StatsUtil;
import no.monopixel.slimcolonies.core.colony.buildings.workerbuildings.BuildingBlacksmith;
import no.monopixel.slimcolonies.core.colony.jobs.JobBlacksmith;
import org.jetbrains.annotations.NotNull;

import static no.monopixel.slimcolonies.api.util.constant.StatisticsConstants.ITEMS_CRAFTED_DETAIL;

/**
 * Crafts tools and armour.
 */
public class EntityAIWorkBlacksmith extends AbstractEntityAICrafting<JobBlacksmith, BuildingBlacksmith>
{
    /**
     * Initialize the blacksmith and add all his tasks.
     *
     * @param blacksmith the job he has.
     */
    public EntityAIWorkBlacksmith(@NotNull final JobBlacksmith blacksmith)
    {
        super(blacksmith);
    }

    @Override
    public Class<BuildingBlacksmith> getExpectedBuildingClass()
    {
        return BuildingBlacksmith.class;
    }

}
