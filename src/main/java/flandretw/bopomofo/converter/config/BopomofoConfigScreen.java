package flandretw.bopomofo.converter.config;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
//? if >=26.1 {
/*import net.minecraft.client.gui.GuiGraphicsExtractor;
*///?} else {
import net.minecraft.client.gui.GuiGraphics;
//?}
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

public class BopomofoConfigScreen extends Screen {
    private final Screen parent;
    private final BopomofoConfig config;
    private Button resetBtn;

    public BopomofoConfigScreen(Screen parent) {
        super(Component.translatable("bopomofo.config.title"));
        this.parent = parent;
        this.config = BopomofoConfig.getInstance();
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int buttonWidth = 200;
        int buttonHeight = 20;
        int totalHeight = 166;
        int startY = Math.max(15, (this.height - totalHeight) / 2);
        int btn0Y = startY + 28;

        // Color toggle button
        ChatFormatting[] colors = new ChatFormatting[] {
            ChatFormatting.BLACK, ChatFormatting.DARK_BLUE, ChatFormatting.DARK_GREEN, ChatFormatting.DARK_AQUA,
            ChatFormatting.DARK_RED, ChatFormatting.DARK_PURPLE, ChatFormatting.GOLD, ChatFormatting.GRAY,
            ChatFormatting.DARK_GRAY, ChatFormatting.BLUE, ChatFormatting.GREEN, ChatFormatting.AQUA,
            ChatFormatting.RED, ChatFormatting.LIGHT_PURPLE, ChatFormatting.YELLOW, ChatFormatting.WHITE
        };
        Button colorBtn = Button.builder(getColorText(), button -> {
            int curIdx = 15;
            for (int i = 0; i < colors.length; i++) {
                if (colors[i] == config.textColor) {
                    curIdx = i;
                    break;
                }
            }
            config.textColor = colors[(curIdx + 1) % colors.length];
            button.setMessage(getColorText());
            updateResetButton();
        }).bounds(centerX - buttonWidth / 2, btn0Y, buttonWidth, buttonHeight).build();
        this.addRenderableWidget(colorBtn);

        // Bold toggle
        Button boldBtn = Button.builder(getBoolText("bopomofo.config.bold", config.bold, ChatFormatting.BOLD), button -> {
            config.bold = !config.bold;
            button.setMessage(getBoolText("bopomofo.config.bold", config.bold, ChatFormatting.BOLD));
            updateResetButton();
        }).bounds(centerX - buttonWidth / 2, btn0Y + 24, buttonWidth, buttonHeight).build();
        this.addRenderableWidget(boldBtn);

        // Italic toggle
        Button italicBtn = Button.builder(getBoolText("bopomofo.config.italic", config.italic, ChatFormatting.ITALIC), button -> {
            config.italic = !config.italic;
            button.setMessage(getBoolText("bopomofo.config.italic", config.italic, ChatFormatting.ITALIC));
            updateResetButton();
        }).bounds(centerX - buttonWidth / 2, btn0Y + 48, buttonWidth, buttonHeight).build();
        this.addRenderableWidget(italicBtn);

        // Underline toggle
        Button underlineBtn = Button.builder(getBoolText("bopomofo.config.underline", config.underline, ChatFormatting.UNDERLINE), button -> {
            config.underline = !config.underline;
            button.setMessage(getBoolText("bopomofo.config.underline", config.underline, ChatFormatting.UNDERLINE));
            updateResetButton();
        }).bounds(centerX - buttonWidth / 2, btn0Y + 72, buttonWidth, buttonHeight).build();
        this.addRenderableWidget(underlineBtn);

        // Reset and Done buttons
        int halfWidth = (buttonWidth - 4) / 2;
        this.resetBtn = Button.builder(Component.translatable("bopomofo.config.reset"), button -> {
            config.reset();
            colorBtn.setMessage(getColorText());
            boldBtn.setMessage(getBoolText("bopomofo.config.bold", config.bold, ChatFormatting.BOLD));
            italicBtn.setMessage(getBoolText("bopomofo.config.italic", config.italic, ChatFormatting.ITALIC));
            underlineBtn.setMessage(getBoolText("bopomofo.config.underline", config.underline, ChatFormatting.UNDERLINE));
            updateResetButton();
        }).bounds(centerX - buttonWidth / 2, startY + 146, halfWidth, buttonHeight).build();
        this.addRenderableWidget(this.resetBtn);

        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, button -> {
            config.save();
            closeScreen();
        }).bounds(centerX + 2, startY + 146, halfWidth, buttonHeight).build());

        updateResetButton();
    }

    private void updateResetButton() {
        if (this.resetBtn != null) {
            this.resetBtn.active = !this.config.isDefault();
        }
    }

    private void closeScreen() {
        //? if >=26.2 {
        /*this.minecraft.gui.setScreen(this.parent);
        *///?} else {
        this.minecraft.setScreen(this.parent);
        //?}
    }

    private Component getColorText() {
        String name = config.textColor.name().toLowerCase(java.util.Locale.ROOT);
        Component colorName = Component.translatable("bopomofo.color." + name)
                .withStyle(config.textColor);
        return Component.translatable("bopomofo.config.format", Component.translatable("bopomofo.config.color"), colorName);
    }

    private Component getBoolText(String key, boolean value, ChatFormatting formatting) {
        Component status = CommonComponents.optionStatus(value);
        if (value && formatting != null) {
            status = status.copy().withStyle(formatting);
        }
        return Component.translatable("bopomofo.config.format", Component.translatable(key), status);
    }

    @Override
    public void onClose() {
        config.save();
        closeScreen();
    }

    //? if >=26.1 {
    /*@Override
    public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        super.extractRenderState(context, mouseX, mouseY, delta);
        int totalHeight = 166;
        int startY = Math.max(15, (this.height - totalHeight) / 2);
        context.centeredText(this.font, this.title, this.width / 2, startY, 0xFFFFFFFF);
        context.centeredText(this.font, Component.translatable("bopomofo.config.warning"), this.width / 2, startY + 128, 0xFFFF5555);
    }
    *///?} else {
    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        //? if <=1.20.1 {
        /*this.renderBackground(context);
        *///?}
        super.render(context, mouseX, mouseY, delta);
        int totalHeight = 166;
        int startY = Math.max(15, (this.height - totalHeight) / 2);
        context.drawCenteredString(this.font, this.title, this.width / 2, startY, 0xFFFFFFFF);
        context.drawCenteredString(this.font, Component.translatable("bopomofo.config.warning"), this.width / 2, startY + 128, 0xFFFF5555);
    }
    //?}
}
