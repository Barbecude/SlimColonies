package no.monopixel.slimcolonies.core.client.render;

import no.monopixel.slimcolonies.api.colony.ICitizenDataView;
import no.monopixel.slimcolonies.api.colony.IColonyView;
import no.monopixel.slimcolonies.api.colony.requestsystem.manager.IRequestManager;
import no.monopixel.slimcolonies.api.colony.requestsystem.resolver.player.IPlayerRequestResolver;
import no.monopixel.slimcolonies.api.colony.requestsystem.resolver.retrying.IRetryingRequestResolver;
import no.monopixel.slimcolonies.api.colony.requestsystem.token.IToken;
import no.monopixel.slimcolonies.api.items.component.ColonyId;
import no.monopixel.slimcolonies.api.util.Log;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.IItemDecorator;

import java.util.HashSet;
import java.util.Set;

public class ClipBoardDecorator implements IItemDecorator
{
    private static IColonyView colonyView;
    private static boolean     render = false;
    private        long        lastChange;

    @Override
    public boolean render(GuiGraphics graphics, Font font, ItemStack stack, int xOffset, int yOffset)
    {
        final long gametime = Minecraft.getInstance().level.getGameTime();

        if (lastChange != gametime && gametime % 40 == 0)
        {
            lastChange = gametime;
            render = !render;
        }

        if (render)
        {
            colonyView = ColonyId.readColonyViewFromItemStack(stack);
            if (colonyView != null)
            {
                try
                {

                    final IRequestManager requestManager = colonyView.getRequestManager();
                    if (requestManager != null)
                    {
                        final IPlayerRequestResolver resolver = requestManager.getPlayerResolver();
                        final IRetryingRequestResolver retryingRequestResolver = requestManager.getRetryingRequestResolver();

                        final Set<IToken<?>> requestTokens = new HashSet<>();
                        requestTokens.addAll(resolver.getAllAssignedRequests());
                        requestTokens.addAll(retryingRequestResolver.getAllAssignedRequests());

                        for (final ICitizenDataView view : colonyView.getCitizens().values())
                        {
                            if (view.getJobView() != null)
                            {
                                requestTokens.removeAll(view.getJobView().getAsyncRequests());
                            }
                        }

                        if (!requestTokens.isEmpty())
                        {
                            final PoseStack ps = graphics.pose();
                            ps.pushPose();
                            ps.translate(0, 0, 200);
                            graphics.drawCenteredString(font,
                                Component.literal(requestTokens.size() + ""),
                                xOffset + 15,
                                yOffset - 2,
                                0xFFFF4500);
                            ps.popPose();
                            return true;
                        }

                    }
                }
                catch (Exception e)
                {
                    Log.getLogger().error("Something went wrong with the clipboard item decorator", e);
                }
            }
        }
        return false;
    }
}