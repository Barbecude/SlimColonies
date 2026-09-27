package no.monopixel.slimcolonies.core.network.messages.server.colony.building.warehouse;

import com.ldtteam.common.network.PlayMessageType;
import no.monopixel.slimcolonies.api.colony.IColony;
import no.monopixel.slimcolonies.api.colony.buildings.IBuilding;
import no.monopixel.slimcolonies.api.colony.buildings.views.IBuildingView;
import no.monopixel.slimcolonies.api.inventory.api.CombinedItemHandler;
import no.monopixel.slimcolonies.api.util.constant.Constants;
import no.monopixel.slimcolonies.core.network.messages.server.AbstractBuildingServerMessage;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Sort the specified building inventory if level greater than or equal to requiredSortLevel.
 */
public class SortBuildingMessage extends AbstractBuildingServerMessage<IBuilding>
{
    public static final PlayMessageType<?> TYPE = PlayMessageType.forServer(Constants.MOD_ID, "sort_building_message", SortBuildingMessage::new);

    public SortBuildingMessage(final IBuildingView building)
    {
        super(TYPE, building);
    }

    protected SortBuildingMessage(final RegistryFriendlyByteBuf buf, final PlayMessageType<?> type)
    {
        super(buf, type);
    }

    /**
     * Sorts the item handler of the building if the building's level is bigger or equal to the required sort level.
     * 
     * @param ctxIn The context of the payload.
     * @param player The player that sent the message.
     * @param colony The colony that the building is in.
     * @param building The building that needs to be sorted.
     */
    @Override
    protected void onExecute(final IPayloadContext ctxIn, final ServerPlayer player, final IColony colony, final IBuilding building)
    {
        if (building.canSort())
        {
            if (building.getItemHandlerCap() instanceof final CombinedItemHandler combinedInv)
            {
                building.sort(player.level().registryAccess(), combinedInv);
            }
        }
    }
}
