package no.monopixel.slimcolonies.core.colony.buildings.moduleviews;

import com.ldtteam.blockui.views.BOWindow;
import no.monopixel.slimcolonies.api.colony.buildings.modules.AbstractBuildingModuleView;
import no.monopixel.slimcolonies.api.util.constant.Constants;
import no.monopixel.slimcolonies.core.client.gui.modules.building.WarehouseOptionsModuleWindow;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

/**
 * Client side version of the warehouse module.
 */
public class WarehouseOptionsModuleView extends AbstractBuildingModuleView
{
    /**
     * Storage upgrade level.
     */
    private int storageUpgrade = 0;

    @Override
    public Component getDesc()
    {
        return Component.translatable("no.monopixel.slimcolonies.coremod.gui.workerhuts.settings");
    }

    @Override
    public void deserialize(@NotNull final RegistryFriendlyByteBuf buf)
    {
        storageUpgrade = buf.readInt();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public BOWindow getWindow()
    {
        return new WarehouseOptionsModuleWindow(this);
    }

    @Override
    public ResourceLocation getIconResourceLocation()
    {
        return new ResourceLocation(Constants.MOD_ID, "textures/gui/modules/settings.png");
    }

    /**
     * Increment storage upgrade.
     */
    public void incrementStorageUpgrade()
    {
        storageUpgrade++;
    }

    /**
     * Get the current storage upgrade level.
     *
     * @return the level.
     */
    public int getStorageUpgradeLevel()
    {
        return storageUpgrade;
    }
}
