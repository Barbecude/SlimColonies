package no.monopixel.slimcolonies.core.entity.ai.workers.guard;

import no.monopixel.slimcolonies.api.items.ModItems;
import no.monopixel.slimcolonies.api.util.BlockPosUtil;
import no.monopixel.slimcolonies.api.util.InventoryUtils;
import no.monopixel.slimcolonies.core.colony.buildings.AbstractBuildingGuards;
import no.monopixel.slimcolonies.core.colony.jobs.guard.JobDruid;
import no.monopixel.slimcolonies.core.entity.citizen.EntityCitizen;
import no.monopixel.slimcolonies.core.entity.pathfinding.navigation.MinecoloniesAdvancedPathNavigate;
import no.monopixel.slimcolonies.core.entity.pathfinding.pathjobs.PathJobWalkRandomEdge;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import static no.monopixel.slimcolonies.api.entity.ai.statemachine.states.AIWorkerState.IDLE;
import static no.monopixel.slimcolonies.api.research.util.ResearchConstants.DRUID_USE_POTIONS;

/**
 * Druid AI class, which deals with equipment and movement specifics
 */
@SuppressWarnings("squid:MaximumInheritanceDepth")
public class EntityAIDruid extends AbstractEntityAIGuard<JobDruid, AbstractBuildingGuards>
{
    /**
     * Potion meta data.
     */
    public static final String RENDER_META_POTION = "potion";

    public EntityAIDruid(@NotNull final JobDruid job)
    {
        super(job);
        new DruidCombatAI((EntityCitizen) worker, getStateAI(), this);
    }

    @Override
    protected void updateRenderMetaData()
    {
        String renderMeta = getState() == IDLE ? "" : RENDER_META_WORKING;
        if (worker.getCitizenInventoryHandler().hasItemInInventory(Items.POTION))
        {
            renderMeta += RENDER_META_POTION;
        }
        worker.setRenderMetadata(renderMeta);
    }

    @Override
    protected void atBuildingActions()
    {
        super.atBuildingActions();

        if (worker.getCitizenColonyHandler().getColonyOrRegister().getResearchManager().getResearchEffects().getEffectStrength(DRUID_USE_POTIONS) > 0)
        {
            // Mistletoes and water bottles
            InventoryUtils.transferXOfFirstSlotInProviderWithIntoNextFreeSlotInItemHandler(building,
              item -> item.getItem() == ModItems.magicpotion,
              32,
              worker.getInventoryCitizen());

            if (InventoryUtils.getItemCountInItemHandler(worker.getInventoryCitizen(), item -> item.getItem() == ModItems.magicpotion) < 8)
            {
                checkIfRequestForItemExistOrCreateAsync(new ItemStack(ModItems.magicpotion), 16, 8);
            }
        }
    }

    @Override
    public void guardMovement()
    {
        if (worker.getRandom().nextInt(3) < 1)
        {
            walkToSafePos(buildingGuards.getGuardPos(worker));
            return;
        }

        if ((BlockPosUtil.dist(buildingGuards.getGuardPos(worker), worker.blockPosition()) <= 10 || walkToSafePos(buildingGuards.getGuardPos(worker)))
            || Math.abs(buildingGuards.getGuardPos(worker).getY() - worker.blockPosition().getY()) > 3)
        {
            // Moves the druid randomly to close edges, for better vision to mobs
            ((MinecoloniesAdvancedPathNavigate) worker.getNavigation()).setPathJob(new PathJobWalkRandomEdge(world, buildingGuards.getGuardPos(worker), 20, worker),
                null,
                1.0, true);
        }
    }
}
