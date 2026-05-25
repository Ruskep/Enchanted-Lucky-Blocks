package com.luckychallenges.hud;

import com.luckychallenges.network.ChallengeTimerPacket;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;

/**
 * Рендерит таймер испытания в правом верхнем углу экрана.
 * Формат: 04.92 (секунды.сотые)
 * Цвет: зелёный → жёлтый (≤5с) → красный (≤3с)
 */
public class ChallengeTimerRenderer {

    private static int ticksLeft  = -1;
    private static int totalTicks = 0;
    private static String description = "";

    public static void receive(ChallengeTimerPacket packet) {
        ticksLeft    = packet.ticksLeft();
        totalTicks   = packet.totalTicks();
        description  = packet.description();
    }

    public static void render(DrawContext ctx, RenderTickCounter counter) {
        if (ticksLeft < 0) return;

        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.options.hudHidden) return;

        float smoothTicks = ticksLeft - counter.getTickProgress(false);
        if (smoothTicks < 0) smoothTicks = 0;

        float seconds = smoothTicks / 20f;
        int sec = (int) seconds;
        int centis = (int)((seconds - sec) * 100);

        String text = String.format("%02d.%02d", sec, centis);

        TextRenderer font = mc.textRenderer;
        float scale = 2.0f;
        int textW = (int)(font.getWidth(text) * scale);

        int screenW = ctx.getScaledWindowWidth();
        int x = screenW - textW - 8;

        int color;
        if (seconds > 5f) {
            color = 0xFF55FF55;
        } else if (seconds > 3f) {
            color = 0xFFFFFF55;
        } else {
            float blink = (float)(Math.sin(smoothTicks * 0.4f) * 0.5f + 0.5f);
            int bright = (int)(155 + blink * 100);
            color = (0xFF << 24) | (bright << 16) | 0x4444;
        }

        // Описание испытания над таймером (переводится под язык игры)
        if (!description.isEmpty()) {
            Text descText = Text.translatable(description);
            ctx.drawTextWithShadow(font, descText.asOrderedText(), screenW - font.getWidth(descText.asOrderedText()) - 8, 2, 0xFFFFFFAA);
        }

        int timerY = 14;
        int bgPad = 3;
        int bgX = x - bgPad;
        int bgY = timerY - bgPad;
        int bgW = textW + bgPad * 2;
        int bgH = (int)(font.fontHeight * scale) + bgPad * 2;
        ctx.fill(bgX, bgY, bgX + bgW, bgY + bgH, 0x88000000);

        ctx.getMatrices().pushMatrix();
        ctx.getMatrices().translate(x, timerY);
        ctx.getMatrices().scale(scale, scale);
        ctx.drawText(font, text, 0, 0, color, true);
        ctx.getMatrices().popMatrix();
    }
}


