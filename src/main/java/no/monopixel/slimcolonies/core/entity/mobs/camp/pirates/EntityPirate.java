package no.monopixel.slimcolonies.core.entity.mobs.camp.pirates;

import no.monopixel.slimcolonies.api.entity.mobs.pirates.AbstractEntityPirate;
import no.monopixel.slimcolonies.api.entity.mobs.pirates.IMeleePirateEntity;
import no.monopixel.slimcolonies.core.entity.pathfinding.navigation.MovementHandler;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/**
 * Class for the Pirate entity.
 */
public class EntityPirate extends AbstractEntityPirate implements IMeleePirateEntity
{

    /**
     * Constructor of the entity.
     *
     * @param type    the entity type.
     * @param worldIn world to construct it in.
     */
    public EntityPirate(final EntityType<? extends EntityPirate> type, final Level worldIn)
    {
        super(type, worldIn);
        this.moveControl = new MovementHandler(this);
    }
}
