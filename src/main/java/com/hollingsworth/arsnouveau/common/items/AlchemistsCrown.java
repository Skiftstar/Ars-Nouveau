package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.api.item.IRadialProvider;
import com.hollingsworth.arsnouveau.api.potion.PotionData;
import com.hollingsworth.arsnouveau.client.gui.radial_menu.GuiRadialMenu;
import com.hollingsworth.arsnouveau.client.gui.radial_menu.RadialMenu;
import com.hollingsworth.arsnouveau.client.gui.radial_menu.RadialMenuSlot;
import com.hollingsworth.arsnouveau.client.gui.utils.RenderUtils;
import com.hollingsworth.arsnouveau.client.keybindings.ModKeyBindings;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketConsumePotion;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class AlchemistsCrown extends ModItem implements IRadialProvider {

    public AlchemistsCrown(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip2, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip2, flagIn);
        tooltip2.add(Component.translatable("ars_nouveau.tooltip.alchemists_crown",  KeyMapping.createNameSupplier(ModKeyBindings.HEAD_CURIO_HOTKEY.getName()).get()));
    }

    @Override
    public int forKey() {
        return ModKeyBindings.HEAD_CURIO_HOTKEY.getKey().getValue();
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onRadialKeyPressed(ItemStack stack, Player player) {
        List<RadialMenuSlot<SlotData>> slots = new ArrayList<>();
        for(int i = 0; i < player.inventory.getContainerSize(); i++) {
            if(slots.size() >= 9)
                break;
            ItemStack item = player.inventory.getItem(i);
            PotionData potionData = new PotionData(item);
            if(potionData.isEmpty())
                continue;
            slots.add(new RadialMenuSlot<>(item.getHoverName().getString(), new AlchemistsCrown.SlotData(i, item)));
        }
        if(slots.isEmpty()) {
            PortUtil.sendMessage(Minecraft.getInstance().player, Component.translatable("ars_nouveau.alchemists_crown.no_flasks"));
            return;
        }
        Minecraft.getInstance().setScreen(new GuiRadialMenu<>(new RadialMenu<>((int index) -> {
            Networking.INSTANCE.sendToServer(new PacketConsumePotion(slots.get(index).primarySlotIcon().slot));
        }, slots, (slotData, posestack, positionx, posy, size, transparent) -> RenderUtils.drawItemAsIcon(slotData.stack, posestack, positionx, posy, size, transparent), 3)));
    }

    public record SlotData(int slot, ItemStack stack){
        public int getSlot() {
            return slot;
        }
        public ItemStack getStack() {
            return stack;
        }
    }
}
