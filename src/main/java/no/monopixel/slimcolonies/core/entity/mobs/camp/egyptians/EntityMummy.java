package no.monopixel.slimcolonies.core.entity.mobs.camp.egyptians;

import no.monopixel.slimcolonies.api.entity.mobs.egyptians.AbstractEntityEgyptian;
import no.monopixel.slimcolonies.api.entity.mobs.egyptians.IMeleeMummyEntity;
import no.monopixel.slimcolonies.core.entity.pathfinding.navigation.MovementHandler;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/**
 * Class for the Mummy entity.
 */
public class EntityMummy extends AbstractEntityEgyptian implements IMeleeMummyEntity
{

    /**
     * Constructor of the entity.
     *
     * @param type    the entity type.
     * @param worldIn world to construct it in.
     */
    public EntityMummy(final EntityType<? extends EntityMummy> type, final Level worldIn)
    {
        super(type, worldIn);
        this.moveControl = new MovementHandler(this);
    }
}
