package no.monopixel.slimcolonies.apiimp.initializer;

import no.monopixel.slimcolonies.api.colony.colonyEvents.registry.ColonyEventTypeRegistryEntry;
import no.monopixel.slimcolonies.api.util.constant.Constants;
import no.monopixel.slimcolonies.apiimp.CommonMinecoloniesAPIImpl;
import no.monopixel.slimcolonies.core.colony.events.raid.CustomModRaidEvent;
import no.monopixel.slimcolonies.core.colony.events.raid.amazonevent.AmazonRaidEvent;
import no.monopixel.slimcolonies.core.colony.events.raid.barbarianEvent.BarbarianRaidEvent;
import no.monopixel.slimcolonies.core.colony.events.raid.egyptianevent.EgyptianRaidEvent;
import no.monopixel.slimcolonies.core.colony.events.raid.norsemenevent.NorsemenRaidEvent;
import no.monopixel.slimcolonies.core.colony.events.raid.norsemenevent.NorsemenShipRaidEvent;
import no.monopixel.slimcolonies.core.colony.events.raid.pirateEvent.DrownedPirateRaidEvent;
import no.monopixel.slimcolonies.core.colony.events.raid.pirateEvent.PirateGroundRaidEvent;
import no.monopixel.slimcolonies.core.colony.events.raid.pirateEvent.PirateRaidEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Initializer for colony event types, register new event types here.
 */
public final class ModColonyEventTypeInitializer
{
    public final static DeferredRegister<ColonyEventTypeRegistryEntry> DEFERRED_REGISTER = DeferredRegister.create(CommonMinecoloniesAPIImpl.COLONY_EVENT_TYPES, Constants.MOD_ID);

    private ModColonyEventTypeInitializer()
    {
        throw new IllegalStateException("Tried to initialize: ModColonyEventTypeInitializer but this is a Utility class.");
    }

    static
    {
        DEFERRED_REGISTER.register(CustomModRaidEvent.CUSTOM_MOD_RAID_EVENT_TYPE_ID.getPath(), () -> new ColonyEventTypeRegistryEntry(CustomModRaidEvent::loadFromNBT, CustomModRaidEvent.CUSTOM_MOD_RAID_EVENT_TYPE_ID, true));
        DEFERRED_REGISTER.register(PirateRaidEvent.PIRATE_RAID_EVENT_TYPE_ID.getPath(), () -> new ColonyEventTypeRegistryEntry(PirateRaidEvent::loadFromNBT, PirateRaidEvent.PIRATE_RAID_EVENT_TYPE_ID, true));
        DEFERRED_REGISTER.register(BarbarianRaidEvent.BARBARIAN_RAID_EVENT_TYPE_ID.getPath(), () -> new ColonyEventTypeRegistryEntry(BarbarianRaidEvent::loadFromNBT, BarbarianRaidEvent.BARBARIAN_RAID_EVENT_TYPE_ID, true));
        DEFERRED_REGISTER.register(EgyptianRaidEvent.EGYPTIAN_RAID_EVENT_TYPE_ID.getPath(), () -> new ColonyEventTypeRegistryEntry(EgyptianRaidEvent::loadFromNBT, EgyptianRaidEvent.EGYPTIAN_RAID_EVENT_TYPE_ID, true));
        DEFERRED_REGISTER.register(AmazonRaidEvent.AMAZON_RAID_EVENT_TYPE_ID.getPath(), () -> new ColonyEventTypeRegistryEntry(AmazonRaidEvent::loadFromNBT, AmazonRaidEvent.AMAZON_RAID_EVENT_TYPE_ID, true));
        DEFERRED_REGISTER.register(NorsemenRaidEvent.NORSEMEN_RAID_EVENT_TYPE_ID.getPath(), () -> new ColonyEventTypeRegistryEntry(NorsemenRaidEvent::loadFromNBT, NorsemenRaidEvent.NORSEMEN_RAID_EVENT_TYPE_ID, true));
        DEFERRED_REGISTER.register(NorsemenShipRaidEvent.NORSEMEN_RAID_EVENT_TYPE_ID.getPath(), () -> new ColonyEventTypeRegistryEntry(NorsemenShipRaidEvent::loadFromNBT, NorsemenShipRaidEvent.NORSEMEN_RAID_EVENT_TYPE_ID, true));
        DEFERRED_REGISTER.register(PirateGroundRaidEvent.PIRATE_GROUND_RAID_EVENT_TYPE_ID.getPath(), () -> new ColonyEventTypeRegistryEntry(PirateGroundRaidEvent::loadFromNBT, PirateGroundRaidEvent.PIRATE_GROUND_RAID_EVENT_TYPE_ID, true));
        DEFERRED_REGISTER.register(DrownedPirateRaidEvent.PIRATE_RAID_EVENT_TYPE_ID.getPath(), () -> new ColonyEventTypeRegistryEntry(DrownedPirateRaidEvent::loadFromNBT, DrownedPirateRaidEvent.PIRATE_RAID_EVENT_TYPE_ID, true));
    }
}
