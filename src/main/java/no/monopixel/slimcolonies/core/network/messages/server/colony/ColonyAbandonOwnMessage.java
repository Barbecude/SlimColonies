package no.monopixel.slimcolonies.core.network.messages.server.colony;

import com.ldtteam.common.network.AbstractServerPlayMessage;
import com.ldtteam.common.network.PlayMessageType;
import no.monopixel.slimcolonies.api.colony.IColony;
import no.monopixel.slimcolonies.api.colony.IColonyManager;
import no.monopixel.slimcolonies.api.util.MessageUtils;
import no.monopixel.slimcolonies.api.util.constant.Constants;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static no.monopixel.slimcolonies.api.util.constant.TranslationConstants.*;

/**
 * Message for deleting an owned colony
 */
public class ColonyAbandonOwnMessage extends AbstractServerPlayMessage
{
    public static final PlayMessageType<?> TYPE = PlayMessageType.forServer(Constants.MOD_ID, "colony_abandon_own", ColonyAbandonOwnMessage::new);

    @Override
    protected void toBytes(final RegistryFriendlyByteBuf buf)
    {
        // noop
    }

    protected ColonyAbandonOwnMessage(final RegistryFriendlyByteBuf buf, final PlayMessageType<?> type)
    {
        super(buf, type);
    }

    public ColonyAbandonOwnMessage()
    {
        super(TYPE);
    }

    @Override
    protected void onExecute(final IPayloadContext ctxIn, final ServerPlayer player)
    {
        if (player == null)
        {
            return;
        }

        final IColony colony = IColonyManager.getInstance().getIColonyByOwner(player.level(), player);
        if (colony != null)
        {
            colony.getPermissions().setOwnerAbandoned();
            colony.getPermissions().addPlayer(player.getGameProfile(), colony.getPermissions().getRankOfficer());
            MessageUtils.format(MESSAGE_INFO_COLONY_ABANDON_SUCCESS).sendTo(player);
        }
        else
        {
            MessageUtils.format(MESSAGE_INFO_COLONY_NOT_FOUND).sendTo(player);
        }
    }
}
