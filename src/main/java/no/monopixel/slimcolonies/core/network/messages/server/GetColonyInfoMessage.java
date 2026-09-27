package no.monopixel.slimcolonies.core.network.messages.server;

import com.ldtteam.common.network.AbstractServerPlayMessage;
import com.ldtteam.common.network.PlayMessageType;
import no.monopixel.slimcolonies.api.colony.IColony;
import no.monopixel.slimcolonies.api.colony.IColonyManager;
import no.monopixel.slimcolonies.api.util.BlockPosUtil;
import no.monopixel.slimcolonies.api.util.MessageUtils;
import no.monopixel.slimcolonies.api.util.constant.Constants;
import no.monopixel.slimcolonies.core.MineColonies;
import no.monopixel.slimcolonies.core.colony.Colony;
import no.monopixel.slimcolonies.core.network.messages.client.OpenCantFoundColonyWarningMessage;
import no.monopixel.slimcolonies.core.network.messages.client.OpenColonyFoundingCovenantMessage;
import no.monopixel.slimcolonies.core.network.messages.client.OpenDeleteAbandonColonyMessage;
import no.monopixel.slimcolonies.core.network.messages.client.OpenReactivateColonyMessage;
import no.monopixel.slimcolonies.core.tileentities.TileEntityColonyBuilding;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static no.monopixel.slimcolonies.api.util.constant.BuildingConstants.DEACTIVATED;
import static no.monopixel.slimcolonies.api.util.constant.TranslationConstants.HUT_BLOCK_MISSING_BUILDING;
import static no.monopixel.slimcolonies.core.MineColonies.getConfig;

/**
 * Message for asking the server for some colony info before creation.
 */
public class GetColonyInfoMessage extends AbstractServerPlayMessage
{
    public static final PlayMessageType<?> TYPE = PlayMessageType.forServer(Constants.MOD_ID, "get_colony_info", GetColonyInfoMessage::new);

    /**
     * Position the player wants to found the colony at.
     */
    BlockPos pos;

    public GetColonyInfoMessage(final RegistryFriendlyByteBuf buf, final PlayMessageType<?> type)
    {
        super(type);
        pos = buf.readBlockPos();
    }

    public GetColonyInfoMessage(final BlockPos pos)
    {
        super(TYPE);
        this.pos = pos;
    }

    @Override
    public void toBytes(final RegistryFriendlyByteBuf buf)
    {
        buf.writeBlockPos(pos);
    }

    @Override
    protected void onExecute(final IPayloadContext ctxIn, final ServerPlayer sender)
    {
        if (sender == null)
        {
            return;
        }
        final Level world = sender.level();

        if (IColonyManager.getInstance().getColonyByPosFromWorld(world, pos) instanceof Colony)
        {
            MessageUtils.format(HUT_BLOCK_MISSING_BUILDING).sendTo(sender);
            return;
        }

        if (IColonyManager.getInstance().getIColonyByOwner(world, sender) instanceof Colony colony)
        {
            new OpenDeleteAbandonColonyMessage(pos, colony.getName(), colony.getCenter(), colony.getID()).sendToPlayer(sender);
            return;
        }

        final IColony nextColony = IColonyManager.getInstance().getClosestColony(world, pos);
        if (IColonyManager.getInstance().isFarEnoughFromColonies(world, pos))
        {
            final double spawnDistance = Math.sqrt(BlockPosUtil.getDistanceSquared2D(pos, world.getSharedSpawnPos()));
            if (spawnDistance < MineColonies.getConfig().getServer().minDistanceFromWorldSpawn.get())
            {
                new OpenCantFoundColonyWarningMessage(Component.translatable("no.monopixel.slimcolonies.core.founding.tooclosetospawn", (int) (MineColonies.getConfig().getServer().minDistanceFromWorldSpawn.get() - spawnDistance)), pos, true).sendToPlayer(sender);
            }
            else if (spawnDistance > MineColonies.getConfig().getServer().maxDistanceFromWorldSpawn.get())
            {
                new OpenCantFoundColonyWarningMessage(Component.translatable("no.monopixel.slimcolonies.core.founding.toofarfromspawn", (int) (spawnDistance - MineColonies.getConfig().getServer().maxDistanceFromWorldSpawn.get())), pos, true).sendToPlayer(sender);
            }
            else if (world.getBlockEntity(pos) instanceof TileEntityColonyBuilding townhall && townhall.getPositionedTags().containsKey(BlockPos.ZERO) && townhall.getPositionedTags().get(BlockPos.ZERO).contains(DEACTIVATED))
            {
               new OpenReactivateColonyMessage(nextColony == null ? "" : nextColony.getName(), nextColony == null ? Integer.MAX_VALUE : (int) BlockPosUtil.getDistance(nextColony.getCenter(), pos) - (getConfig().getServer().initialColonySize.get() << 4), pos).sendToPlayer(sender);
            }
            else
            {
                new OpenColonyFoundingCovenantMessage(nextColony == null ? "" : nextColony.getName(), nextColony == null ? Integer.MAX_VALUE : (int) BlockPosUtil.getDistance(nextColony.getCenter(), pos) - (getConfig().getServer().initialColonySize.get() << 4), pos).sendToPlayer(sender);
            }
        }
        else
        {
            if (nextColony == null)
            {
                return;
            }

            final int blockRange = Math.max(MineColonies.getConfig().getServer().minColonyDistance.get(), getConfig().getServer().initialColonySize.get()) << 4;
            final int distance = (int) BlockPosUtil.getDistance(pos, nextColony.getCenter());

            new OpenCantFoundColonyWarningMessage(Component.translatable("no.monopixel.slimcolonies.core.founding.tooclosetocolony", Math.max(100, blockRange - distance)), pos, false).sendToPlayer(sender);
        }
    }
}
