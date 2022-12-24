package com.sliceclient.cef;

import com.mojang.blaze3d.platform.GlStateManager;
import com.sliceclient.SliceMain;
import com.sliceclient.ui.HTMLMainMenu;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.montoyo.mcef.MCEF;
import net.montoyo.mcef.api.*;
import net.montoyo.mcef.client.ClientProxy;
import net.montoyo.mcef.client.MessageRouter;
import org.cef.browser.CefMessageRouter;
import org.lwjgl.glfw.GLFW;

@Getter @Setter
@SuppressWarnings("all")
public class ViewGui extends Screen implements IJSQueryHandler {

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

            ((ClientProxy)(MCEF.PROXY)).cefClient.addMessageRouter(CefMessageRouter.create(new MessageRouter(this)));
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
            GlStateManager._disableDepthTest();
            GlStateManager._enableTexture();
            GlStateManager._clearColor(1.0f, 1.0f, 1.0f, 1.0f);
            browser.draw(.0d, height, width, 0);
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
        assert client != null;

        if(keyCode == GLFW.GLFW_KEY_ESCAPE && (!(this instanceof HTMLMainMenu))) {
            client.setScreen(null);
            return true;
        }

        String keystr = GLFW.glfwGetKeyName(keyCode, scanCode);

        if(keystr == null) keystr = "\0";
        if(keyCode == GLFW.GLFW_KEY_ENTER) keystr = "\n";
        if(keystr.length() == 0) return false;

        char key = keystr.charAt(keystr.length() - 1);

        if(browser != null) {
            if(pressed) browser.injectKeyPressedByKeyCode(keyCode, key, 0);
            else browser.injectKeyReleasedByKeyCode(keyCode, key, 0);
            if(key == '\n') browser.injectKeyTyped(key, 0);
            return true;
        }

        return false;
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
        return this.mouseChanged(mouseX, mouseY, -1, 0,0, amount,false) || super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        this.mouseChanged(mouseX, mouseY, -1,  0, 0, 0, false);
    }

    public boolean mouseChanged(double mouseX, double mouseY, int btn, double deltaX, double deltaY, double scrollAmount, boolean pressed) {
        int sx = scaleX((int) mouseX);
        int sy = (int) mouseY;
        int wheel = (int) scrollAmount;

        if(browser != null) {
            int y = scaleY(sy);

            if(wheel != 0) {
                browser.injectMouseWheel(sx, y, 0,  wheel, 0);
                return true;
            }

            if(btn == -1) {
                browser.injectMouseMove(sx, y, 0, y < 0);
                return true;
            }

            browser.injectMouseButton(sx, y, 0, btn + 1, pressed, 1);
            return true;
        }

        return true;
    }

    public int scaleY(int y) {
        assert client != null;
        double sy = ((double) y) / ((double) height) * ((double) client.getWindow().getHeight());
        return (int) sy;
    }

    public int scaleX(int x) {
        assert client != null;
        double sx = ((double) x) / ((double) width) * ((double) client.getWindow().getWidth());
        return (int) sx;
    }

    @Override
    public boolean handleQuery(IBrowser b, long queryId, String query, boolean persistent, IJSQueryCallback cb) {
        return false;
    }

    @Override
    public void cancelQuery(IBrowser b, long queryId) {

    }
}
