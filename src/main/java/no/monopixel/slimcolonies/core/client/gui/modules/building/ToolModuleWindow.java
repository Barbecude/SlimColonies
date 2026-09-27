package no.monopixel.slimcolonies.core.client.gui.modules.building;

import com.ldtteam.blockui.controls.Text;
import no.monopixel.slimcolonies.api.util.constant.Constants;
import no.monopixel.slimcolonies.core.client.gui.AbstractModuleWindow;
import no.monopixel.slimcolonies.core.colony.buildings.moduleviews.ToolModuleView;
import no.monopixel.slimcolonies.core.network.messages.server.colony.building.GiveToolMessage;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ToolModuleWindow extends AbstractModuleWindow<ToolModuleView>
{
    /**
     * ID of the button to give tool
     */
    private static final String BUTTON_GIVE_TOOL = "giveTool";

    /**
     * Constructor for the minimum stock window view.
     *
     * @param moduleView the module view.
     */
    public ToolModuleWindow(final ToolModuleView moduleView)
    {
        super(moduleView,  new ResourceLocation(Constants.MOD_ID, "gui/layouthuts/layouttool.xml"));

        findPaneOfTypeByID("desc", Text.class).setText(Component.translatableEscape("no.monopixel.slimcolonies.coremod.gui.tooldesc." + BuiltInRegistries.ITEM.getKey(moduleView.getTool()).getPath()));
        registerButton(BUTTON_GIVE_TOOL, this::givePlayerScepter);
    }

    /**
     * Send message to player to add scepter to his inventory.
     */
    private void givePlayerScepter()
    {
        new GiveToolMessage(buildingView, moduleView.getTool()).sendToServer();
    }
}
