package no.monopixel.slimcolonies.core.recipes;

import no.monopixel.slimcolonies.api.MinecoloniesAPIProxy;
import no.monopixel.slimcolonies.api.compatibility.ICompatibilityManager;
import no.monopixel.slimcolonies.api.crafting.GenericRecipe;
import no.monopixel.slimcolonies.api.crafting.IGenericRecipe;
import no.monopixel.slimcolonies.api.crafting.ModCraftingTypes;
import no.monopixel.slimcolonies.api.crafting.registry.CraftingType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * A crafting type for brewing recipes
 */
public class BrewingCraftingType extends CraftingType
{
    public BrewingCraftingType()
    {
        super(ModCraftingTypes.BREWING_ID);
    }

    @Override
    @NotNull
    public List<IGenericRecipe> findRecipes(@NotNull RecipeManager recipeManager, @Nullable Level world)
    {
        final List<IGenericRecipe> recipes = new ArrayList<>();
        final ICompatibilityManager compatibilityManager = MinecoloniesAPIProxy.getInstance().getColonyManager().getCompatibilityManager();

        final List<ItemStack> containers = compatibilityManager.getListOfAllItems().stream()
                .filter(world.potionBrewing()::isInput)
                .toList();
        final List<ItemStack> ingredients = compatibilityManager.getListOfAllItems().stream()
                .filter(world.potionBrewing()::isIngredient)
                .toList();

        for (final ItemStack container : containers)
        {
            for (final ItemStack ingredient : ingredients)
            {
                final ItemStack output = world.potionBrewing().mix(ingredient, container);
                if (!output.isEmpty() && output != container)
                {
                    recipes.add(GenericRecipe.builder()
                            .withOutput(output.copyWithCount(3))
                            .withInputs(List.of(List.of(ingredient), List.of(container.copyWithCount(3))))
                            .withIntermediate(Blocks.BREWING_STAND)
                            .build());
                }
            }
        }

        return recipes;
    }
}
