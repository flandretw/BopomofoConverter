package flandretw.bopomofo.translator.config;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

public class BopomofoConfigScreen extends Screen {
    private final Screen parent;
    private final BopomofoConfig config;

    public BopomofoConfigScreen(Screen parent) {
        super(Component.translatable("bopomofo.config.title"));
        this.parent = parent;
        this.config = BopomofoConfig.getInstance();
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int topY = 40;
        int buttonWidth = 200;
        int buttonHeight = 20;

        // 顏色切換按鈕
        this.addRenderableWidget(Button.builder(getColorText(), button -> {
            int next = config.textColor.getId() + 1;
            while (true) {
                if (next > 15)
                    next = 0;
                ChatFormatting f = ChatFormatting.getById(next);
                if (f != null && f.isColor()) {
                    config.textColor = f;
                    break;
                }
                next++;
            }
            button.setMessage(getColorText());
        }).bounds(centerX - buttonWidth / 2, topY, buttonWidth, buttonHeight).build());

        // 粗體開關
        this.addRenderableWidget(Button.builder(getBoolText("bopomofo.config.bold", config.bold), button -> {
            config.bold = !config.bold;
            button.setMessage(getBoolText("bopomofo.config.bold", config.bold));
        }).bounds(centerX - buttonWidth / 2, topY + 24, buttonWidth, buttonHeight).build());

        // 斜體開關
        this.addRenderableWidget(Button.builder(getBoolText("bopomofo.config.italic", config.italic), button -> {
            config.italic = !config.italic;
            button.setMessage(getBoolText("bopomofo.config.italic", config.italic));
        }).bounds(centerX - buttonWidth / 2, topY + 48, buttonWidth, buttonHeight).build());

        // 底線開關
        this.addRenderableWidget(
                Button.builder(getBoolText("bopomofo.config.underline", config.underline), button -> {
                    config.underline = !config.underline;
                    button.setMessage(getBoolText("bopomofo.config.underline", config.underline));
                }).bounds(centerX - buttonWidth / 2, topY + 72, buttonWidth, buttonHeight).build());

        // 確定按鈕
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, button -> {
            config.save();
            this.minecraft.setScreen(this.parent);
        }).bounds(centerX - buttonWidth / 2, this.height - 30, buttonWidth, buttonHeight).build());
    }

    private Component getColorText() {
        String name = config.textColor.getName();
        Component colorName = Component.literal(name.substring(0, 1).toUpperCase() + name.substring(1))
                .withStyle(config.textColor);
        return Component.translatable("bopomofo.config.format", Component.translatable("bopomofo.config.color"), colorName);
    }

    private Component getBoolText(String key, boolean value) {
        return Component.translatable("bopomofo.config.format", Component.translatable(key), CommonComponents.optionStatus(value));
    }

    @Override
    public void onClose() {
        config.save();
        this.minecraft.setScreen(this.parent);
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredString(this.font, this.title, this.width / 2, 15, 0xFFFFFFFF);
        context.drawCenteredString(this.font, Component.translatable("bopomofo.config.warning"), this.width / 2, this.height - 50, 0xFFFF5555);
    }
}
