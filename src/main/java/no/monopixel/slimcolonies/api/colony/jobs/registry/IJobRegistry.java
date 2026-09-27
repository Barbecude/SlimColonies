package no.monopixel.slimcolonies.api.colony.jobs.registry;

import no.monopixel.slimcolonies.api.IMinecoloniesAPI;
import net.minecraft.core.Registry;

public interface IJobRegistry
{
    static Registry<JobEntry> getInstance()
    {
        return IMinecoloniesAPI.getInstance().getJobRegistry();
    }
}
