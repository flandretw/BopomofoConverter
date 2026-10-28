package flandretw.bopomofo.converter.mixin;

import flandretw.bopomofo.converter.BopomofoConverter;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.ArrayList;
import java.util.List;

@Mixin(ChatComponent.class)
public class ChatComponentMixin {

    //? if >=26.1 {
    /*@ModifyVariable(method = "addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;Lnet/minecraft/client/multiplayer/chat/GuiMessageSource;Lnet/minecraft/client/multiplayer/chat/GuiMessageTag;)V", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private Component modifyChatMessage(Component originalMessage) {
        if (originalMessage == null)
            return null;
        Component modified = processText(originalMessage);
        return modified != null ? modified : originalMessage;
    }
    *///?} else {
    @ModifyVariable(method = "addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;Lnet/minecraft/client/GuiMessageTag;)V", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private Component modifyChatMessage(Component originalMessage) {
        if (originalMessage == null)
            return null;
        Component modified = processText(originalMessage);
        return modified != null ? modified : originalMessage;
    }
    //?}

    private Component processText(Component text) {
        MutableComponent modifiedContent = null;
        Style contentStyle = text.getStyle();

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
                    modifiedContent = buildTranslatedComponent(result, contentStyle);
                }
            }
        } else if (content instanceof net.minecraft.network.chat.contents.TranslatableContents translatable) {
            Object[] args = translatable.getArgs();
            Object[] newArgs = null;
            for (int i = 0; i < args.length; i++) {
                Object arg = args[i];
                Object newArg = arg;
                if (arg instanceof Component argText) {
                    Component processedArg = processText(argText);
                    if (processedArg != argText) {
                        newArg = processedArg;
                    }
                } else if (arg instanceof String argStr) {
                    if (!argStr.isEmpty()) {
                        BopomofoConverter.BopomofoResult result = BopomofoConverter.convert(argStr);
                        if (result.changed) {
                            newArg = buildTranslatedComponent(result, Style.EMPTY);
                        }
                    }
                }
                if (newArg != arg) {
                    if (newArgs == null) {
                        newArgs = new Object[args.length];
                        System.arraycopy(args, 0, newArgs, 0, i);
                    }
                    newArgs[i] = newArg;
                } else if (newArgs != null) {
                    newArgs[i] = arg;
                }
            }
            if (newArgs != null) {
                if (translatable.getFallback() != null) {
                    modifiedContent = Component.translatableWithFallback(translatable.getKey(), translatable.getFallback(), newArgs);
                } else {
                    modifiedContent = Component.translatable(translatable.getKey(), newArgs);
                }
                modifiedContent.setStyle(contentStyle);
            }
        }

        List<Component> siblings = text.getSiblings();
        List<Component> newSiblings = null;
        for (int i = 0; i < siblings.size(); i++) {
            Component sibling = siblings.get(i);
            Component processedSibling = processText(sibling);
            if (processedSibling != sibling) {
                if (newSiblings == null) {
                    newSiblings = new ArrayList<>(siblings.size());
                    for (int j = 0; j < i; j++) {
                        newSiblings.add(siblings.get(j));
                    }
                }
                newSiblings.add(processedSibling);
            } else if (newSiblings != null) {
                newSiblings.add(sibling);
            }
        }

        if (modifiedContent == null && newSiblings == null) {
            return text;
        }

        MutableComponent resultText = modifiedContent != null ? modifiedContent : text.plainCopy();
        List<Component> siblingsToAppend = newSiblings != null ? newSiblings : siblings;
        for (Component sibling : siblingsToAppend) {
            resultText.append(sibling);
        }

        return resultText;
    }

    private MutableComponent buildTranslatedComponent(BopomofoConverter.BopomofoResult result, Style contentStyle) {
        MutableComponent modifiedContent = Component.empty();
        flandretw.bopomofo.converter.config.BopomofoConfig config = flandretw.bopomofo.converter.config.BopomofoConfig.getInstance();
        for (BopomofoConverter.Segment seg : result.segments) {
            MutableComponent segText = Component.literal(seg.original);
            if (seg.translated != null) {
                MutableComponent translatedText = Component.literal(seg.translated).withStyle(config.textColor);
                if (config.bold)
                    translatedText.withStyle(net.minecraft.ChatFormatting.BOLD);
                if (config.italic)
                    translatedText.withStyle(net.minecraft.ChatFormatting.ITALIC);
                if (config.underline)
                    translatedText.withStyle(net.minecraft.ChatFormatting.UNDERLINE);

                //? if <1.20.3 {
                /*segText.setStyle(contentStyle.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, translatedText)));
                *///?} else {
                segText.setStyle(contentStyle.withHoverEvent(new HoverEvent.ShowText(translatedText)));
                //?}
            } else {
                segText.setStyle(contentStyle);
            }
            modifiedContent.append(segText);
        }
        return modifiedContent;
    }
}
