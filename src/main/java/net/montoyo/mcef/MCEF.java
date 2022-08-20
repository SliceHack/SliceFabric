package net.montoyo.mcef;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.montoyo.mcef.client.ClientProxy;
import net.montoyo.mcef.easy_forge_compat.Configuration;
import net.montoyo.mcef.utilities.Log;
import net.fabricmc.api.ModInitializer;

public class MCEF {

    public static String VERSION = "1.33";
    public static boolean ENABLE_EXAMPLE;
    public static boolean SKIP_UPDATES;
    public static boolean WARN_UPDATES;
    public static boolean USE_FORGE_SPLASH;
    public static String FORCE_MIRROR = null;
    public static String HOME_PAGE;
    public static String[] CEF_ARGS = new String[0];
    public static boolean CHECK_VRAM_LEAK;
    public static boolean SHUTDOWN_JCEF;
    public static boolean SECURE_MIRRORS_ONLY;

    public static boolean FORCE_LEGACY_VERSION;
    public static boolean BYPASS_LEGACY_WARNING;

    public static MCEF INSTANCE;

    public static BaseProxy PROXY;

    public void onInitialize() {
        System.out.println("MCEF Initalizing...");
        Log.info("Loading MCEF config...");
        Configuration cfg = new Configuration();

        INSTANCE = this;

        //Config: main
        SKIP_UPDATES = cfg.getBoolean("skipUpdates", "main", false, "Do not update binaries.");
        WARN_UPDATES = cfg.getBoolean("warnUpdates", "main", true, "Tells in the chat if a new version of MCEF is available (broken)."); // TODO: Find equavilent of playerjoinevent for fabric
        USE_FORGE_SPLASH = cfg.getBoolean("useForgeSplash", "main", false, "(No effect) Use Forge's splash screen to display resource download progress (may be unstable).");
        CEF_ARGS = cfg.getString("cefArgs", "main", "", "Command line arguments passed to CEF. For advanced users.").split("\\s+");
        SHUTDOWN_JCEF = cfg.getBoolean("shutdownJcef", "main", false, "Set this to true if your Java process hangs after closing Minecraft. This is disabled by default because it makes the launcher think Minecraft crashed...");
        SECURE_MIRRORS_ONLY = cfg.getBoolean("secureMirrorsOnly", "main", true, "Only enable secure (HTTPS) mirror. This should be kept to true unless you know what you're doing.");

        String mirror = cfg.getString("forcedMirror", "main", "", "A URL that contains every MCEF resources; for instance https://montoyo.net/jcef.").trim();
        if (mirror.length() > 0)
            FORCE_MIRROR = mirror;

        //Config: exampleBrowser
        ENABLE_EXAMPLE = cfg.getBoolean("enable", "exampleBrowser", true, "Set this to false if you don't want to enable the F10 browser.");
        HOME_PAGE = cfg.getString("home", "exampleBrowser", "https://google.com", "The home page of the F10 browser.");

        //Config: debug
        CHECK_VRAM_LEAK = cfg.getBoolean("checkForVRAMLeak", "debug", false, "Track allocated OpenGL textures to make sure there's no leak");

        //Config: smashmaster's legacy mode
        FORCE_LEGACY_VERSION = cfg.getBoolean("useLegacyVersionWithCodecs", "legacy", false, "Set this to true if you want to use the 1.30 version of JCEF. This however provides Codec patches. ");
        BYPASS_LEGACY_WARNING = cfg.getBoolean("skipLegacyWarning", "legacy", false, "Set this to true if you want to bypass the warning message about security against exploits.");

        if(FORCE_LEGACY_VERSION) {
            VERSION = "1.30";
        }

        cfg.save();

        setupProxy();

        PROXY.onPreInit();
        this.onInit(); // old init
    }

    @Environment(EnvType.CLIENT)
    public void setupProxy(){
        PROXY = new ClientProxy();
    }

    public void onInit() {
        PROXY.onInit();
    }

    //Called by mixin
    public static void onMinecraftShutdown() {
        PROXY.onShutdown();
    }

}
