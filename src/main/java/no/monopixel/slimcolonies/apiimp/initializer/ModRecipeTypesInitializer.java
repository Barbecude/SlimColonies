package no.monopixel.slimcolonies.apiimp.initializer;

import no.monopixel.slimcolonies.api.crafting.ClassicRecipe;
import no.monopixel.slimcolonies.api.crafting.ModRecipeTypes;
import no.monopixel.slimcolonies.api.crafting.MultiOutputRecipe;
import no.monopixel.slimcolonies.api.crafting.registry.RecipeTypeEntry;
import no.monopixel.slimcolonies.api.util.constant.Constants;
import no.monopixel.slimcolonies.apiimp.CommonMinecoloniesAPIImpl;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModRecipeTypesInitializer
{
    public final static DeferredRegister<RecipeTypeEntry> DEFERRED_REGISTER = DeferredRegister.create(CommonMinecoloniesAPIImpl.RECIPE_TYPE_ENTRIES, Constants.MOD_ID);

    private ModRecipeTypesInitializer()
    {
        throw new IllegalStateException("Tried to initialize: ModRecipeTypesInitializer but this is a Utility class.");
    }

    static
    {
        ModRecipeTypes.Classic = DEFERRED_REGISTER.register(ModRecipeTypes.CLASSIC_ID.getPath(), () -> new RecipeTypeEntry.Builder()
                                .setRecipeTypeProducer(ClassicRecipe::new)
                                .setRegistryName(ModRecipeTypes.CLASSIC_ID)
                                .createRecipeTypeEntry());

        ModRecipeTypes.MultiOutput = DEFERRED_REGISTER.register(ModRecipeTypes.MULTI_OUTPUT_ID.getPath(), () -> new RecipeTypeEntry.Builder()
                                .setRecipeTypeProducer(MultiOutputRecipe::new)
                                .setRegistryName(ModRecipeTypes.MULTI_OUTPUT_ID)
                                .createRecipeTypeEntry());
    }
}
