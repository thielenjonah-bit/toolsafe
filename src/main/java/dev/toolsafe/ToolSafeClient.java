package dev.toolsafe;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class ToolSafeClient implements ClientModInitializer {
    private static KeyBinding openKey;

    @Override
    public void onInitializeClient() {
        ModConfig.load();

        openKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.toolsafe.open", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_K,
                KeyBinding.Category.create(Identifier.of("toolsafe", "main"))));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openKey.wasPressed()) {
                client.setScreen(new ToolSafeScreen(client.currentScreen));
            }
        });

        // Schlagen / Abbauen starten
        AttackBlockCallback.EVENT.register((player, world, hand, pos, dir) ->
                block(player, world.isClient(), player.getStackInHand(hand)) ? ActionResult.FAIL : ActionResult.PASS);

        AttackEntityCallback.EVENT.register((player, world, hand, entity, hit) ->
                block(player, world.isClient(), player.getStackInHand(hand)) ? ActionResult.FAIL : ActionResult.PASS);

        // Rechtsklick auf Block (Axt entrinden, Hacke pflügen, Schaufel, Feuerzeug, Schere)
        UseBlockCallback.EVENT.register((player, world, hand, hit) -> {
            ItemStack s = player.getStackInHand(hand);
            boolean tool = s.isIn(ItemTags.AXES) || s.isIn(ItemTags.SHOVELS) || s.isIn(ItemTags.HOES)
                    || s.isOf(Items.FLINT_AND_STEEL) || s.isOf(Items.SHEARS);
            return tool && block(player, world.isClient(), s) ? ActionResult.FAIL : ActionResult.PASS;
        });

        // Rechtsklick in die Luft (Bogen, Schild, Angel, Dreizack, Speer, ...)
        UseItemCallback.EVENT.register((player, world, hand) -> {
            ItemStack s = player.getStackInHand(hand);
            if (s.contains(DataComponentTypes.EQUIPPABLE)) { // Rüstung, Elytra: Anlegen bleibt erlaubt
                return ActionResult.PASS;
            }
            return block(player, world.isClient(), s) ? ActionResult.FAIL : ActionResult.PASS;
        });
    }

    /** true = Aktion blockieren (nur auf dem Client, Item hat nur noch 1 Haltbarkeit). */
    private static boolean block(PlayerEntity player, boolean clientSide, ItemStack stack) {
        if (!clientSide || !ModConfig.enabled) return false;
        if (!stack.isDamageable()) return false; // Baublöcke, Essen, unzerstörbare Items usw.
        if (stack.getDamage() < stack.getMaxDamage() - 1) return false;
        player.sendMessage(Text.translatable("toolsafe.blocked"), true);
        return true;
    }
}
