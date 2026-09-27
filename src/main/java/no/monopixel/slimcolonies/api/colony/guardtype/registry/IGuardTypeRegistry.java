package no.monopixel.slimcolonies.api.colony.guardtype.registry;

import no.monopixel.slimcolonies.api.IMinecoloniesAPI;
import no.monopixel.slimcolonies.api.colony.guardtype.GuardType;
import net.minecraft.core.Registry;

public interface IGuardTypeRegistry
{

    static Registry<GuardType> getInstance()
    {
        return IMinecoloniesAPI.getInstance().getGuardTypeRegistry();
    }
}
