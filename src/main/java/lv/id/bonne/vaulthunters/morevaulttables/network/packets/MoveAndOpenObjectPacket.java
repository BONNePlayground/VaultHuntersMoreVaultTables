//
// Created by BONNe
// Copyright - 2024
//


package lv.id.bonne.vaulthunters.morevaulttables.network.packets;


import java.util.function.Supplier;

import lv.id.bonne.vaulthunters.morevaulttables.block.menu.ICraftingSpotManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkEvent;


public class MoveAndOpenObjectPacket
{
    public MoveAndOpenObjectPacket(boolean move)
    {
        this.move = move;
    }


    public static void encode(MoveAndOpenObjectPacket message, FriendlyByteBuf buffer)
    {
        buffer.writeBoolean(message.move);
    }


    public static MoveAndOpenObjectPacket decode(FriendlyByteBuf buffer)
    {
        return new MoveAndOpenObjectPacket(buffer.readBoolean());
    }


    public static void handle(MoveAndOpenObjectPacket message,
        Supplier<NetworkEvent.Context> contextSupplier)
    {
        NetworkEvent.Context context = contextSupplier.get();

        context.enqueueWork(() ->
        {
            ServerPlayer requester = context.getSender();

            if (requester != null)
            {
                AbstractContainerMenu menu = requester.containerMenu;

                if (menu instanceof ICraftingSpotManager container)
                {
                    if (message.move)
                    {
                        container.moveItem();
                    }

                    container.identifySelectedItem(requester);
                    menu.broadcastChanges();
                }
            }
        });
        context.setPacketHandled(true);
    }


    /**
     * Indicate if movement should happen together with identifying.
     */
    private final boolean move;
}
