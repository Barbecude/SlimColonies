package no.monopixel.slimcolonies.core.colony.buildings.moduleviews;

import com.ldtteam.blockui.views.BOWindow;
import no.monopixel.slimcolonies.api.colony.buildings.modules.AbstractBuildingModuleView;
import no.monopixel.slimcolonies.api.colony.managers.interfaces.IStatisticsManager;
import no.monopixel.slimcolonies.api.util.constant.Constants;
import no.monopixel.slimcolonies.core.client.gui.modules.building.WindowStatsModule;
import no.monopixel.slimcolonies.core.colony.managers.StatisticsManager;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

/**
 * Building statistic module.
 */
public class BuildingStatisticsModuleView extends AbstractBuildingModuleView
{
    /**
     * List of all beds.
     */
    private IStatisticsManager statisticsManager = new StatisticsManager();

    @Override
    public void deserialize(final @NotNull RegistryFriendlyByteBuf buf)
    {
        statisticsManager.deserialize(buf);
    }

    @Override
    public BOWindow getWindow()
    {
        return new WindowStatsModule(this);
    }

    @Override
    public ResourceLocation getIconResourceLocation()
    {
        return new ResourceLocation(Constants.MOD_ID, "textures/gui/modules/stats.png");
    }

    @Override
    public Component getDesc()
    {
        return Component.translatable("no.monopixel.slimcolonies.core.gui.modules.stats");
    }

    /**
     * Get the statistic manager of the building.
     * @return the manager.
     */
    public IStatisticsManager getBuildingStatisticsManager()
    {
        return statisticsManager;
    }
}
