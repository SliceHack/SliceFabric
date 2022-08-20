package com.sliceclient.cef;

import com.sliceclient.Slice;
import com.sliceclient.notification.Notification;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.LivingEntity;
import org.cef.browser.CefBrowser;

@Getter @Setter
public class RequestHandler {
    public static RequestHandler INSTANCE;

    public CefBrowser browser;

    private boolean TargetHudShown;
    private boolean SessionHudShown;


    public RequestHandler(CefBrowser browser) {
        INSTANCE = this;
        this.browser = browser;
        this.setupInfo();
        sendJavascript("let iframe;");
        this.setupTargetHUD();
        sendJavascript("let arraylist;");
        this.setupArrayList();

        TargetHudShown = true;
        RequestHandler.hideTargetHUD();
        this.setupSessionHUD();
        SessionHudShown = true;
        RequestHandler.hideSessionHUD();
    }

    public static void addToArrayList(String text) {
        if(INSTANCE == null) return;

        INSTANCE.sendJavascript("addToArrayList(\"" + text + "\");");
    }

    public static void removeFromArrayList(String text) {
        if(INSTANCE == null) return;

        INSTANCE.sendJavascript("removeFromArrayList(\"" + text + "\");");
    }

    public static void renameFromArrayList(String value, String newValue) {
        if(INSTANCE == null) return;

        INSTANCE.sendJavascript("renameFromArrayList(\"" + value + "\", \"" + newValue + "\");");
    }

    public void sendJavascript(String js) {
        browser.executeJavaScript(js, null, 0);
    }

    public void setupInfo() {
        sendJavascript("document.querySelector(\".text\").innerHTML = \"" + Slice.NAME + " | " + Slice.VERSION + " | " + Slice.INSTANCE.getDiscordName() + "\";");
        sendJavascript("document.querySelector(\".box\").style.width = (document.querySelector(\".text\").offsetWidth)+ 50 + \"px\";");
    }

    public void createIframe(String path) {
        sendJavascript("document.body.insertAdjacentHTML('beforeend', '<iframe src=\"" + path + "\" frameborder=\"0\"></iframe>');");
    }

    public void setupTargetHUD() {
        createIframe("TargetHUD/index.html");
    }

    public static void updateTargetHUD(LivingEntity target) {
        if (!INSTANCE.TargetHudShown) return;
        double health = target.getHealth();
        double max = target.getMaxHealth();
        String name = target.getName().getString();

        INSTANCE.sendJavascript("updateTargetHUD(" + health + ", " + max + ", \"" + name + "\");");
    }

    public static void hideTargetHUD() {
        if (!INSTANCE.TargetHudShown) return;
        INSTANCE.TargetHudShown = false;
        INSTANCE.sendJavascript("document.querySelector(\"iframe[src='TargetHUD/index.html']\").style.visibility = \"hidden\";");
    }

    public static void showTargetHUD() {
        if (INSTANCE.TargetHudShown) return;
        INSTANCE.TargetHudShown = true;
        INSTANCE.sendJavascript("document.querySelector(\"iframe[src='TargetHUD/index.html']\").style.visibility = \"visible\";");
    }

    public void setupSessionHUD() {
        createIframe("SessionHUD/index.html");
    }
    public void setupArrayList() {
        createIframe("ArrayList/index.html");
    }

    public static void hideSessionHUD() {
        if (!INSTANCE.SessionHudShown) return;
        INSTANCE.SessionHudShown = false;
        INSTANCE.sendJavascript("document.querySelector(\"iframe[src='SessionHUD/index.html']\").style.visibility = \"hidden\";");
    }

    public static void showSessionHUD() {
        if (INSTANCE.SessionHudShown) return;
        INSTANCE.SessionHudShown = true;
        INSTANCE.sendJavascript("document.querySelector(\"iframe[src='SessionHUD/index.html']\").style.visibility = \"visible\";");
    }

    public static void updateSessionHUD() {
        if (!INSTANCE.SessionHudShown) return;
        INSTANCE.sendJavascript("updateSessionHUD(\"" + Slice.INSTANCE.getDate() + "\", " + Slice.INSTANCE.getPlayers() + ", " + Slice.INSTANCE.getPing() + ", \"" + Slice.INSTANCE.getTotalPlayTime() + "\", \"" + Slice.INSTANCE.getPlayTime() + "\");");
    }

    public static void addNotification(Notification notification) {
        INSTANCE.sendJavascript("addNotification(\"" + notification.getTitle() + "\", \"" + notification.getMessage() + "\", " + notification.getSeconds() + ");");
    }

}
