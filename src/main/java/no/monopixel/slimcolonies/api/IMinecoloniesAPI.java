package no.monopixel.slimcolonies.api;

import com.ldtteam.common.config.Configurations;

import no.monopixel.slimcolonies.api.client.render.modeltype.registry.IModelTypeRegistry;
import no.monopixel.slimcolonies.api.colony.ICitizenDataManager;
import no.monopixel.slimcolonies.api.colony.IColonyManager;
import no.monopixel.slimcolonies.api.colony.buildingextensions.registry.BuildingExtensionRegistries.BuildingExtensionEntry;
import no.monopixel.slimcolonies.api.colony.buildings.registry.BuildingEntry;
import no.monopixel.slimcolonies.api.colony.buildings.registry.IBuildingDataManager;
import no.monopixel.slimcolonies.api.colony.colonyEvents.registry.ColonyEventDescriptionTypeRegistryEntry;
import no.monopixel.slimcolonies.api.colony.colonyEvents.registry.ColonyEventTypeRegistryEntry;
import no.monopixel.slimcolonies.api.colony.guardtype.GuardType;
import no.monopixel.slimcolonies.api.colony.guardtype.registry.IGuardTypeDataManager;
import no.monopixel.slimcolonies.api.colony.interactionhandling.registry.IInteractionResponseHandlerDataManager;
import no.monopixel.slimcolonies.api.colony.interactionhandling.registry.InteractionResponseHandlerEntry;
import no.monopixel.slimcolonies.api.colony.jobs.registry.IJobDataManager;
import no.monopixel.slimcolonies.api.colony.jobs.registry.JobEntry;
import no.monopixel.slimcolonies.api.compatibility.IFurnaceRecipes;
import no.monopixel.slimcolonies.api.configuration.ClientConfiguration;
import no.monopixel.slimcolonies.api.configuration.CommonConfiguration;
import no.monopixel.slimcolonies.api.configuration.ServerConfiguration;
import no.monopixel.slimcolonies.api.crafting.registry.CraftingType;
import no.monopixel.slimcolonies.api.crafting.registry.RecipeTypeEntry;
import no.monopixel.slimcolonies.api.entity.mobs.registry.IMobAIRegistry;
import no.monopixel.slimcolonies.api.entity.citizen.happiness.HappinessRegistry;
import no.monopixel.slimcolonies.api.entity.pathfinding.registry.IPathNavigateRegistry;
import no.monopixel.slimcolonies.api.equipment.registry.EquipmentTypeEntry;
import no.monopixel.slimcolonies.api.eventbus.EventBus;
import no.monopixel.slimcolonies.api.quests.registries.QuestRegistries;
import no.monopixel.slimcolonies.api.research.IGlobalResearchTree;
import no.monopixel.slimcolonies.api.research.ModResearchEffects;
import no.monopixel.slimcolonies.api.research.ModResearchRequirements;
import net.minecraft.core.Registry;
import net.neoforged.neoforge.registries.NewRegistryEvent;

public interface IMinecoloniesAPI
{

    static IMinecoloniesAPI getInstance()
    {
        return MinecoloniesAPIProxy.getInstance();
    }

    IColonyManager getColonyManager();

    ICitizenDataManager getCitizenDataManager();

    IMobAIRegistry getMobAIRegistry();

    IPathNavigateRegistry getPathNavigateRegistry();

    IBuildingDataManager getBuildingDataManager();

    Registry<BuildingEntry> getBuildingRegistry();

    Registry<BuildingExtensionEntry> getBuildingExtensionRegistry();

    IJobDataManager getJobDataManager();

    Registry<JobEntry> getJobRegistry();

    Registry<InteractionResponseHandlerEntry> getInteractionResponseHandlerRegistry();

    IGuardTypeDataManager getGuardTypeDataManager();

    Registry<GuardType> getGuardTypeRegistry();

    IModelTypeRegistry getModelTypeRegistry();

    Configurations<ClientConfiguration, ServerConfiguration, CommonConfiguration> getConfig();

    IFurnaceRecipes getFurnaceRecipes();

    IInteractionResponseHandlerDataManager getInteractionResponseHandlerDataManager();

    IGlobalResearchTree getGlobalResearchTree();

    Registry<ModResearchRequirements.ResearchRequirementEntry> getResearchRequirementRegistry();

    Registry<ModResearchEffects.ResearchEffectEntry> getResearchEffectRegistry();

    Registry<ColonyEventTypeRegistryEntry> getColonyEventRegistry();

    Registry<ColonyEventDescriptionTypeRegistryEntry> getColonyEventDescriptionRegistry();

    Registry<RecipeTypeEntry> getRecipeTypeRegistry();

    Registry<CraftingType> getCraftingTypeRegistry();

    Registry<QuestRegistries.RewardEntry> getQuestRewardRegistry();

    Registry<QuestRegistries.ObjectiveEntry> getQuestObjectiveRegistry();

    Registry<QuestRegistries.TriggerEntry> getQuestTriggerRegistry();

    Registry<QuestRegistries.DialogueAnswerEntry> getQuestDialogueAnswerRegistry();

    Registry<HappinessRegistry.HappinessFactorTypeEntry> getHappinessTypeRegistry();

    Registry<HappinessRegistry.HappinessFunctionEntry> getHappinessFunctionRegistry();

    void onRegistryNewRegistry(NewRegistryEvent event);

    Registry<EquipmentTypeEntry> getEquipmentTypeRegistry();

    EventBus getEventBus();
}
