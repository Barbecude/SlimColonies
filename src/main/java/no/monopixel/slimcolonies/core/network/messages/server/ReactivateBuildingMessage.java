package no.monopixel.slimcolonies.core.network.messages.server;

import com.ldtteam.common.network.AbstractServerPlayMessage;
import com.ldtteam.common.network.PlayMessageType;
import no.monopixel.slimcolonies.api.colony.IColony;
import no.monopixel.slimcolonies.api.colony.IColonyManager;
import no.monopixel.slimcolonies.api.colony.permissions.Action;
import no.monopixel.slimcolonies.api.util.constant.Constants;
import no.monopixel.slimcolonies.core.colony.buildings.AbstractBuilding;
import no.monopixel.slimcolonies.core.tileentities.TileEntityColonyBuilding;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

/**
 * Reactivate a building.
 */
public class ReactivateBuildingMessage extends AbstractServerPlayMessage
{
    public static final PlayMessageType<?> TYPE = PlayMessageType.forServer(Constants.MOD_ID, "reactivate_building", ReactivateBuildingMessage::new);

    /**
     * The position to reactivate it.
     */
    private final BlockPos pos;

    /**
     * Reactivate the building.
     *
     * @param pos the position of the building.
     */
    public ReactivateBuildingMessage(final BlockPos pos)
    {
        super(TYPE);
        this.pos = pos;
    }

    /**
     * Reads this packet from a {@link RegistryFriendlyByteBuf}.
     *
     * @param buf The buffer begin read from.
     */
    protected ReactivateBuildingMessage(final RegistryFriendlyByteBuf buf, final PlayMessageType<?> type)
    {
        super(buf, type);
        pos = buf.readBlockPos();
    }

    /**
     * Writes this packet to a {@link RegistryFriendlyByteBuf}.
     *
     * @param buf The buffer being written to.
     */
    @Override
    protected void toBytes(@NotNull final RegistryFriendlyByteBuf buf)
    {
        buf.writeBlockPos(pos);
    }

    @Override
    protected void onExecute(final IPayloadContext ctxIn, final ServerPlayer player)
    {
        final Level world = player.getCommandSenderWorld();
        final IColony colony = IColonyManager.getInstance().getColonyByPosFromWorld(world, pos);
        if (colony != null && colony.getPermissions().hasPermission(player, Action.MANAGE_HUTS))
        {
            AbstractBuilding building = (AbstractBuilding) colony.getServerBuildingManager().getBuilding(pos);
            if (building == null)
            {
                final BlockEntity tileEntity = world.getBlockEntity(pos);
                if (tileEntity instanceof final TileEntityColonyBuilding hut)
                {
                    if (!colony.getServerBuildingManager().canPlaceAt(tileEntity.getBlockState().getBlock(), pos, player))
                    {
                        return;
                    }

                    hut.reactivate();
                    colony.getServerBuildingManager().addNewBuilding(hut, world);
                }
            }
        }
    }
}
