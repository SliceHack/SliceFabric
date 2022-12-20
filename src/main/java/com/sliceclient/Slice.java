package com.sliceclient;

import com.sliceclient.api.IRC;
import com.sliceclient.cef.Page;
import com.sliceclient.cef.RequestHandler;
import com.sliceclient.cef.ViewNoGui;
import com.sliceclient.clickgui.HTMLGui;
import com.sliceclient.event.data.EventInfo;
import com.sliceclient.event.events.EventChat;
import com.sliceclient.event.events.EventKey;
import com.sliceclient.event.events.EventUpdate;
import com.sliceclient.file.Saver;
import com.sliceclient.manager.command.CommandManager;
import com.sliceclient.manager.event.EventManager;
import com.sliceclient.manager.module.ModuleManager;
import com.sliceclient.manager.notification.NotificationManager;
import com.sliceclient.manager.setting.SettingsManager;
import com.sliceclient.module.Module;
import com.sliceclient.util.MoveUtil;
import com.sliceclient.util.ResourceUtil;
import com.sliceclient.util.RotationUtil;
import lombok.Getter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.LivingEntity;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * The Slice class
 *
 * @author Nick
 * */
@Getter
@SuppressWarnings("all")
public enum Slice {
    INSTANCE;

    public final static String NAME = "Slice", VERSION = "1.0";

    /** Minecraft instance */
    public final MinecraftClient mc = MinecraftClient.getInstance();

    /** managers */
    private final EventManager eventManager;
    private final ModuleManager moduleManager;
    private final SettingsManager settingsManager;
    private final CommandManager commandManager;

    private final HTMLGui clickGui;
    private Saver saver;

    private NotificationManager notificationManager;

    /** discord */
    public String discordName = "1.19 User", discordID, discordDiscriminator;
    public IRC irc;

    private final List<ViewNoGui> html = new ArrayList<>();

    /*
    * Other things
    * */
    public int ping = 0, players = 0, seconds = 0, minutes = 0, hours = 0;
    public final long startTime;
    public long totalTime;
    public String playTime, totalPlayTime;

    private final String date;

    public LivingEntity target;

    /** alt manager */
    public String currentEmail, currentPassword;

    Slice() {
        eventManager = new EventManager();
        moduleManager = new ModuleManager();
        settingsManager = new SettingsManager(moduleManager);
        commandManager = new CommandManager(moduleManager);
        clickGui = new HTMLGui();
        irc = new IRC();

        date = (new SimpleDateFormat("dd/MM/yyyy")).format(new Date());
        totalTime = System.currentTimeMillis();
        startTime = System.currentTimeMillis();
        eventManager.register(this);

        Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
    }

    public void init() {
        notificationManager = new NotificationManager();
        saver = new Saver(moduleManager);
        if(irc != null) {

        }
        saver.load();

        html.add(new ViewNoGui(new Page("https://assets.sliceclient.com/hud" + "?name=" + NAME + "&version=" + VERSION + "&discord=" + discordName)));
    }

    public void stop() {
        if(irc != null) {
            irc.getSocket().disconnect();
        }
        saver.save();
    }

    @SuppressWarnings("all")
    public void extractHTML(File computerPath, String path) {
        File sliceDir = new File(MinecraftClient.getInstance().runDirectory, "Slice"),
                sliceHTML = new File(sliceDir, "html"),
                sliceHUD = new File(sliceHTML, "hud");

        if (!sliceHTML.exists()) sliceHTML.mkdirs();

        File html = new File(computerPath, "index.html"), css = new File(computerPath, "styles.css");

        if (!html.exists() || !css.exists()) {
            ResourceUtil.extractResource(path + "/index.html", html.toPath());
            ResourceUtil.extractResource(path + "/styles.css", css.toPath());

            if (sliceHUD.getParentFile().exists()) sliceHUD.mkdirs();
        }
    }

    @EventInfo
    public void onUpdate(EventUpdate e) {
        RequestHandler.setBPS(MoveUtil.getBPS());

        double milliseconds = System.currentTimeMillis() - Slice.INSTANCE.startTime, milliseconds1 = System.currentTimeMillis() - Slice.INSTANCE.startTime;
        int seconds = (int) (milliseconds / 1000), seconds1 = (int) (milliseconds1 / 1000);
        int minutes = seconds / 60, minutes1 = seconds1 / 60;
        int hours = minutes / 60, hours1 = minutes1 / 60;
        int days = hours / 24, days1 = hours1 / 24;

        seconds = seconds % 60;
        minutes = minutes % 60;
        hours = hours % 24;
        seconds1 = seconds % 60;
        minutes1 = minutes % 60;
        hours1 = hours % 24;

        Slice.INSTANCE.playTime = days + "d " + hours + "h " + minutes + "m " + seconds + "s";
        Slice.INSTANCE.totalPlayTime = days1 + "d " + hours1 + "h " + minutes1 + "m " + seconds1 + "s";

        for (Module module : moduleManager.getModules()) {

            if (module.isEnabled()) {
                RequestHandler.addToArrayList(module.getMode() != null ? module.getName() + " " + module.getMode().getValue() : module.getName());
            }
        }

        if(Objects.requireNonNull(MinecraftClient.getInstance().getNetworkHandler()).getPlayerList() == null) return;

        players = MinecraftClient.getInstance().getNetworkHandler().getPlayerList().size();

        for(PlayerListEntry npi : MinecraftClient.getInstance().getNetworkHandler().getPlayerList()) {
            if(npi.getProfile().getName().equalsIgnoreCase(MinecraftClient.getInstance().getSession().getUsername())) {
                Slice.INSTANCE.ping = npi.getLatency();
            }
        }

        RotationUtil.prevHeadPitch = RotationUtil.headPitch;
        RotationUtil.headPitch = e.getPitch();
    }

    @EventInfo
    public void onChat(EventChat e) {
        if(irc != null && e.getMessage().startsWith("#")) {
            irc.sendMessage(e.getMessage().substring(1).replaceFirst(" ", ""));
            e.setCancelled(true);
        }

        commandManager.handleChat(e);
    }

    @EventInfo
    public void onKey(EventKey e) {
        if(e.getKey() == GLFW.GLFW_KEY_RIGHT_SHIFT) {
            mc.setScreen(clickGui);
        }

        moduleManager.getModules().stream().filter(module -> module.getKey() == e.getKey()).forEach(Module::toggle);
    }

}
