package no.monopixel.slimcolonies.core.commands.colonycommands;

import no.monopixel.slimcolonies.api.colony.IColony;
import no.monopixel.slimcolonies.api.util.MessageUtils;
import no.monopixel.slimcolonies.core.MineColonies;
import no.monopixel.slimcolonies.core.commands.arguments.ColonyIdArgument;
import no.monopixel.slimcolonies.core.commands.commandTypes.IMCColonyOfficerCommand;
import no.monopixel.slimcolonies.core.commands.commandTypes.IMCCommand;
import no.monopixel.slimcolonies.core.util.TeleportHelper;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import static no.monopixel.slimcolonies.api.util.constant.translation.CommandTranslationConstants.COMMAND_DISABLED_IN_CONFIG;
import static no.monopixel.slimcolonies.core.commands.CommandArgumentNames.COLONYID_ARG;

/**
 * Teleports the user to the given colony
 */
public class CommandTeleport implements IMCColonyOfficerCommand
{
    /**
     * What happens when the command is executed after preConditions are successful.
     *
     * @param context the context of the command execution
     */
    @Override
    public int onExecute(final CommandContext<CommandSourceStack> context)
    {
        final Entity sender = context.getSource().getEntity();

        if (!IMCCommand.isPlayerOped((Player) sender) && !MineColonies.getConfig().getServer().canPlayerUseColonyTPCommand.get())
        {
            MessageUtils.format(COMMAND_DISABLED_IN_CONFIG).sendTo((Player) sender);
            return 0;
        }

        final IColony colony = ColonyIdArgument.getColony(context, COLONYID_ARG);

        final ServerPlayer player = (ServerPlayer) sender;
        TeleportHelper.colonyTeleport(player, colony);
        return 1;
    }

    /**
     * Name string of the command.
     */
    @Override
    public String getName()
    {
        return "teleport";
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> build()
    {
        return IMCCommand.newLiteral(getName())
                 .then(IMCCommand.newArgument(COLONYID_ARG, ColonyIdArgument.id()).executes(this::checkPreConditionAndExecute));
    }
}
