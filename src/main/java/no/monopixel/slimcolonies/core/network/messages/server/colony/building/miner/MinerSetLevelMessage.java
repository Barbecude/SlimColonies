package no.monopixel.slimcolonies.core.network.messages.server.colony.building.miner;

import com.ldtteam.common.network.PlayMessageType;
import no.monopixel.slimcolonies.api.colony.IColony;
import no.monopixel.slimcolonies.api.colony.buildings.views.IBuildingView;
import no.monopixel.slimcolonies.api.util.constant.Constants;
import no.monopixel.slimcolonies.core.colony.buildings.modules.BuildingModules;
import no.monopixel.slimcolonies.core.colony.buildings.workerbuildings.BuildingMiner;
import no.monopixel.slimcolonies.core.network.messages.server.AbstractBuildingServerMessage;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

/**
 * Message to set the level of the miner from the GUI.
 */
public class MinerSetLevelMessage extends AbstractBuildingServerMessage<BuildingMiner>
{
    public static final PlayMessageType<?> TYPE = PlayMessageType.forServer(Constants.MOD_ID, "miner_set_level", MinerSetLevelMessage::new);

    private int level;

    /**
     * Creates object for the miner set level
     *
     * @param building View of the building to read data from.
     * @param level    Level of the miner.
     */
    public MinerSetLevelMessage(@NotNull final IBuildingView building, final int level)
    {
        super(TYPE, building);
        this.level = level;
    }

    protected MinerSetLevelMessage(final RegistryFriendlyByteBuf buf, final PlayMessageType<?> type)
    {
        super(buf, type);
        level = buf.readInt();
    }

    @Override
    protected void toBytes(@NotNull final RegistryFriendlyByteBuf buf)
    {
        super.toBytes(buf);
        buf.writeInt(level);
    }

    @Override
    protected void onExecute(final IPayloadContext ctxIn, final ServerPlayer player, final IColony colony, final BuildingMiner building)
    {
        building.getModule(BuildingModules.MINER_LEVELS).setCurrentLevel(level);
    }
}
