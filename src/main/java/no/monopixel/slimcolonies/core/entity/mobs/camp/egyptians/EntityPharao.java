package no.monopixel.slimcolonies.core.entity.mobs.camp.egyptians;

import no.monopixel.slimcolonies.api.entity.mobs.egyptians.AbstractEntityEgyptian;
import no.monopixel.slimcolonies.api.entity.mobs.egyptians.IPharaoEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

import static no.monopixel.slimcolonies.api.entity.mobs.RaiderMobUtils.MOB_ATTACK_DAMAGE;
import static no.monopixel.slimcolonies.core.colony.events.raid.RaiderConstants.CHIEF_BONUS_ARMOR;

/**
 * Class for the Pharao entity.
 */
public class EntityPharao extends AbstractEntityEgyptian implements IPharaoEntity
{

    /**
     * Constructor of the entity.
     *
     * @param type    the entity type.
     * @param worldIn world to construct it in.
     */
    public EntityPharao(final EntityType<? extends EntityPharao> type, final Level worldIn)
    {
        super(type, worldIn);
    }

    @Override
    public void initStatsFor(final double baseHealth, final double difficulty, final double baseDamage)
    {
        super.initStatsFor(baseHealth, difficulty, baseDamage);
        final double chiefArmor = difficulty * CHIEF_BONUS_ARMOR;
        this.getAttribute(Attributes.ARMOR).setBaseValue(chiefArmor);
        this.getAttribute(MOB_ATTACK_DAMAGE).setBaseValue(baseDamage + 1.0);
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(baseHealth * 4.5);
        this.setHealth(this.getMaxHealth());
    }
}
