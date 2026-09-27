package no.monopixel.slimcolonies.core.colony.buildings.modules;

import no.monopixel.slimcolonies.api.colony.buildings.IBuilding;
import no.monopixel.slimcolonies.api.colony.buildings.IBuildingWorkerModule;
import no.monopixel.slimcolonies.api.colony.buildings.modules.*;
import no.monopixel.slimcolonies.api.colony.jobs.registry.JobEntry;
import no.monopixel.slimcolonies.api.entity.citizen.Skill;
import java.util.function.Function;

/**
 * Assignment module for crafting workers.
 */
public class CraftingWorkerBuildingModule extends WorkerBuildingModule implements IBuildingEventsModule, ITickingModule, IPersistentModule, IBuildingWorkerModule, ICreatesResolversModule
{
    /**
     * Skill influencing crafting behaviour.
     */
    private final Skill craftingSpeedSkill;
    private final Skill recipeImprovementSkill;

    public CraftingWorkerBuildingModule(final JobEntry entry,
      final Skill primary,
      final Skill secondary,
      final boolean canWorkingDuringRain,
      final Function<IBuilding, Integer> sizeLimit,
      final Skill craftingSpeedSkill,
      final Skill recipeImprovementSkill)
    {
        super(entry, primary, secondary, canWorkingDuringRain, sizeLimit);
        this.craftingSpeedSkill = craftingSpeedSkill;
        this.recipeImprovementSkill = recipeImprovementSkill;
    }

    public CraftingWorkerBuildingModule(final JobEntry entry,
      final Skill primary,
      final Skill secondary,
      final boolean canWorkingDuringRain,
      final Function<IBuilding, Integer> sizeLimit)
    {
        super(entry, primary, secondary, canWorkingDuringRain, sizeLimit);
        this.craftingSpeedSkill = primary;
        this.recipeImprovementSkill = secondary;
    }

    /**
     * Get the skill that improves the crafting speed.
     * @return the speed.
     */
    public Skill getCraftSpeedSkill()
    {
        return craftingSpeedSkill;
    }

    /**
     * Skill responsible for recipe improvement.
     * @return the skill.
     */
    public Skill getRecipeImprovementSkill()
    {
        return recipeImprovementSkill;
    }
}
