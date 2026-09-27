package no.monopixel.slimcolonies.core.client.gui;

import no.monopixel.slimcolonies.api.colony.buildings.modules.IMinimumStockModuleView;
import no.monopixel.slimcolonies.api.util.constant.Constants;
import no.monopixel.slimcolonies.core.client.gui.modules.building.MinimumStockModuleWindow;
import net.minecraft.resources.ResourceLocation;

/**
 * BOWindow for the request PostBox GUI.
 */
public class WindowPostBoxMinStock extends MinimumStockModuleWindow
{
    /**
     * Create the postBox GUI.
     *
     * @param moduleView the module view.
     */
    public WindowPostBoxMinStock(final IMinimumStockModuleView moduleView)
    {
        super(moduleView, new ResourceLocation(Constants.MOD_ID, "gui/windowpostboxminstock.xml"));
        WindowPostBoxMain.registerPostboxTabs(this, moduleView.getBuildingView());
    }

    @Override
    protected boolean shouldRenderDefaultSidebar()
    {
        return false;
    }
}
