package no.monopixel.slimcolonies.core.client.gui.citizen;

import com.ldtteam.blockui.PaneBuilders;
import no.monopixel.slimcolonies.api.colony.ICitizenDataView;
import no.monopixel.slimcolonies.api.colony.IColonyView;
import no.monopixel.slimcolonies.api.colony.buildings.ModBuildings;
import no.monopixel.slimcolonies.api.colony.buildings.views.IBuildingView;
import no.monopixel.slimcolonies.core.client.gui.AbstractWindowSkeleton;
import no.monopixel.slimcolonies.core.colony.buildings.views.AbstractBuildingView;
import no.monopixel.slimcolonies.core.debug.DebugPlayerManager;
import no.monopixel.slimcolonies.core.debug.gui.DebugWindowCitizen;
import no.monopixel.slimcolonies.core.network.messages.server.colony.OpenInventoryMessage;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * BOWindow for the citizen.
 */
public abstract class AbstractWindowCitizen extends AbstractWindowSkeleton
{
    protected final IColonyView colony;

    /**
     * The citizenData.View object.
     */
    protected final ICitizenDataView citizen;

    /**
     * Constructor to initiate the citizen windows.
     *
     * @param citizen citizen to bind the window to.
     * @param ui the xml res loc.
     */
    public AbstractWindowCitizen(final ICitizenDataView citizen, final ResourceLocation ui)
    {
        super(ui);
        this.colony = citizen.getColony();
        this.citizen = citizen;

        registerButton("mainTab", () -> new MainWindowCitizen(citizen).open());
        registerButton("mainIcon", () -> new MainWindowCitizen(citizen).open());
        PaneBuilders.tooltipBuilder().hoverPane(findPaneByID("mainIcon")).build().setText(Component.translatableEscape("no.monopixel.slimcolonies.coremod.gui.citizen.main"));

        registerButton("requestTab", () -> new RequestWindowCitizen(citizen).open());
        registerButton("requestIcon", () -> new RequestWindowCitizen(citizen).open());
        PaneBuilders.tooltipBuilder().hoverPane(findPaneByID("requestIcon")).build().setText(Component.translatableEscape("no.monopixel.slimcolonies.coremod.gui.citizen.requests"));

        registerButton("inventoryTab", () -> new OpenInventoryMessage(citizen.getColony(), citizen.getName(), citizen.getEntityId()).sendToServer());
        registerButton("inventoryIcon", () -> new OpenInventoryMessage(citizen.getColony(), citizen.getName(), citizen.getEntityId()).sendToServer());
        PaneBuilders.tooltipBuilder().hoverPane(findPaneByID("inventoryIcon")).build().setText(Component.translatableEscape("no.monopixel.slimcolonies.coremod.gui.citizen.inventory"));

        registerButton("happinessTab", () -> new HappinessWindowCitizen(citizen).open());
        registerButton("happinessIcon", () -> new HappinessWindowCitizen(citizen).open());
        PaneBuilders.tooltipBuilder().hoverPane(findPaneByID("happinessIcon")).build().setText(Component.translatableEscape("no.monopixel.slimcolonies.coremod.gui.citizen.happiness"));

        registerButton("familyTab", () -> new FamilyWindowCitizen(citizen).open());
        registerButton("familyIcon", () -> new FamilyWindowCitizen(citizen).open());
        PaneBuilders.tooltipBuilder().hoverPane(findPaneByID("familyIcon")).build().setText(Component.translatableEscape("no.monopixel.slimcolonies.coremod.gui.citizen.family"));

        if (DebugPlayerManager.hasDebugEnabled(mc.player))
        {
            findPaneByID("debugTab").setVisible(true);
            findPaneByID("debugIcon").setVisible(true);
            registerButton("debugTab", () -> new DebugWindowCitizen(citizen).open());
            registerButton("debugIcon", () -> new DebugWindowCitizen(citizen).open());
            PaneBuilders.singleLineTooltip(Component.translatable("no.monopixel.slimcolonies.coremod.debug.gui.tabicon"), findPaneByID("debugIcon"));
        }

        final IBuildingView building = citizen.getColony().getClientBuildingManager().getBuilding(citizen.getWorkBuilding());

        if (building instanceof AbstractBuildingView && building.getBuildingType() != ModBuildings.library.get())
        {
            findPaneByID("jobTab").setVisible(true);
            findPaneByID("jobIcon").setVisible(true);

            registerButton("jobTab", () -> new JobWindowCitizen(citizen).open());
            registerButton("jobIcon", () -> new JobWindowCitizen(citizen).open());
            PaneBuilders.tooltipBuilder().hoverPane(findPaneByID("jobIcon")).build().setText(Component.translatableEscape("no.monopixel.slimcolonies.coremod.gui.citizen.job"));
        }
        else
        {
            findPaneByID("jobTab").setVisible(false);
            findPaneByID("jobIcon").setVisible(false);
        }
    }
}
