package no.monopixel.slimcolonies.core.entity.mobs.raider.egyptians;

import no.monopixel.slimcolonies.api.entity.mobs.egyptians.AbstractEntityEgyptianRaider;
import no.monopixel.slimcolonies.api.entity.mobs.egyptians.IArcherMummyEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/**
 * Class for the Archer mummy entity.
 */
public class EntityArcherMummyRaider extends AbstractEntityEgyptianRaider implements IArcherMummyEntity
{
    /**
     * Constructor of the entity.
     *
     * @param worldIn world to construct it in.
     * @param type    the entity type.
     */
    public EntityArcherMummyRaider(final EntityType<? extends EntityArcherMummyRaider> type, final Level worldIn)
    {
        super(type, worldIn);
    }
}
