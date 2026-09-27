package no.monopixel.slimcolonies.api.colony.buildings.registry;

import no.monopixel.slimcolonies.api.IMinecoloniesAPI;
import net.minecraft.core.Registry;

public interface IBuildingRegistry
{

    static Registry<BuildingEntry> getInstance()
    {
        return IMinecoloniesAPI.getInstance().getBuildingRegistry();
    }
}
