package no.monopixel.slimcolonies.core.network.messages.server;

import com.ldtteam.common.network.AbstractServerPlayMessage;
import com.ldtteam.common.network.PlayMessageType;
import no.monopixel.slimcolonies.api.util.InventoryUtils;
import no.monopixel.slimcolonies.api.util.ItemStackUtils;
import no.monopixel.slimcolonies.api.util.MessageUtils;
import no.monopixel.slimcolonies.api.util.Utils;
import no.monopixel.slimcolonies.api.util.constant.Constants;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import static no.monopixel.slimcolonies.api.util.constant.TranslationConstants.COM_MINECOLONIES_BANNER_RALLY_GUARDS_GUI_ERROR;
import static no.monopixel.slimcolonies.core.items.ItemBannerRallyGuards.toggleBanner;

/**
 * Toggles a rallying banner
 */
public class ToggleBannerRallyGuardsMessage extends AbstractServerPlayMessage
{
    public static final PlayMessageType<?> TYPE = PlayMessageType.forServer(Constants.MOD_ID, "toggle_banner_rally_guards", ToggleBannerRallyGuardsMessage::new);

    /**
     * The banner to be toggled.
     */
    private final ItemStack banner;

    /**
     * Toggle the banner
     *
     * @param banner The banner to be toggled.
     */
    public ToggleBannerRallyGuardsMessage(final ItemStack banner)
    {
        super(TYPE);
        this.banner = banner;
    }

    protected ToggleBannerRallyGuardsMessage(final RegistryFriendlyByteBuf buf, final PlayMessageType<?> type)
    {
        super(buf, type);
        banner = Utils.deserializeCodecMess(buf);
    }

    @Override
    protected void toBytes(@NotNull final RegistryFriendlyByteBuf buf)
    {
        Utils.serializeCodecMess(buf, banner);
    }

    @Override
    protected void onExecute(final IPayloadContext ctxIn, final ServerPlayer player)
    {
        final int slot = InventoryUtils.findFirstSlotInItemHandlerWith(new InvWrapper(player.getInventory()),
          (itemStack -> ItemStackUtils.compareItemStacksIgnoreStackSize(itemStack, banner)));

        if (slot == -1)
        {
            MessageUtils.format(COM_MINECOLONIES_BANNER_RALLY_GUARDS_GUI_ERROR).sendTo(player);
            return;
        }

        toggleBanner(player.getInventory().getItem(slot), player);
    }
}
