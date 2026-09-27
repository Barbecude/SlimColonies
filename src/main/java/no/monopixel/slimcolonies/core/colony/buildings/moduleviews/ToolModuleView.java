package no.monopixel.slimcolonies.core.colony.buildings.moduleviews;

import com.ldtteam.blockui.views.BOWindow;
import no.monopixel.slimcolonies.api.colony.buildings.modules.AbstractBuildingModuleView;
import no.monopixel.slimcolonies.api.util.constant.Constants;
import no.monopixel.slimcolonies.core.client.gui.modules.building.ToolModuleWindow;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

/**
 * Client side version of the abstract class for all buildings which allows to select tools.
 */
public class ToolModuleView extends AbstractBuildingModuleView
{
    /**
     * The worker specific tool.
     */
    private final Item tool;

    /**
     * The tool of the worker.
     * @param tool the item.
     */
    public ToolModuleView(final Item tool)
    {
        super();
        this.tool = tool;
    }

    @Override
    public Component getDesc()
    {
        return Component.translatable("no.monopixel.slimcolonies.coremod.gui.workerhuts.tools");
    }

    @Override
    public void deserialize(@NotNull final RegistryFriendlyByteBuf buf)
    {

    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public BOWindow getWindow()
    {
        return new ToolModuleWindow(this);
    }

    @Override
    public ResourceLocation getIconResourceLocation()
    {
        return new ResourceLocation(Constants.MOD_ID, "textures/gui/modules/scepter.png");
    }

    /**
     * Get the correct tool.
     * @return the tool to give.
     */
    public Item getTool()
    {
        return tool;
    }
}
