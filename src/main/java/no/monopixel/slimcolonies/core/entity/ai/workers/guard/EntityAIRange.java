package no.monopixel.slimcolonies.core.entity.ai.workers.guard;

import no.monopixel.slimcolonies.api.util.BlockPosUtil;
import no.monopixel.slimcolonies.api.util.InventoryUtils;
import no.monopixel.slimcolonies.core.colony.buildings.AbstractBuildingGuards;
import no.monopixel.slimcolonies.core.colony.jobs.guard.JobRanger;
import no.monopixel.slimcolonies.core.entity.citizen.EntityCitizen;
import no.monopixel.slimcolonies.core.entity.pathfinding.navigation.MinecoloniesAdvancedPathNavigate;
import no.monopixel.slimcolonies.core.entity.pathfinding.pathjobs.PathJobWalkRandomEdge;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import static no.monopixel.slimcolonies.api.entity.ai.statemachine.states.AIWorkerState.IDLE;
import static no.monopixel.slimcolonies.api.research.util.ResearchConstants.ARCHER_USE_ARROWS;

/**
 * Ranger AI class, which deals with equipment and movement specifics
 */
@SuppressWarnings("squid:MaximumInheritanceDepth")
public class EntityAIRange extends AbstractEntityAIGuard<JobRanger, AbstractBuildingGuards>
{
    public static final String RENDER_META_ARROW = "arrow";

    public EntityAIRange(@NotNull final JobRanger job)
    {
        super(job);
        toolsNeeded.add(job.getEquipmentType());
        new RangeCombatAI((EntityCitizen) worker, getStateAI(), this);
    }

    @Override
    protected void updateRenderMetaData()
    {
        String renderMeta = getState() == IDLE ? "" : RENDER_META_WORKING;
        if (worker.getCitizenInventoryHandler().hasItemInInventory(Items.ARROW))
        {
            renderMeta += RENDER_META_ARROW;
        }
        worker.setRenderMetadata(renderMeta);
    }

    @Override
    protected void atBuildingActions()
    {
        super.atBuildingActions();

        if (worker.getCitizenColonyHandler().getColonyOrRegister().getResearchManager().getResearchEffects().getEffectStrength(ARCHER_USE_ARROWS) > 0)
        {
            // Pickup arrows and request arrows
            InventoryUtils.transferXOfFirstSlotInProviderWithIntoNextFreeSlotInItemHandler(building,
              item -> item.getItem() instanceof ArrowItem,
              64,
              worker.getInventoryCitizen());

            if (InventoryUtils.getItemCountInItemHandler(worker.getInventoryCitizen(), item -> item.getItem() instanceof ArrowItem) < 16)
            {
                checkIfRequestForItemExistOrCreateAsync(new ItemStack(Items.ARROW), 64, 16);
            }
        }
    }

    @Override
    public void guardMovement()
    {
        if (worker.getRandom().nextInt(30) < 1)
        {
            walkToUnSafePos(buildingGuards.getGuardPos(worker), 5);
            return;
        }

        if (!worker.getNavigation().isDone())
        {
            return;
        }

        final BlockPos guardPos = buildingGuards.getGuardPos(worker);
        if ((BlockPosUtil.dist(guardPos, worker.blockPosition()) <= 10 || walkToSafePos(guardPos)))
        {
            // Moves the ranger randomly to close edges, for better vision to mobs
             ((MinecoloniesAdvancedPathNavigate) worker.getNavigation()).setPathJob(
                 new PathJobWalkRandomEdge(world, guardPos, 10, worker), null, 1.0, true);
        }
    }
}
