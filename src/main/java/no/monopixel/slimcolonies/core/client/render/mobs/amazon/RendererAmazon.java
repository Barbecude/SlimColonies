package no.monopixel.slimcolonies.core.client.render.mobs.amazon;

import no.monopixel.slimcolonies.api.entity.mobs.AbstractEntityMinecoloniesMonster;
import no.monopixel.slimcolonies.core.client.model.raiders.ModelAmazon;
import no.monopixel.slimcolonies.core.event.ClientRegistryHandler;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

/**
 * Renderer used for archer amazons.
 */
public class RendererAmazon extends AbstractRendererAmazon<AbstractEntityMinecoloniesMonster, ModelAmazon>
{
    /**
     * Texture of the entity.
     */
    private static final ResourceLocation TEXTURE = new ResourceLocation("slimcolonies", "textures/entity/raiders/amazon.png");

    /**
     * Constructor method for renderer
     *
     * @param context the renderManager
     */
    public RendererAmazon(final EntityRendererProvider.Context context)
    {
        super(context, new ModelAmazon(context.bakeLayer(ClientRegistryHandler.AMAZON)), 0.5F);
    }

    @NotNull
    @Override
    public ResourceLocation getTextureLocation(@NotNull final AbstractEntityMinecoloniesMonster entity)
    {
        return TEXTURE;
    }
}
