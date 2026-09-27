package no.monopixel.slimcolonies.core.client.render.mobs.norsemen;

import no.monopixel.slimcolonies.api.entity.mobs.AbstractEntityMinecoloniesMonster;
import no.monopixel.slimcolonies.core.client.model.raiders.ModelShieldmaiden;
import no.monopixel.slimcolonies.core.event.ClientRegistryHandler;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

/**
 * Renderer used for the shieldmaiden.
 */
public class RendererShieldmaidenNorsemen extends AbstractRendererNorsemen<AbstractEntityMinecoloniesMonster, ModelShieldmaiden>
{
    /**
     * Texture of the entity.
     */
    private static final ResourceLocation TEXTURE1 = new ResourceLocation("slimcolonies", "textures/entity/raiders/norsemen_shieldmaiden1.png");
    private static final ResourceLocation TEXTURE2 = new ResourceLocation("slimcolonies", "textures/entity/raiders/norsemen_shieldmaiden2.png");

    /**
     * Constructor method for renderer
     *
     * @param context the renderManager
     */
    public RendererShieldmaidenNorsemen(final EntityRendererProvider.Context context)
    {
        super(context, new ModelShieldmaiden(context.bakeLayer(ClientRegistryHandler.SHIELD_MAIDEN)), 0.5F);
    }

    @NotNull
    @Override
    public ResourceLocation getTextureLocation(final AbstractEntityMinecoloniesMonster entity)
    {
        if (entity.getTextureId() == 1)
        {
            return TEXTURE2;
        }
        return TEXTURE1;
    }
}
