package dev.toolsafe;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

public class ToolSafeScreen extends Screen {
    private final Screen parent;

    public ToolSafeScreen(Screen parent) {
        super(Text.translatable("toolsafe.title"));
        this.parent = parent;
    }

    private static Text label() {
        return Text.translatable("toolsafe.option").append(": ").append(ScreenTexts.onOrOff(ModConfig.enabled));
    }

    @Override
    protected void init() {
        addDrawableChild(ButtonWidget.builder(label(), b -> {
            ModConfig.enabled = !ModConfig.enabled;
            ModConfig.save();
            b.setMessage(label());
        }).dimensions(width / 2 - 100, height / 2 - 10, 200, 20).build());

        addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, b -> close())
                .dimensions(width / 2 - 100, height / 2 + 20, 200, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(textRenderer, title, width / 2, height / 2 - 40, 0xFFFFFFFF);
    }

    @Override
    public void close() {
        client.setScreen(parent);
    }
}
