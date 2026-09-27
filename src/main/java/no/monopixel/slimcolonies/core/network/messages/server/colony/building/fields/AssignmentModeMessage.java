package no.monopixel.slimcolonies.core.network.messages.server.colony.building.fields;

import com.ldtteam.common.network.PlayMessageType;
import no.monopixel.slimcolonies.api.colony.IColony;
import no.monopixel.slimcolonies.api.colony.buildings.IBuilding;
import no.monopixel.slimcolonies.api.colony.buildings.views.IBuildingView;
import no.monopixel.slimcolonies.api.util.constant.Constants;
import no.monopixel.slimcolonies.core.colony.buildings.modules.BuildingExtensionsModule;
import no.monopixel.slimcolonies.core.network.messages.server.AbstractBuildingServerMessage;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

/**
 * Message to change the assignmentMode of the fields of the farmer.
 */
public class AssignmentModeMessage extends AbstractBuildingServerMessage<IBuilding>
{
    public static final PlayMessageType<?> TYPE = PlayMessageType.forServer(Constants.MOD_ID, "assignment_mode", AssignmentModeMessage::new);

    private final int id;
    private final boolean assignmentMode;

    /**
     * Creates object for the assignmentMode
     *
     * @param assignmentMode assignmentMode of the particular farmer.
     * @param building       the building we're executing on.
     */
    public AssignmentModeMessage(@NotNull final IBuildingView building, final boolean assignmentMode , final int runtimeID)
    {
        super(TYPE, building);
        this.assignmentMode = assignmentMode;
        this.id = runtimeID;
    }

    @Override
    protected void toBytes(@NotNull final RegistryFriendlyByteBuf buf)
    {
        super.toBytes(buf);
        buf.writeInt(id);
        buf.writeBoolean(assignmentMode);
    }

    protected AssignmentModeMessage(final RegistryFriendlyByteBuf buf, final PlayMessageType<?> type)
    {
        super(buf, type);
        id = buf.readInt();
        assignmentMode = buf.readBoolean();
    }

    @Override
    protected void onExecute(final IPayloadContext ctxIn, final ServerPlayer player, final IColony colony, final IBuilding building)
    {
        if (building.hasModule(BuildingExtensionsModule.class))
        {
            ((BuildingExtensionsModule)building.getModule(id)).setAssignManually(assignmentMode);
        }
    }
}
