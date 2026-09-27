package no.monopixel.slimcolonies.core.network.messages.server.colony;

import com.ldtteam.common.network.PlayMessageType;
import no.monopixel.slimcolonies.api.colony.IColony;
import no.monopixel.slimcolonies.api.util.InventoryUtils;
import no.monopixel.slimcolonies.api.util.constant.Constants;
import no.monopixel.slimcolonies.core.network.messages.server.AbstractColonyServerMessage;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static no.monopixel.slimcolonies.core.colony.buildings.workerbuildings.BuildingBarracks.SPIES_GOLD_COST;

/**
 * Message for hiring spies at the cost of gold.
 */
public class HireSpiesMessage extends AbstractColonyServerMessage
{
    public static final PlayMessageType<?> TYPE = PlayMessageType.forServer(Constants.MOD_ID, "hire_spies", HireSpiesMessage::new);

    public HireSpiesMessage(final IColony colony)
    {
        super(TYPE, colony);
    }

    protected HireSpiesMessage(final RegistryFriendlyByteBuf buf, final PlayMessageType<?> type)
    {
        super(buf, type);
    }

    @Override
    protected void onExecute(final IPayloadContext ctxIn, final ServerPlayer player, final IColony colony)
    {
        if (InventoryUtils.getItemCountInItemHandler(new InvWrapper(player.getInventory()), stack -> stack.getItem() == Items.GOLD_INGOT) >= SPIES_GOLD_COST)
        {
            InventoryUtils.reduceStackInItemHandler(new InvWrapper(player.getInventory()), new ItemStack(Items.GOLD_INGOT), SPIES_GOLD_COST);
            colony.getRaiderManager().setSpiesEnabled(true);
            colony.markDirty();
        }
    }
}
