package no.monopixel.slimcolonies.core.debug.messages;

import com.ldtteam.common.network.PlayMessageType;
import no.monopixel.slimcolonies.api.colony.ICitizenData;
import no.monopixel.slimcolonies.api.colony.ICitizenDataView;
import no.monopixel.slimcolonies.api.colony.IColony;
import no.monopixel.slimcolonies.api.util.constant.Constants;
import no.monopixel.slimcolonies.core.debug.DebugPlayerManager;
import no.monopixel.slimcolonies.core.entity.citizen.EntityCitizen;
import no.monopixel.slimcolonies.core.network.messages.server.AbstractColonyServerMessage;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Message to query ai history from the server
 */
public class QueryCitizenAIHistoryMessage extends AbstractColonyServerMessage
{
    public static final PlayMessageType<?> TYPE = PlayMessageType.forServer(Constants.MOD_ID, "debug_aihistory", QueryCitizenAIHistoryMessage::new);

    /**
     * Citizen id
     */
    private int id;

    public QueryCitizenAIHistoryMessage(final ICitizenDataView citizen)
    {
        super(TYPE, citizen.getColony());
        this.id = citizen.getId();
    }

    protected QueryCitizenAIHistoryMessage(final RegistryFriendlyByteBuf buf, final PlayMessageType<?> type)
    {
        super(buf, type);
        this.id = buf.readInt();
    }

    @Override
    protected void toBytes(final RegistryFriendlyByteBuf buf)
    {
        super.toBytes(buf);
        buf.writeInt(id);
    }

    @Override
    protected void onExecute(final IPayloadContext ctxIn, final ServerPlayer player, final IColony colony)
    {
        if (player == null || !DebugPlayerManager.hasDebugEnabled(player))
        {
            return;
        }

        final ICitizenData citizen = colony.getCitizenManager().getCivilian(id);
        if (citizen == null || !citizen.getEntity().isPresent())
        {
            return;
        }

        if (citizen.getEntity().get() instanceof EntityCitizen entityCitizen)
        {
            MutableComponent message = Component.literal("Citizen AI: ").append(entityCitizen.getCitizenAI().getHistory());

            if (entityCitizen.getCitizenJobHandler().getColonyJob() != null)
            {
                message.append(Component.literal("Job AI: ").append(entityCitizen.getCitizenJobHandler().getWorkAI().getStateAI().getHistory()));
            }

            new DebugOutputMessage(message, true).sendToPlayer(player);
        }
    }
}
