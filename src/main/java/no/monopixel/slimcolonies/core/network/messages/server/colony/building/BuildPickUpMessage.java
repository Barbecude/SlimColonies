package no.monopixel.slimcolonies.core.network.messages.server.colony.building;

import com.ldtteam.common.network.PlayMessageType;
import no.monopixel.slimcolonies.api.colony.IColony;
import no.monopixel.slimcolonies.api.colony.buildings.IBuilding;
import no.monopixel.slimcolonies.api.colony.buildings.views.IBuildingView;
import no.monopixel.slimcolonies.api.util.constant.Constants;
import no.monopixel.slimcolonies.core.network.messages.server.AbstractBuildingServerMessage;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

/**
 * Picks up the building block with the level.
 */
public class BuildPickUpMessage extends AbstractBuildingServerMessage<IBuilding>
{
    public static final PlayMessageType<?> TYPE = PlayMessageType.forServer(Constants.MOD_ID, "build_pick_up", BuildPickUpMessage::new);

    /**
     * Creates a build request
     *
     * @param building the building we're executing on.
     */
    public BuildPickUpMessage(@NotNull final IBuildingView building)
    {
        super(TYPE, building);
    }

    protected BuildPickUpMessage(final RegistryFriendlyByteBuf buf, final PlayMessageType<?> type)
    {
        super(buf, type);
    }

    @Override
    protected void onExecute(final IPayloadContext ctxIn, final ServerPlayer player, final IColony colony, final IBuilding building)
    {
        building.pickUp(player);
    }
}
