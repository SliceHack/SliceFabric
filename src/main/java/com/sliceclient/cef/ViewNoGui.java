package com.sliceclient.cef;

import com.mojang.blaze3d.platform.GlStateManager;
import com.sliceclient.Slice;
import com.sliceclient.SliceMain;
import com.sliceclient.event.data.EventInfo;
import com.sliceclient.event.events.Event2D;
import com.sliceclient.event.events.EventResize;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.montoyo.mcef.api.*;
import net.montoyo.mcef.client.ClientProxy;
import net.montoyo.mcef.client.MessageRouter;
import org.cef.CefClient;
import org.cef.browser.CefMessageRouter;
import org.lwjgl.glfw.GLFW;

@Getter @Setter
@SuppressWarnings("all")
public class ViewNoGui implements IJSQueryHandler {

    private IBrowser browser = null;
    private CefClient cefClient = null;

    private final String url;

    private MinecraftClient client;

    public ViewNoGui(Page page) {
        this.url = page.getUrl();
        this.client = MinecraftClient.getInstance();

        if(Slice.INSTANCE == null) {
            browser.close();
            try {
                finalize();
            } catch (Throwable e) {
                e.printStackTrace();
            }
            return;
        }
        Slice.INSTANCE.getEventManager().register(this);
        init();
    }

    public void init() {
        try {
            if (browser != null) return;

            API api = MCEFApi.getAPI();

            if (api == null) return;

            ((ClientProxy)(SliceMain.MCEF.PROXY)).cefClient.addMessageRouter(CefMessageRouter.create(new MessageRouter(this)));
            browser = api.createBrowser(url, true);

            if(client == null) return;

            browser.resize(client.getWindow().getWidth(), client.getWindow().getHeight());
        } catch (Exception ignored){}
    }

    @EventInfo
    public void on2D(Event2D e) {
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

    @EventInfo
    public void onResize(EventResize e) {
        browser.resize(e.getWidth(), e.getHeight());
    }

    @Override
    public boolean handleQuery(IBrowser b, long queryId, String query, boolean persistent, IJSQueryCallback cb) {
        return false;
    }

    @Override
    public void cancelQuery(IBrowser b, long queryId) {}
}
