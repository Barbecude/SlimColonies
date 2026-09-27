package no.monopixel.slimcolonies.core.entity.mobs.raider.egyptians;

import no.monopixel.slimcolonies.api.entity.mobs.egyptians.AbstractEntityEgyptianRaider;
import no.monopixel.slimcolonies.api.entity.mobs.egyptians.IMeleeMummyEntity;
import no.monopixel.slimcolonies.core.entity.pathfinding.navigation.MovementHandler;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/**
 * Class for the Mummy entity.
 */
public class EntityMummyRaider extends AbstractEntityEgyptianRaider implements IMeleeMummyEntity
{

    /**
     * Constructor of the entity.
     *
     * @param type    the entity type.
     * @param worldIn world to construct it in.
     */
    public EntityMummyRaider(final EntityType<? extends EntityMummyRaider> type, final Level worldIn)
    {
        super(type, worldIn);
        this.moveControl = new MovementHandler(this);
    }
}
