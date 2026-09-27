package no.monopixel.slimcolonies.core.generation.defaults.workers;

import no.monopixel.slimcolonies.api.colony.jobs.ModJobs;
import no.monopixel.slimcolonies.api.crafting.ItemStorage;
import no.monopixel.slimcolonies.api.items.ModItems;
import no.monopixel.slimcolonies.api.research.util.ResearchConstants;
import no.monopixel.slimcolonies.core.generation.CustomRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.component.ItemLore;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static no.monopixel.slimcolonies.api.util.constant.BuildingConstants.MODULE_CRAFTING;

/**
 * Datagen for Alchemist
 */
public class DefaultAlchemistCraftingProvider extends CustomRecipeProvider
{
    private final String ALCHEMIST = ModJobs.ALCHEMIST_ID.getPath();

    public DefaultAlchemistCraftingProvider(@NotNull final PackOutput packOutput, final CompletableFuture<HolderLookup.Provider> lookupProvider)
    {
        super(packOutput, lookupProvider);
    }

    @NotNull
    @Override
    public String getName()
    {
        return "DefaultAlchemistCraftingProvider";
    }

    @Override
    protected void registerRecipes(@NotNull Consumer<CustomRecipeBuilder> consumer)
    {
        recipe(ALCHEMIST, MODULE_CRAFTING, "magicpotion")
                .inputs(List.of(new ItemStorage(new ItemStack(ModItems.mistletoe)),
                        new ItemStorage(ModItems.large_water_bottle.getDefaultInstance())))
                .result(new ItemStack(ModItems.magicpotion))
                .minResearchId(ResearchConstants.DRUID_USE_POTIONS)
                .showTooltip(true)
                .build(consumer);

        // this isn't a real recipe, it's just here to conveniently generate something for the quest
        final ItemStack suspiciousPotion = PotionContents.createItemStack(Items.POTION, Potions.POISON);
        suspiciousPotion.set(DataComponents.ITEM_NAME, Component.translatable("no.monopixel.slimcolonies.alchemyquestpotion.name"));
        suspiciousPotion.set(DataComponents.LORE, new ItemLore(List.of(Component.translatable("no.monopixel.slimcolonies.alchemyquestpotion.lore"))));
        //recipe(ALCHEMIST, MODULE_CRAFTING, "questpotion").result(suspiciousPotion).minBuildingLevel(10).build(consumer);
    }
}
