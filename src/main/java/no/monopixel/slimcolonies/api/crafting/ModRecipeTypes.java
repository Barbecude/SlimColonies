package no.monopixel.slimcolonies.api.crafting;

import no.monopixel.slimcolonies.api.crafting.registry.RecipeTypeEntry;
import no.monopixel.slimcolonies.api.util.constant.Constants;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;

public final class ModRecipeTypes
{

    public static final ResourceLocation CLASSIC_ID    = new ResourceLocation(Constants.MOD_ID, "classic");
    public static final ResourceLocation MULTI_OUTPUT_ID    = new ResourceLocation(Constants.MOD_ID, "multi_output");

    public static DeferredHolder<RecipeTypeEntry, RecipeTypeEntry> Classic;
    public static DeferredHolder<RecipeTypeEntry, RecipeTypeEntry> MultiOutput;

    private ModRecipeTypes()
    {
        throw new IllegalStateException("Tried to initialize: ModJobs but this is a Utility class.");
    }
}
