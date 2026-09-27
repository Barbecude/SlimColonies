package no.monopixel.slimcolonies.core.network.messages.server.colony;

import com.ldtteam.common.network.PlayMessageType;
import no.monopixel.slimcolonies.api.colony.IColony;
import no.monopixel.slimcolonies.api.util.constant.Constants;
import no.monopixel.slimcolonies.core.network.messages.server.AbstractColonyServerMessage;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Message to set the colony default structure style.
 */
public class ColonyStructureStyleMessage extends AbstractColonyServerMessage
{
    public static final PlayMessageType<?> TYPE = PlayMessageType.forServer(Constants.MOD_ID, "colony_structure_style", ColonyStructureStyleMessage::new);

    /**
     * The chosen pack.
     */
    private final String pack;

    /**
     * Change the colony default pack from the client to the serverside.
     *
     * @param colony the colony the player changed the pack in.
     * @param pack   the pack name,
     */
    public ColonyStructureStyleMessage(final IColony colony, final String pack)
    {
        super(TYPE, colony);
        this.pack = pack;
    }

    @Override
    protected void onExecute(final IPayloadContext ctxIn, final ServerPlayer player, final IColony colony)
    {
        colony.setStructurePack(pack);
    }

    @Override
    protected void toBytes(final RegistryFriendlyByteBuf buf)
    {
        super.toBytes(buf);
        buf.writeUtf(pack);
    }

    protected ColonyStructureStyleMessage(final RegistryFriendlyByteBuf buf, final PlayMessageType<?> type)
    {
        super(buf, type);
        this.pack = buf.readUtf(32767);
    }
}

