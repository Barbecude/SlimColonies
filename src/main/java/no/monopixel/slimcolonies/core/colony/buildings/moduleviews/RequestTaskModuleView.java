package no.monopixel.slimcolonies.core.colony.buildings.moduleviews;

import com.ldtteam.blockui.views.BOWindow;
import no.monopixel.slimcolonies.api.colony.buildings.modules.AbstractBuildingModuleView;
import no.monopixel.slimcolonies.api.colony.requestsystem.token.IToken;
import no.monopixel.slimcolonies.api.util.constant.Constants;
import no.monopixel.slimcolonies.core.client.gui.modules.building.WindowHutRequestTaskModule;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Request task module to display tasks in the UI.
 */
public abstract class RequestTaskModuleView extends AbstractBuildingModuleView
{
    @Override
    public void deserialize(@NotNull final RegistryFriendlyByteBuf buf)
    {

    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public BOWindow getWindow()
    {
        return new WindowHutRequestTaskModule(this);
    }

    @Override
    public ResourceLocation getIconResourceLocation()
    {
        return new ResourceLocation(Constants.MOD_ID, "textures/gui/modules/info.png");
    }

    @Override
    public Component getDesc()
    {
        return Component.translatable("no.monopixel.slimcolonies.coremod.gui.workerhuts.crafter.tasks");
    }

    /**
     * Get the specific task list.
     * @return the task list.
     */
    public abstract List<IToken<?>> getTasks();
}
