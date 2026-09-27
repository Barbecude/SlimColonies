package no.monopixel.slimcolonies.core.client.render.mobs.egyptians;

import no.monopixel.slimcolonies.api.entity.mobs.AbstractEntityMinecoloniesMonster;
import no.monopixel.slimcolonies.core.client.model.raiders.ModelArcherMummy;
import no.monopixel.slimcolonies.core.event.ClientRegistryHandler;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

/**
 * Renderer used for archer mummy.
 */
public class RendererArcherMummy extends AbstractRendererEgyptian<AbstractEntityMinecoloniesMonster, ModelArcherMummy>
{
    /**
     * Texture of the entity.
     */
    private static final ResourceLocation TEXTURE = new ResourceLocation("slimcolonies", "textures/entity/raiders/archer_mummy.png");

    /**
     * Constructor method for renderer
     *
     * @param context the renderManager
     */
    public RendererArcherMummy(final EntityRendererProvider.Context context)
    {
        super(context, new ModelArcherMummy(context.bakeLayer(ClientRegistryHandler.ARCHER_MUMMY)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(final AbstractEntityMinecoloniesMonster entity)
    {
        return TEXTURE;
    }
}
