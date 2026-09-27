package no.monopixel.slimcolonies.core.network.messages.server.colony;

import com.ldtteam.common.network.PlayMessageType;
import no.monopixel.slimcolonies.api.colony.IColony;
import no.monopixel.slimcolonies.api.colony.IColonyManager;
import no.monopixel.slimcolonies.api.colony.connections.ConnectionEvent;
import no.monopixel.slimcolonies.api.colony.permissions.Action;
import no.monopixel.slimcolonies.api.util.Log;
import no.monopixel.slimcolonies.api.util.constant.Constants;
import no.monopixel.slimcolonies.core.network.messages.server.AbstractColonyServerMessage;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Message for triggering a connection event message at a given colony from another colony.
 */
public class TriggerConnectionEventMessage extends AbstractColonyServerMessage
{
    public static final PlayMessageType<?> TYPE = PlayMessageType.forServer(Constants.MOD_ID, "trigger_connection", TriggerConnectionEventMessage::new);

    /**
     * Set the connection event data.
     */
    private ConnectionEvent connectionEventData;

    /**
     * Target colony id.
     */
    private int targetColonyId;

    public TriggerConnectionEventMessage(final IColony colony, final ConnectionEvent coreConnectionEventData, final int targetColonyId)
    {
        super(TYPE, colony);
        this.connectionEventData = coreConnectionEventData;
        this.targetColonyId = targetColonyId;
    }

    protected TriggerConnectionEventMessage(final RegistryFriendlyByteBuf buf, final PlayMessageType<?> type)
    {
        super(buf, type);
        this.connectionEventData = ConnectionEvent.deserializeByteBuf(buf);
        this.targetColonyId = buf.readInt();
    }

    @Override
    protected void onExecute(final IPayloadContext ctxIn, final ServerPlayer player, final IColony colony)
    {
        if (player == null)
        {
            return;
        }

        if (colony.getPermissions().hasPermission(player, Action.MANAGE_HUTS))
        {
            final IColony targetColony = IColonyManager.getInstance().getColonyByDimension(targetColonyId, colony.getDimension());
            if (targetColony == null)
            {
                Log.getLogger().error("Tried to trigger connection event at null colony: {}", targetColonyId);
                return;
            }

            targetColony.getConnectionManager().triggerConnectionEvent(connectionEventData);
        }
    }

    @Override
    protected void toBytes(final RegistryFriendlyByteBuf buf)
    {
        super.toBytes(buf);
        connectionEventData.serializeByteBuf(buf);
        buf.writeInt(targetColonyId);
    }
}
