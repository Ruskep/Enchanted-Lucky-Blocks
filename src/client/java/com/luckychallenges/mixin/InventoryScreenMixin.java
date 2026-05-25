package com.luckychallenges.mixin;

import com.luckychallenges.screen.UpgradeScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(InventoryScreen.class)
public class InventoryScreenMixin {

    private static final Identifier COIN_ICON =
        Identifier.of("luckychallenges", "textures/gui/coin.png");
    private static final int BTN_SIZE = 20;
    private static final int BG_WIDTH = 176;

    @Unique
    private int lc_btnX() {
        int w = MinecraftClient.getInstance().getWindow().getScaledWidth();
        int x = (w - BG_WIDTH) / 2;
        return x + BG_WIDTH - 2;
    }

    @Unique
    private int lc_btnY() {
        int h = MinecraftClient.getInstance().getWindow().getScaledHeight();
        int y = (h - 166) / 2;
        return y - BTN_SIZE - 2;
    }

    @Inject(method = "render(Lnet/minecraft/client/gui/DrawContext;IIF)V", at = @At("TAIL"))
    private void lc_renderUpgradeButton(DrawContext ctx, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        int bx = lc_btnX();
        int by = lc_btnY();
        boolean hover = mouseX >= bx && mouseX <= bx + BTN_SIZE && mouseY >= by && mouseY <= by + BTN_SIZE;

        int bg = hover ? 0xCC448844 : 0xCC224422;
        int border = hover ? 0xFF88FF88 : 0xFF55FF55;

        // glow
        if (hover) {
            ctx.fill(bx - 2, by - 2, bx + BTN_SIZE + 2, by + BTN_SIZE + 2, 0x2255FF55);
        }

        ctx.fill(bx, by, bx + BTN_SIZE, by + BTN_SIZE, bg);
        ctx.fill(bx, by, bx + BTN_SIZE, by + 1, border);
        ctx.fill(bx, by + BTN_SIZE - 1, bx + BTN_SIZE, by + BTN_SIZE, border);
        ctx.fill(bx, by, bx + 1, by + BTN_SIZE, border);
        ctx.fill(bx + BTN_SIZE - 1, by, bx + BTN_SIZE, by + BTN_SIZE, border);

        // inner glow on hover
        if (hover) {
            ctx.fill(bx + 2, by + 2, bx + BTN_SIZE - 2, by + BTN_SIZE - 2, 0x1155FF55);
        }

        ctx.drawTexture(RenderPipelines.GUI_TEXTURED, COIN_ICON,
            bx + 2, by + 2, 0f, 0f, 16, 16, 16, 16);
    }

    @Inject(method = "mouseReleased(Lnet/minecraft/client/gui/Click;)Z", at = @At("HEAD"), cancellable = true)
    private void lc_onMouseClicked(Click click, CallbackInfoReturnable<Boolean> cir) {
        int bx = lc_btnX();
        int by = lc_btnY();
        if (click.x() >= bx && click.x() <= bx + BTN_SIZE && click.y() >= by && click.y() <= by + BTN_SIZE) {
            MinecraftClient.getInstance().setScreen(new UpgradeScreen());
            cir.setReturnValue(true);
            cir.cancel();
        }
    }
}


