package no.monopixel.slimcolonies.core.quests.rewards;

import com.google.gson.JsonObject;
import net.minecraft.world.entity.player.Player;
import no.monopixel.slimcolonies.api.colony.IColony;
import no.monopixel.slimcolonies.api.colony.managers.interfaces.IRaiderManager.RaidSettings;
import no.monopixel.slimcolonies.api.quests.IQuestInstance;
import no.monopixel.slimcolonies.api.quests.IQuestRewardTemplate;
import org.jetbrains.annotations.Nullable;

import static no.monopixel.slimcolonies.api.quests.QuestParseConstant.DETAILS_KEY;

/**
 * Quest reward template that triggers a colony raid (either immediately or tonight)
 * with an optional custom raidType ID and raider amount.
 */
public class TriggerRaidRewardTemplate implements IQuestRewardTemplate
{
    @Nullable
    private final String raidType;
    @Nullable
    private final Integer amount;
    private final boolean tonight;

    public TriggerRaidRewardTemplate(@Nullable final String raidType, @Nullable final Integer amount, final boolean tonight)
    {
        this.raidType = raidType;
        this.amount = amount;
        this.tonight = tonight;
    }

    public static IQuestRewardTemplate createReward(final JsonObject jsonObject)
    {
        final JsonObject details = jsonObject.has(DETAILS_KEY) ? jsonObject.getAsJsonObject(DETAILS_KEY) : jsonObject;
        final String raidType = details.has("raidType") && !details.get("raidType").getAsString().isBlank()
            ? details.get("raidType").getAsString()
            : null;
        final Integer amount = details.has("amount") && details.get("amount").getAsInt() > 0
            ? details.get("amount").getAsInt()
            : null;
        final boolean tonight = details.has("tonight") && details.get("tonight").getAsBoolean();
        return new TriggerRaidRewardTemplate(raidType, amount, tonight);
    }

    @Override
    public void applyReward(final IColony colony, final Player player, final IQuestInstance colonyQuest)
    {
        final RaidSettings settings = new RaidSettings(true, raidType, false, amount, null);
        if (tonight)
        {
            colony.getRaiderManager().setRaidNextNight(settings);
        }
        else
        {
            colony.getRaiderManager().raiderEvent(settings);
        }
    }
}
