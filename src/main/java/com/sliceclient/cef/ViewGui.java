package com.sliceclient.cef;

import com.mojang.blaze3d.platform.GlStateManager;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import org.cef.browser.CefMessageRouter;
import org.lwjgl.glfw.GLFW;

import net.montoyo.mcef.api.API;
import net.montoyo.mcef.api.IBrowser;
import net.montoyo.mcef.api.MCEFApi;

@Getter @Setter
@SuppressWarnings("all")
public class ViewGui extends Screen {

    private IBrowser browser = null;
    private CefMessageRouter messageRouter = null;

    private final String url;

    public ViewGui(Page page) {
        super(null);
        this.url = page.getUrl();
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        if(browser != null) browser.resize(client.getWindow().getWidth(), client.getWindow().getHeight());

        super.resize(client, width, height);
    }

    @Override
    public void init() {
        try {
            super.init();
            if (browser != null) return;

            API api = MCEFApi.getAPI();

            if (api == null) return;

            browser = api.createBrowser(url, true);

            if(client == null) return;

            browser.resize(client.getWindow().getWidth(), client.getWindow().getHeight());
        } catch (Exception ignored){} // prevent crashes
    }

    public void executeJavaScript(String script, String frame) {
        if(browser != null) {
            browser.runJS(script, frame);
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);

        if(browser != null) {
            GlStateManager._enableBlend();
            GlStateManager._blendFunc(770, 771);
            GlStateManager._disableDepthTest();
            GlStateManager._disableTexture();
            browser.draw(0, client.getWindow().getScaledHeight(), client.getWindow().getScaledWidth(), 0);
            GlStateManager._disableBlend();
            GlStateManager._enableDepthTest();
        }
    }

    @Override
    public void close() {
        if(browser != null) browser.close();

        super.close();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        try {
            return this.keyChanged(keyCode, scanCode, modifiers, true) || super.keyPressed(keyCode, scanCode, modifiers);
        } catch (Exception ignored){} // prevent crashes
        return false;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        try {
            return this.keyChanged(keyCode, scanCode, modifiers, false) || super.keyReleased(keyCode, scanCode, modifiers);
        } catch (Exception ignored){} // prevent crashes
        return false;
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if(browser != null) { browser.injectKeyTyped(codePoint, modifiers); return true;
        } else { return super.charTyped(codePoint, modifiers); }
    }

    public boolean keyChanged(int keyCode, int scanCode, int modifiers, boolean pressed) {
        if(client == null) return false;

        if(keyCode == GLFW.GLFW_KEY_ESCAPE) {
            client.setScreen(null);
            return true;
        }
        if(browser == null) return false;

        String keyStr = GLFW.glfwGetKeyName(keyCode, scanCode);

        if(keyStr == null) return false;

        char key = keyStr.charAt(keyStr.length() - 1);


        if(pressed) browser.injectKeyPressedByKeyCode(keyCode, key, 0);
        else browser.injectKeyReleasedByKeyCode(keyCode, key, 0);

        if(key == '\n') browser.injectKeyTyped(key, 0);
        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return this.mouseChanged(mouseX, mouseY, button, 0,0,0,true) || super.mouseClicked(mouseX,mouseY,button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return this.mouseChanged(mouseX, mouseY, button, 0,0,0,false) || super.mouseReleased(mouseX,mouseY,button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return this.mouseChanged(mouseX, mouseY, button, deltaX,deltaY,0,true) || super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        return this.mouseChanged(mouseX, mouseY, -1, 0,0,0,false) || super.mouseScrolled(mouseX, mouseY, amount);
    }

    public boolean mouseChanged(double mouseX, double mouseY,  int btn, double deltaX, double deltaY, double scrollAmount, boolean pressed) {
        int sx = (int) mouseX;
        int sy = (int) mouseY;
        int wheel = (int) scrollAmount;

        if(browser != null) {
            if(wheel != 0) browser.injectMouseWheel(sx, sy, 0,  wheel, 0);
            else if(btn == -1) browser.injectMouseMove(sx, sy, 0, sy < 0);
            else browser.injectMouseButton(sx, sy, 0, btn, pressed, 1);
        }

        return !(mouseY <= 20);
    }

    public int scaleY(int y) {
        return y;
    }

    public int scaleX(int x) {
        return x;
    }

}
