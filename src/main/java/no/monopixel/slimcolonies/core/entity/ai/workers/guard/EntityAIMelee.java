package no.monopixel.slimcolonies.core.entity.ai.workers.guard;

import no.monopixel.slimcolonies.api.crafting.ItemStorage;
import no.monopixel.slimcolonies.api.entity.ai.workers.util.GuardGear;
import no.monopixel.slimcolonies.api.equipment.ModEquipmentTypes;
import no.monopixel.slimcolonies.core.colony.buildings.AbstractBuildingGuards;
import no.monopixel.slimcolonies.core.colony.jobs.guard.JobHuscarl;
import no.monopixel.slimcolonies.core.colony.jobs.guard.JobKnight;
import no.monopixel.slimcolonies.core.entity.citizen.EntityCitizen;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static no.monopixel.slimcolonies.api.research.util.ResearchConstants.SHIELD_USAGE;
import static no.monopixel.slimcolonies.api.util.constant.EquipmentLevelConstants.TOOL_LEVEL_MAXIMUM;
import static no.monopixel.slimcolonies.api.util.constant.EquipmentLevelConstants.TOOL_LEVEL_WOOD_OR_GOLD;
import static no.monopixel.slimcolonies.api.util.constant.GuardConstants.SHIELD_BUILDING_LEVEL_RANGE;
import static no.monopixel.slimcolonies.api.util.constant.GuardConstants.SHIELD_LEVEL_RANGE;

/**
 * Knight AI, which deals with gear specifics
 */
@SuppressWarnings("squid:MaximumInheritanceDepth")
public class EntityAIMelee extends AbstractEntityAIGuard<JobKnight, AbstractBuildingGuards>
{
    public EntityAIMelee(@NotNull final JobKnight job)
    {
        super(job);
        super.registerTargets();

        if (job instanceof JobHuscarl)
        {
            toolsNeeded.add(ModEquipmentTypes.axe.get());
        }
        else
        {
            toolsNeeded.add(ModEquipmentTypes.sword.get());
        }

        for (final List<GuardGear> list : itemsNeeded)
        {
            list.add(new GuardGear(ModEquipmentTypes.shield.get(),
              EquipmentSlot.OFFHAND,
              TOOL_LEVEL_WOOD_OR_GOLD,
              TOOL_LEVEL_MAXIMUM,
              SHIELD_LEVEL_RANGE,
              SHIELD_BUILDING_LEVEL_RANGE));
        }

        new MeleeCombatAI((EntityCitizen) worker, getStateAI(), this);
    }

    @NotNull
    @Override
    protected List<ItemStorage> itemsNiceToHave()
    {
        final List<ItemStorage> list = super.itemsNiceToHave();
        if (worker.getCitizenColonyHandler().getColonyOrRegister().getResearchManager().getResearchEffects().getEffectStrength(SHIELD_USAGE) > 0)
        {
            list.add(new ItemStorage(Items.SHIELD, 1));
        }
        return list;
    }
}
