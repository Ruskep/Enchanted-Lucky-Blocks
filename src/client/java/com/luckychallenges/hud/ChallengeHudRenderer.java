package com.luckychallenges.hud;

import com.luckychallenges.challenge.ChallengeHud;
import com.luckychallenges.network.ChallengeHudPacket;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ChallengeHudRenderer {

    private static final Identifier HEART_SUCCESS = Identifier.of("luckychallenges", "textures/gui/hardcore_complete.png");
    private static final Identifier HEART_FAIL    = Identifier.of("luckychallenges", "textures/gui/hardcore.png");

    private static final int HEART_TEX_SIZE = 9;

    private static int    type         = -1;
    private static Text   mainText     = Text.empty();
    private static String subText      = "";
    private static long   startTime    = -1;

    private static int totalMs() {
        return switch (type) {
            case ChallengeHud.TYPE_RESET -> 1000;  // сброс — 1 сек
            case ChallengeHud.TYPE_SUCCESS, ChallengeHud.TYPE_FAIL -> 4000;
            default -> 5500; // анонс: 500 + 4000 + 1000
        };
    }

    private static int fadeInMs() {
        return switch (type) {
            case ChallengeHud.TYPE_RESET -> 100;
            default -> 500;
        };
    }

    private static int holdMs() {
        return switch (type) {
            case ChallengeHud.TYPE_RESET -> 800;
            case ChallengeHud.TYPE_SUCCESS, ChallengeHud.TYPE_FAIL -> 3000;
            default -> 4000;
        };
    }

    private static int fadeOutMs() {
        return switch (type) {
            case ChallengeHud.TYPE_RESET -> 100;
            default -> 1000;
        };
    }

    public static void receive(ChallengeHudPacket packet) {
        type         = packet.type();
        subText      = packet.message();
        startTime    = System.currentTimeMillis();
        mainText = switch (type) {
            case ChallengeHud.TYPE_SUCCESS -> Text.translatable("hud.luckychallenges.success");
            case ChallengeHud.TYPE_FAIL    -> Text.translatable("hud.luckychallenges.fail");
            case ChallengeHud.TYPE_RESET   -> Text.translatable("hud.luckychallenges.reset");
            default                        -> Text.translatable("hud.luckychallenges.challenge");
        };
    }

    public static void render(DrawContext ctx, RenderTickCounter counter) {
        if (startTime < 0) return;

        int totalMs = totalMs();
        int fadeInMs = fadeInMs();
        int holdMs = holdMs();
        int fadeOutMs = fadeOutMs();

        long elapsed = System.currentTimeMillis() - startTime;
        if (elapsed >= totalMs) {
            startTime = -1;
            return;
        }

        float t = elapsed;

        float alpha;
        if (t < fadeInMs) {
            alpha = t / fadeInMs;
        } else if (t < fadeInMs + holdMs) {
            alpha = 1.0f;
        } else {
            alpha = 1.0f - (t - fadeInMs - holdMs) / fadeOutMs;
        }
        alpha = Math.max(0f, Math.min(1f, alpha));

        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.options.hudHidden) return;

        int a = (int)(alpha * 255);
        if (a <= 0) return;

        TextRenderer font   = mc.textRenderer;
        int screenW = ctx.getScaledWindowWidth();
        int screenH = ctx.getScaledWindowHeight();

        Identifier heartTex;
        int mainColor, subColor;
        switch (type) {
            case ChallengeHud.TYPE_SUCCESS -> {
                heartTex  = HEART_SUCCESS;
                mainColor = color(a, 0x55, 0xFF, 0x55);
                subColor  = color(a, 0xAA, 0xFF, 0xAA);
            }
            case ChallengeHud.TYPE_FAIL -> {
                heartTex  = HEART_FAIL;
                mainColor = color(a, 0xFF, 0x44, 0x44);
                subColor  = color(a, 0xFF, 0xAA, 0xAA);
            }
            case ChallengeHud.TYPE_RESET -> {
                heartTex  = HEART_FAIL;
                mainColor = color(a, 0xFF, 0xAA, 0x00);
                subColor  = color(a, 0xCC, 0xCC, 0xCC);
            }
            default -> {
                heartTex  = HEART_FAIL;
                mainColor = color(a, 0xFF, 0xFF, 0x55);
                subColor  = color(a, 0xFF, 0xFF, 0xFF);
            }
        }

        float textScale  = 2.0f;
        float heartScale = 3.0f;
        int   heartSize  = (int)(HEART_TEX_SIZE * heartScale);
        int   gap        = 10;

        int textW = (int)(font.getWidth(mainText.asOrderedText()) * textScale);
        int totalW = heartSize + gap + textW + gap + heartSize;
        int startX = (screenW - totalW) / 2;
        int centerY = screenH / 2 - 24;

        float wobble     = (float) Math.sin(t * 0.12f * 0.05f);
        float leftAngle  =  wobble * 14f;
        float rightAngle = -wobble * 14f;

        int textH  = (int)(font.fontHeight * textScale);
        int heartY = centerY + (textH - heartSize) / 2;

        drawRotatedTexture(ctx, heartTex, startX, heartY, heartSize, leftAngle, a);

        int rightX = startX + heartSize + gap + textW + gap;
        drawRotatedTexture(ctx, heartTex, rightX, heartY, heartSize, rightAngle, a);

        ctx.getMatrices().pushMatrix();
        ctx.getMatrices().translate(startX + heartSize + gap, centerY);
        ctx.getMatrices().scale(textScale, textScale);
        ctx.drawText(font, mainText.asOrderedText(), 0, 0, mainColor, true);
        ctx.getMatrices().popMatrix();
    }

    private static void drawRotatedTexture(DrawContext ctx, Identifier texture,
                                            int x, int y, int size,
                                            float angleDeg, int alpha) {
        float scale = (float) size / HEART_TEX_SIZE;
        float cx = x + size / 2f;
        float cy = y + size / 2f;

        ctx.getMatrices().pushMatrix();
        ctx.getMatrices().translate(cx, cy);
        ctx.getMatrices().rotate((float) Math.toRadians(angleDeg));
        ctx.getMatrices().scale(scale, scale);
        ctx.getMatrices().translate(-HEART_TEX_SIZE / 2f, -HEART_TEX_SIZE / 2f);

        ctx.drawTexture(
            RenderPipelines.GUI_TEXTURED,
            texture,
            0, 0,
            0f, 0f,
            HEART_TEX_SIZE, HEART_TEX_SIZE,
            HEART_TEX_SIZE, HEART_TEX_SIZE
        );

        ctx.getMatrices().popMatrix();
    }

    private static int color(int a, int r, int g, int b) {
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}
