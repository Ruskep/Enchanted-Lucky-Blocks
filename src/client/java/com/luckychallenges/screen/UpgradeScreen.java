package com.luckychallenges.screen;

import com.luckychallenges.client.ClientData;
import com.luckychallenges.network.PurchaseUpgradePacket;
import com.luckychallenges.upgrade.UpgradeType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;

public class UpgradeScreen extends Screen {

    private static final int BG_TOP     = 0xDD0A1A0A;
    private static final int BG_BOTTOM  = 0xDD0D2D0D;
    private static final int CARD_COLOR = 0xDD223322;
    private static final int CARD_W     = 230;
    private static final int CARD_H     = 64;
    private static final int COLS       = 2;
    private static final int GAP_H      = 20;
    private static final int GAP_V      = 6;

    private final List<ButtonWidget> buyButtons = new ArrayList<>();
    private final long openTime;
    private final float[] hoverProgress;

    public UpgradeScreen() {
        super(Text.translatable("screen.luckychallenges.title"));
        this.openTime = System.currentTimeMillis();
        this.hoverProgress = new float[UpgradeType.values().length];
    }

    @Override
    protected void init() {
        buyButtons.clear();
        UpgradeType[] types = UpgradeType.values();
        int totalW = COLS * CARD_W + GAP_H;
        int startX = (width - totalW) / 2;
        int startY = 36;

        for (int i = 0; i < types.length; i++) {
            UpgradeType type = types[i];
            int col = i % COLS;
            int row = i / COLS;
            int cx = startX + col * (CARD_W + GAP_H);
            int cy = startY + row * (CARD_H + GAP_V);

            ButtonWidget btn = makeButton(type, cx, cy);
            addDrawableChild(btn);
            buyButtons.add(btn);
        }

        addDrawableChild(ButtonWidget.builder(
            Text.translatable("screen.luckychallenges.close").formatted(Formatting.RED),
            b -> close()
        ).dimensions(width / 2 - 40, height - 28, 80, 20).build());
    }

    private ButtonWidget makeButton(UpgradeType type, int cx, int cy) {
        int level = ClientData.upgradeLevels.getOrDefault(type, 0);
        boolean maxed = level >= type.getMaxLevel();
        int price = maxed ? 0 : type.prices[level];
        boolean canAfford = ClientData.coins >= price;

        Text label;
        if (maxed) {
            label = Text.translatable("screen.luckychallenges.maxed").formatted(Formatting.GREEN, Formatting.BOLD);
        } else if (canAfford) {
            label = Text.translatable("screen.luckychallenges.buy", price).formatted(Formatting.GOLD, Formatting.BOLD);
        } else {
            label = Text.translatable("screen.luckychallenges.cannot_afford", price).formatted(Formatting.DARK_GRAY);
        }

        ButtonWidget btn = ButtonWidget.builder(
            label,
            b -> {
                if (!maxed && canAfford) onBuy(type);
            }
        )
        .dimensions(cx + CARD_W - 120, cy + CARD_H - 24, 112, 18)
        .build();
        btn.active = !maxed && canAfford;
        return btn;
    }

    private void onBuy(UpgradeType type) {
        ClientPlayNetworking.send(new PurchaseUpgradePacket(type.name()));
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        // vertical gradient
        int steps = 8;
        for (int i = 0; i < steps; i++) {
            int y1 = height * i / steps;
            int y2 = height * (i + 1) / steps;
            float t = (float) i / (steps - 1);
            int r = (int)((0x0A & 0xFF) * (1 - t) + (0x0D & 0xFF) * t);
            int g = (int)((0x1A & 0xFF) * (1 - t) + (0x2D & 0xFF) * t);
            int b = (int)((0x0A & 0xFF) * (1 - t) + (0x0D & 0xFF) * t);
            int color = (0xDD << 24) | (r << 16) | (g << 8) | b;
            ctx.fill(0, y1, width, y2, color);
        }

        ctx.drawCenteredTextWithShadow(textRenderer,
            Text.translatable("screen.luckychallenges.title").formatted(Formatting.GOLD, Formatting.BOLD),
            width / 2, 10, 0xFFFFFFFF);

        ctx.drawCenteredTextWithShadow(textRenderer,
            Text.translatable("screen.luckychallenges.coins").formatted(Formatting.WHITE)
                .append(Text.literal(String.valueOf(ClientData.coins)).formatted(Formatting.YELLOW, Formatting.BOLD)),
            width / 2, 24, 0xFFFFFFFF);

        float openProgress = Math.min(1, (System.currentTimeMillis() - openTime) / 500f);

        UpgradeType[] types = UpgradeType.values();
        int totalW = COLS * CARD_W + GAP_H;
        int startX = (width - totalW) / 2;
        int startY = 36;

        for (int i = 0; i < types.length; i++) {
            UpgradeType type = types[i];
            int col = i % COLS;
            int row = i / COLS;
            int cx = startX + col * (CARD_W + GAP_H);
            int cy = startY + row * (CARD_H + GAP_V);

            int level = ClientData.upgradeLevels.getOrDefault(type, 0);
            boolean maxed = level >= type.getMaxLevel();

            float cardDelay = 0.05f * i;
            float cardProgress = Math.min(1, Math.max(0, (openProgress - cardDelay) / (1 - cardDelay)));
            float cardEase = 1 - (1 - cardProgress) * (1 - cardProgress);

            boolean hovered = mouseX >= cx && mouseX <= cx + CARD_W && mouseY >= cy && mouseY <= cy + CARD_H;
            float target = hovered ? 1f : 0f;
            hoverProgress[i] += (target - hoverProgress[i]) * Math.min(1, delta * 12f);

            float hoverScale = 1f + hoverProgress[i] * 0.04f;
            float cardCenterX = cx + CARD_W / 2f;
            float cardCenterY = cy + CARD_H / 2f;

            int borderColor = maxed ? 0xFF55FF55
                : (hovered ? 0xFF66FF66 : 0xFF335533);

            ctx.getMatrices().pushMatrix();
            ctx.getMatrices().translate(cardCenterX, cardCenterY);
            ctx.getMatrices().scale(cardEase * hoverScale, cardEase * hoverScale);
            ctx.getMatrices().translate(-cardCenterX, -cardCenterY);

            ctx.fill(cx, cy, cx + CARD_W, cy + CARD_H, CARD_COLOR);
            ctx.fill(cx, cy, cx + CARD_W, cy + 1, borderColor);
            ctx.fill(cx, cy + CARD_H - 1, cx + CARD_W, cy + CARD_H, borderColor);
            ctx.fill(cx, cy, cx + 1, cy + CARD_H, borderColor);
            ctx.fill(cx + CARD_W - 1, cy, cx + CARD_W, cy + CARD_H, borderColor);

            ctx.drawTextWithShadow(textRenderer,
                Text.translatable(type.translationKey() + ".name").formatted(maxed ? Formatting.GREEN : Formatting.AQUA, Formatting.BOLD),
                cx + 6, cy + 5, 0xFFFFFFFF);

            ctx.drawTextWithShadow(textRenderer,
                Text.translatable(type.translationKey() + ".desc").formatted(Formatting.WHITE),
                cx + 6, cy + 18, 0xFFCCCCCC);

            Text lvlText = (type.isPerk()
                ? (maxed ? Text.translatable("screen.luckychallenges.bought") : Text.translatable("screen.luckychallenges.not_bought"))
                : Text.translatable("screen.luckychallenges.level", level, type.getMaxLevel()))
                .copy().formatted(maxed ? Formatting.GREEN : Formatting.YELLOW);
            ctx.drawTextWithShadow(textRenderer,
                lvlText,
                cx + 6, cy + 35, 0xFFFFFFFF);

            {
                int dotR = 4;
                int dots = type.getMaxLevel();
                int dotGap = 2;
                int dotStartX = cx + 8;
                int dotY = cy + 56;
                int maxDotX = dotStartX + (dots - 1) * (dotR * 2 + dotGap) + dotR * 2;
                // Подложка под все точки (только для не-перков)
                if (!type.isPerk()) {
                    ctx.fill(dotStartX - 2, dotY - dotR - 2, maxDotX + 2, dotY + dotR + 2, 0x44000000);
                }
                for (int d = 0; d < dots; d++) {
                    int dx = dotStartX + d * (dotR * 2 + dotGap) + dotR;
                    boolean filled = d < level || (type.isPerk() && maxed);
                    int color;
                    if (filled) {
                        float t = dots > 1 ? (float) d / (dots - 1) : 0;
                        int red = (int)(255 - t * 200);
                        int green = (int)(55 + t * 200);
                        color = (0xFF << 24) | (red << 16) | (green << 8) | 55;
                    } else {
                        color = 0xFF444444;
                    }
                    fillCircle(ctx, dx, dotY, dotR, color);
                }
            }

            ctx.getMatrices().popMatrix();
        }

        super.render(ctx, mouseX, mouseY, delta);
    }

    private static void fillCircle(DrawContext ctx, int cx, int cy, int r, int color) {
        for (int dx = -r; dx <= r; dx++) {
            for (int dy = -r; dy <= r; dy++) {
                if (dx * dx + dy * dy <= r * r) {
                    ctx.fill(cx + dx, cy + dy, cx + dx + 1, cy + dy + 1, color);
                }
            }
        }
    }

    @Override
    public boolean shouldPause() { return false; }

    public void refresh() {
        children().stream()
            .filter(e -> e instanceof ButtonWidget)
            .toList()
            .forEach(this::remove);
        buyButtons.clear();
        init();
    }
}
