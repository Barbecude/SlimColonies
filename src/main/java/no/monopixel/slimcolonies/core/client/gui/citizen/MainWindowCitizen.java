package no.monopixel.slimcolonies.core.client.gui.citizen;

import com.ldtteam.blockui.PaneBuilders;
import com.ldtteam.blockui.controls.Button;
import com.ldtteam.blockui.controls.Image;
import com.ldtteam.blockui.controls.Text;
import com.ldtteam.blockui.views.View;
import no.monopixel.slimcolonies.api.colony.ICitizenDataView;
import no.monopixel.slimcolonies.api.entity.citizen.Skill;
import no.monopixel.slimcolonies.api.util.constant.Constants;
import no.monopixel.slimcolonies.core.network.messages.server.colony.citizen.AdjustSkillCitizenMessage;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import static no.monopixel.slimcolonies.api.util.constant.WindowConstants.*;

/**
 * BOWindow for the citizen.
 */
//TODO: Rework the layout, skills should be icons or shortform text with amount, left to right. This leaves more space and allows us to add missing important information such as current Job, home/work location etc
public class MainWindowCitizen extends AbstractWindowCitizen
{
    /**
     * Tick function for updating every second.
     */
    private int tick = 0;

    /**
     * Constructor to initiate the citizen windows.
     *
     * @param citizen citizen to bind the window to.
     */
    public MainWindowCitizen(final ICitizenDataView citizen)
    {
        super(citizen, new ResourceLocation(Constants.MOD_ID, "gui/citizen/main.xml"));

        final Image statusIcon = findPaneOfTypeByID(STATUS_ICON, Image.class);
        if (citizen.getVisibleStatus() == null)
        {
            statusIcon.hide();
        }
        else
        {
            statusIcon.show();
            statusIcon.setImage(citizen.getVisibleStatus().getIcon(), false);
            PaneBuilders.tooltipBuilder()
                .append(Component.translatable(citizen.getVisibleStatus().getTranslationKey()))
                .hoverPane(statusIcon)
                .build();
        }
    }

    public ICitizenDataView getCitizen()
    {
        return citizen;
    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();

        if (tick++ == 20)
        {
            tick = 0;
            CitizenWindowUtils.createSkillContent(citizen, this);
        }
    }

    /**
     * Called when the gui is opened by an player.
     */
    @Override
    public void onOpened()
    {
        super.onOpened();
        findPaneOfTypeByID(WINDOW_ID_NAME, Text.class).setText(Component.literal(citizen.getName()));

        CitizenWindowUtils.createHealthBar(citizen, findPaneOfTypeByID(WINDOW_ID_HEALTHBAR, View.class));
        CitizenWindowUtils.createSaturationBar(citizen, this);
        CitizenWindowUtils.createHappinessBar(citizen, this);
        CitizenWindowUtils.createSkillContent(citizen, this);

        //Tool of class:§rwith minimal level:§rWood or Gold§r and§rwith maximal level:§rWood or Gold§r

        if (citizen.isFemale())
        {
            findPaneOfTypeByID(WINDOW_ID_GENDER, Image.class).setImage(ResourceLocation.parse(FEMALE_SOURCE), false);
        }
    }

    /**
     * Called when a button in the citizen has been clicked.
     *
     * @param button the clicked button.
     */
    @Override
    public void onButtonClicked(@NotNull final Button button)
    {
        super.onButtonClicked(button);
        if (button.getID().contains(PLUS_PREFIX))
        {
            final String label = button.getID().replace(PLUS_PREFIX, "");
            final Skill skill = Skill.valueOf(StringUtils.capitalize(label));

            new AdjustSkillCitizenMessage(colony, citizen, 1, skill).sendToServer();
        }
        else if (button.getID().contains(MINUS_PREFIX))
        {
            final String label = button.getID().replace(MINUS_PREFIX, "");
            final Skill skill = Skill.valueOf(StringUtils.capitalize(label));

            new AdjustSkillCitizenMessage(colony, citizen, -1, skill).sendToServer();
        }
    }
}
