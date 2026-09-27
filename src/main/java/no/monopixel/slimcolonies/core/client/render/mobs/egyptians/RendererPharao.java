package no.monopixel.slimcolonies.core.client.render.mobs.egyptians;

import no.monopixel.slimcolonies.api.entity.mobs.AbstractEntityMinecoloniesMonster;
import no.monopixel.slimcolonies.core.client.model.raiders.ModelPharaoh;
import no.monopixel.slimcolonies.core.event.ClientRegistryHandler;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

/**
 * Renderer used for the pharao.
 */
public class RendererPharao extends AbstractRendererEgyptian<AbstractEntityMinecoloniesMonster, ModelPharaoh>
{
    /**
     * Texture of the entity.
     */
    private static final ResourceLocation TEXTURE = new ResourceLocation("slimcolonies", "textures/entity/raiders/pharao.png");

    /**
     * Constructor method for renderer
     *
     * @param context the renderManager
     */
    public RendererPharao(final EntityRendererProvider.Context context)
    {
        super(context, new ModelPharaoh(context.bakeLayer(ClientRegistryHandler.PHARAO)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(final AbstractEntityMinecoloniesMonster entity)
    {
        return TEXTURE;
    }
}
