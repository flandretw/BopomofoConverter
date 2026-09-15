package flandretw.bopomofo.translator.mixin;

import flandretw.bopomofo.translator.BopomofoConverter;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.GuiMessageTag;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ChatComponent.class)
public class ChatComponentMixin {

    @ModifyVariable(method = "addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;Lnet/minecraft/client/GuiMessageTag;)V", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private Component modifyChatMessage(Component originalMessage) {
        if (originalMessage == null)
            return null;
        Component modified = processText(originalMessage);
        return modified != null ? modified : originalMessage;
    }

    private Component processText(Component text) {
        boolean changed = false;

        MutableComponent newText = text.plainCopy();
        Style newStyle = text.getStyle();

        net.minecraft.network.chat.ComponentContents content = text.getContents();
        //? if <1.20.3 {
        /*if (content instanceof net.minecraft.network.chat.contents.LiteralContents plain) {
        *///?} else {
        if (content instanceof net.minecraft.network.chat.contents.PlainTextContents plain) {
        //?}
            String literal = plain.text();
            if (!literal.isEmpty()) {
                BopomofoConverter.BopomofoResult result = BopomofoConverter.convert(literal);
                if (result.changed) {
                    changed = true;
                    newText = Component.empty(); // Discard the original single text body
                    for (BopomofoConverter.Segment seg : result.segments) {
                        MutableComponent segText = Component.literal(seg.original);
                        if (seg.translated != null) {
                            flandretw.bopomofo.translator.config.BopomofoConfig config = flandretw.bopomofo.translator.config.BopomofoConfig
                                    .getInstance();
                            MutableComponent translatedText = Component.literal(seg.translated).withStyle(config.textColor);
                            if (config.bold)
                                translatedText.withStyle(net.minecraft.ChatFormatting.BOLD);
                            if (config.italic)
                                translatedText.withStyle(net.minecraft.ChatFormatting.ITALIC);
                            if (config.underline)
                                translatedText.withStyle(net.minecraft.ChatFormatting.UNDERLINE);

                            //? if <1.20.3 {
                            /*segText.setStyle(newStyle.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, translatedText)));
                            *///?} else {
                            segText.setStyle(newStyle.withHoverEvent(new HoverEvent.ShowText(translatedText)));
                            //?}
                        } else {
                            segText.setStyle(newStyle);
                        }
                        newText.append(segText);
                    }
                    newStyle = Style.EMPTY;
                }
            }
        } else if (content instanceof net.minecraft.network.chat.contents.TranslatableContents translatable) {
            Object[] args = translatable.getArgs();
            Object[] newArgs = new Object[args.length];
            for (int i = 0; i < args.length; i++) {
                Object arg = args[i];
                if (arg instanceof Component argText) {
                    Component newArg = processText(argText);
                    if (newArg != argText)
                        changed = true;
                    newArgs[i] = newArg;
                } else if (arg instanceof String argStr) {
                    Component textArg = Component.literal(argStr);
                    Component newArg = processText(textArg);
                    if (newArg != textArg)
                        changed = true;
                    newArgs[i] = (newArg != textArg) ? newArg : argStr;
                } else {
                    newArgs[i] = arg;
                }
            }
            if (changed) {
                if (translatable.getFallback() != null) {
                    newText = Component.translatableWithFallback(translatable.getKey(), translatable.getFallback(), newArgs);
                } else {
                    newText = Component.translatable(translatable.getKey(), newArgs);
                }
            }
        }

        newText.setStyle(newStyle);

        for (Component sibling : text.getSiblings()) {
            Component processedSibling = processText(sibling);
            if (processedSibling != sibling) {
                changed = true;
            }
            newText.append(processedSibling);
        }

        return changed ? newText : text;
    }
}
