package no.monopixel.slimcolonies.core.network.messages.server.colony.building.worker;

import com.ldtteam.common.network.PlayMessageType;
import no.monopixel.slimcolonies.api.colony.IColony;
import no.monopixel.slimcolonies.api.colony.buildings.HiringMode;
import no.monopixel.slimcolonies.api.colony.buildings.IBuilding;
import no.monopixel.slimcolonies.api.colony.buildings.modules.IAssignsCitizen;
import no.monopixel.slimcolonies.api.colony.buildings.views.IBuildingView;
import no.monopixel.slimcolonies.api.util.constant.Constants;
import no.monopixel.slimcolonies.core.network.messages.server.AbstractBuildingServerMessage;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

/**
 * Message to set the hiring mode of a building.
 */
public class BuildingHiringModeMessage extends AbstractBuildingServerMessage<IBuilding>
{
    public static final PlayMessageType<?> TYPE = PlayMessageType.forServer(Constants.MOD_ID, "building_hiring_mode", BuildingHiringModeMessage::new);

    /**
     * The Hiring mode to set.
     */
    private final HiringMode    mode;

    /**
     * The job id.
     */
    private final int moduleId;

    /**
     * Creates object for the hiring mode
     *
     * @param building View of the building to read data from.
     * @param mode     the hiring mode.
     */
    public BuildingHiringModeMessage(@NotNull final IBuildingView building, final HiringMode mode, final int moduleId)
    {
        super(TYPE, building);
        this.mode = mode;
        this.moduleId = moduleId;
    }

    protected BuildingHiringModeMessage(final RegistryFriendlyByteBuf buf, final PlayMessageType<?> type)
    {
        super(buf, type);
        mode = HiringMode.values()[buf.readInt()];
        moduleId = buf.readInt();
    }

    @Override
    protected void toBytes(@NotNull final RegistryFriendlyByteBuf buf)
    {
        super.toBytes(buf);
        buf.writeInt(mode.ordinal());
        buf.writeInt(moduleId);
    }

    @Override
    protected void onExecute(final IPayloadContext ctxIn, final ServerPlayer player, final IColony colony, final IBuilding building)
    {
        if (building.getModule(moduleId) instanceof final IAssignsCitizen module)
        {
            module.setHiringMode(mode);
        }
    }
}
