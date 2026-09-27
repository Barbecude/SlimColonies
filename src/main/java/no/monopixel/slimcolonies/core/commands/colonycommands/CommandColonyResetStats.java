package no.monopixel.slimcolonies.core.commands.colonycommands;

import no.monopixel.slimcolonies.api.colony.IColony;
import no.monopixel.slimcolonies.api.colony.buildings.IBuilding;
import no.monopixel.slimcolonies.core.colony.buildings.modules.BuildingStatisticsModule;
import no.monopixel.slimcolonies.core.commands.arguments.ColonyIdArgument;
import no.monopixel.slimcolonies.core.commands.commandTypes.IMCCommand;
import no.monopixel.slimcolonies.core.commands.commandTypes.IMCOPCommand;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

import static no.monopixel.slimcolonies.api.util.constant.translation.CommandTranslationConstants.COMMAND_COLONY_RESET_STATS_SUCCESS;
import static no.monopixel.slimcolonies.core.commands.CommandArgumentNames.COLONYID_ARG;

/**
 * Command to reset all colony and building statistics for a colony.
 */
public class CommandColonyResetStats implements IMCOPCommand
{
    /**
     * Clears the colony-wide statistics and all statistics stored by the colony's building modules.
     *
     * @param context the command execution context.
     * @return {@code 1} when the statistics were successfully reset.
     */
    @Override
    public int onExecute(final CommandContext<CommandSourceStack> context)
    {
        final IColony colony = ColonyIdArgument.getColony(context, COLONYID_ARG);
        colony.getStatisticsManager().clear();

        int buildingCount = 0;
        for (final IBuilding building : colony.getServerBuildingManager().getBuildings().values())
        {
            boolean clearedBuildingStats = false;
            for (final var module : building.getModules())
            {
                if (module instanceof BuildingStatisticsModule statisticsModule)
                {
                    statisticsModule.getBuildingStatisticsManager().clear();
                    clearedBuildingStats = true;
                }
            }

            if (clearedBuildingStats)
            {
                building.markDirty();
                buildingCount++;
            }
        }

        colony.markDirty();
        final int clearedBuildingCount = buildingCount;
        context.getSource().sendSuccess(
          () -> Component.translatable(COMMAND_COLONY_RESET_STATS_SUCCESS, colony.getID(), colony.getName(), clearedBuildingCount), true);
        return 1;
    }

    /**
     * Gets the literal name used to register this command.
     *
     * @return the command name.
     */
    @Override
    public String getName()
    {
        return "resetStats";
    }

    /**
     * Builds the command with its required colony selector argument.
     *
     * @return the command builder.
     */
    @Override
    public LiteralArgumentBuilder<CommandSourceStack> build()
    {
        return IMCCommand.newLiteral(getName())
                 .then(IMCCommand.newArgument(COLONYID_ARG, ColonyIdArgument.id()).executes(this::checkPreConditionAndExecute));
    }
}
