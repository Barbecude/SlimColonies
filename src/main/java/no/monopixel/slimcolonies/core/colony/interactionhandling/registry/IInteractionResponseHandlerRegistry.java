package no.monopixel.slimcolonies.core.colony.interactionhandling.registry;

import no.monopixel.slimcolonies.api.IMinecoloniesAPI;
import no.monopixel.slimcolonies.api.colony.interactionhandling.registry.InteractionResponseHandlerEntry;
import net.minecraft.core.Registry;

public interface IInteractionResponseHandlerRegistry
{
    static Registry<InteractionResponseHandlerEntry> getInstance()
    {
        return IMinecoloniesAPI.getInstance().getInteractionResponseHandlerRegistry();
    }
}
