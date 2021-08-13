package dev.ftb.mods.sluice.util;

import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;

import java.util.function.UnaryOperator;

public class TextUtil {

    public static final char INFO = 'ℹ';

    public static UnaryOperator<Style> COLOUR_HIGHLIGHT = (style) -> style.withColor(TextColor.fromRgb(0xfcb95b));
    public static UnaryOperator<Style> COLOUR_TRUE = (style) -> style.withColor(TextColor.fromRgb(0x4ecc8d));
    public static UnaryOperator<Style> COLOUR_FALSE = (style) -> style.withColor(TextColor.fromRgb(0xfd6d5d));
    public static UnaryOperator<Style> COLOUR_INFO = (style) -> style.withColor(TextColor.fromRgb(0x0fd1ec));

}
