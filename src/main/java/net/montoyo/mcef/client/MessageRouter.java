package net.montoyo.mcef.client;

import com.sliceclient.Slice;
import com.sliceclient.cef.RequestHandler;
import com.sliceclient.module.Module;
import com.sliceclient.setting.Setting;
import com.sliceclient.setting.settings.BooleanValue;
import com.sliceclient.setting.settings.ModeValue;
import com.sliceclient.setting.settings.NumberValue;
import com.sliceclient.util.account.LoginUtil;
import com.sliceclient.util.account.microsoft.MicrosoftAccount;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.util.Session;
import net.montoyo.mcef.api.IJSQueryHandler;

import org.cef.browser.CefBrowser;
import org.cef.browser.CefBrowserOsr;
import org.cef.browser.CefFrame;
import org.cef.callback.CefQueryCallback;
import org.cef.handler.CefMessageRouterHandlerAdapter;

public class MessageRouter extends CefMessageRouterHandlerAdapter {
    
    private IJSQueryHandler handler;
    
    public MessageRouter(IJSQueryHandler h) {
        handler = h;
    }
    
    @Override
    public boolean onQuery(CefBrowser browser, CefFrame frame, long query_id, String request, boolean persistent, CefQueryCallback callback) {
        Screen screen = MinecraftClient.getInstance().currentScreen;
        MinecraftClient mc = MinecraftClient.getInstance();
        GameOptions gameSettings = mc.options;

        switch (request) {
            case "READY" -> new RequestHandler(browser);
            case "SinglePlayerScreen" -> mc.setScreen(new SelectWorldScreen(screen));
            case "MultiPlayerScreen" -> mc.setScreen(new MultiplayerScreen(screen));
            case "OptionsScreen" -> mc.setScreen(new OptionsScreen(screen, gameSettings));
//            case "AltManagerScreen" -> mc.setScreen(htmlAlt[0] = new HTMLAlt());
//            case "VersionScreen" -> mc.setScreen(new GuiProtocolSelector(screen));
            case "Exit" -> System.exit(0);
            case "Init" -> Slice.INSTANCE.getClickGui().queryInit();
            case "CloseGui" -> mc.setScreen(null);
            case "AltManagerReady" -> {

                if(mc.player != null) {
//                    if (Slice.INSTANCE.currentEmail != null && Slice.INSTANCE.currentPassword != null) {
//                        htmlAlt[0].runJS(String.format("addAccount(\"%s\", \"%s\", \"%s\")", mc.player.getName(), Slice.INSTANCE.currentEmail, Slice.INSTANCE.currentPassword));
//                    } else {
//                        htmlAlt[0].runJS((String.format("addAccount(\"%s\", \"%s\", \"%s\")", mc.player.getName(), mc.player.getName(), mc.player.getName())));
//                    }
//                    htmlAlt[0].runJS(String.format("setCurrentAccount(\"%s\")", mc.player.getName()));
                }

            }
        }

        if(request.startsWith("Login ")) {
            String[] args = request.substring(6).split(":");
            String email = args[0];
            String password = args[1];
            MicrosoftAccount account = LoginUtil.loginMicrosoftNoSetSession(email, password);
            if (account != null) {
                browser.executeJavaScript(String.format("addAccount(\"%s\",\"%s\", \"%s\")", account.getProfile().getName(), email, password), browser.getURL(), 0);
            }
        }

        if(request.startsWith("RealLogin ")) {
            String[] args = request.substring(10).split(":");
            String email = args[0];
            String password = args[1];
            if (!email.contains("@")) {
                Session account = LoginUtil.loginOffline(email);
                browser.executeJavaScript(String.format("setCurrentAccount(\"%s\")", account.getProfile().getName()), browser.getURL(), 0);
            } else {
                MicrosoftAccount account = LoginUtil.loginMicrosoft(email, password);
                if (account != null) {
                    browser.executeJavaScript("setCurrentAccount(\"" + account.getProfile().getName() + "\")", browser.getURL(), 0);
                }
            }
        }

        String[] r = request.split(" ");
        if(Slice.INSTANCE.getModuleManager().getModule(r[0]) != null) {
            Module module = Slice.INSTANCE.getModuleManager().getModule(r[0]);

            if (r.length >= 3) {
                Setting setting = module.getSetting(r[1]);

                if (setting != null) {
                    if (setting instanceof ModeValue) ((ModeValue) setting).setValue(r[2]);
                    if (setting instanceof BooleanValue) ((BooleanValue) setting).setValue(Boolean.parseBoolean(r[2]));
                    if (setting instanceof NumberValue) ((NumberValue) setting).setValue(Double.parseDouble(r[2]));
                }
            }
            if (r.length == 2) {
                module.toggle();
            }
        }
        return handler.handleQuery((CefBrowserOsr) browser, query_id, request, persistent, new QueryCallback(callback));
    }
    
    @Override
    public void onQueryCanceled(CefBrowser browser, CefFrame frame, long query_id) {
        handler.cancelQuery((CefBrowserOsr) browser, query_id);
    }

}
