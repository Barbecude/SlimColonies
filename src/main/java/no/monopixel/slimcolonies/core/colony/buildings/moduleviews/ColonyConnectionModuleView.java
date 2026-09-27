package no.monopixel.slimcolonies.core.colony.buildings.moduleviews;

import com.ldtteam.blockui.views.BOWindow;
import no.monopixel.slimcolonies.api.colony.buildings.modules.AbstractBuildingModuleView;
import no.monopixel.slimcolonies.api.util.constant.Constants;
import no.monopixel.slimcolonies.core.client.gui.modules.building.ConnectionModuleWindow;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

/**
 * Client side version of colony connection module. Primarily a wrapper for the UI.
 */
public class ColonyConnectionModuleView extends AbstractBuildingModuleView
{
    /**
     * Constructor.
     */
    public ColonyConnectionModuleView()
    {
        super();
    }

    @Override
    public Component getDesc()
    {
        return Component.translatable("no.monopixel.slimcolonies.core.gui.connections");
    }

    @Override
    public void deserialize(final @NotNull RegistryFriendlyByteBuf buf)
    {

    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public BOWindow getWindow()
    {
        return new ConnectionModuleWindow(buildingView, false);
    }

    @Override
    public ResourceLocation getIconResourceLocation()
    {
        return new ResourceLocation(Constants.MOD_ID, "textures/gui/modules/connection.png");
    }
}
