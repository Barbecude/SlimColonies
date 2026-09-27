package no.monopixel.slimcolonies.core.items;

import no.monopixel.slimcolonies.core.client.render.worldevent.ColonyBlueprintRenderer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static no.monopixel.slimcolonies.apiimp.initializer.ModItemsInitializer.GOGGLES;

public class ItemBuildGoggles extends ArmorItem
{
    /**
     * Constructor
     *
     * @param name            the name.
     * @param properties      the item properties.
     */
    public ItemBuildGoggles(
            @NotNull final String name,
            final Item.Properties properties)
    {
        super(GOGGLES, Type.HELMET, properties.setNoRepair().rarity(Rarity.UNCOMMON));
    }

    @Override
    public void appendHoverText(@NotNull final ItemStack stack,
                                @Nullable final TooltipContext ctx,
                                @NotNull final List<Component> components,
                                @NotNull final TooltipFlag flags)
    {
        super.appendHoverText(stack, ctx, components, flags);

        components.add(Component.translatableEscape("\"%s\"",
                        Component.translatableEscape("item.slimcolonies.build_goggles.lore")
                                .withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.ITALIC))
                .withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));

        components.add(Component.translatableEscape(ColonyBlueprintRenderer.willRenderBlueprints()
                ? "item.slimcolonies.build_goggles.enabled" : "item.slimcolonies.build_goggles.disabled")
                .withStyle(ChatFormatting.GRAY));
    }
}
