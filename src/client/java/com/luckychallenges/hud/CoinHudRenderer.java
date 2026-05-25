package com.luckychallenges.hud;

import com.luckychallenges.client.ClientData;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.Text;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Identifier;

/**
 * Рендерит счётчик Лаки-баксов в левом верхнем углу.
 * Макет:
 *   [иконка монетки]  Лаки-баксы
 *                     262
 */
public class CoinHudRenderer {

    private static final Identifier COIN_ICON =
        Identifier.of("luckychallenges", "textures/gui/coin.png");

    private static final int ICON_SIZE  = 24;
    private static final int PAD        = 8;
    private static final int GAP        = 6;

    public static void render(DrawContext ctx, RenderTickCounter counter) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.options.hudHidden || mc.currentScreen != null) return;

        TextRenderer font = mc.textRenderer;

        int x = PAD;

        // Медленное плавное движение по эллипсу
        double period = 3000.0;
        double t = System.currentTimeMillis() / period * Math.PI * 2;
        float coinBobX = (float)Math.sin(t) * 2f;
        float coinBobY = (float)Math.cos(t) * 3f;

        // Иконка монетки с плавным субпиксельным покачиванием
        float coinBaseY = PAD + (ICON_SIZE - 16) / 2f;
        ctx.getMatrices().pushMatrix();
        ctx.getMatrices().translate(x + coinBobX, coinBaseY + coinBobY);
        ctx.drawTexture(RenderPipelines.GUI_TEXTURED, COIN_ICON,
            0, 0,
            0f, 0f, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);
        ctx.getMatrices().popMatrix();

        int textX = x + ICON_SIZE + GAP;

        // "Лаки-баксы" — белый, обычный размер
        ctx.drawText(font, Text.translatable("hud.luckychallenges.coins").getString(), textX, PAD + 2, 0xFFFFFFFF, true);

        // Число — золотое, scale 2x
        String numStr = String.valueOf(ClientData.coins);
        ctx.getMatrices().pushMatrix();
        ctx.getMatrices().translate(textX, PAD + font.fontHeight + 4);
        ctx.getMatrices().scale(2f, 2f);
        ctx.drawText(font, numStr, 0, 0, 0xFFFFD700, true);
        ctx.getMatrices().popMatrix();
    }
}


