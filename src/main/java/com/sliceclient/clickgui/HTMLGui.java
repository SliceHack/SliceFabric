package com.sliceclient.clickgui;

import com.sliceclient.Slice;
import com.sliceclient.cef.Page;
import com.sliceclient.cef.ViewGui;
import com.sliceclient.setting.Setting;
import com.sliceclient.setting.settings.BooleanValue;
import com.sliceclient.setting.settings.ModeValue;
import com.sliceclient.setting.settings.NumberValue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import com.sliceclient.module.Module;
import net.montoyo.mcef.MCEF;
import net.montoyo.mcef.client.ClientProxy;

public class HTMLGui extends ViewGui {

    public HTMLGui() {
        super(new Page("https://assets.sliceclient.com/clickgui/"));
    }

    /**
     * Prevent Cef from destroying
     *
     * @see ViewGui
     * */
    @Override
    public void close() {}

    /**
     * When the gui is opened
     *
     * @see net.minecraft.client.gui.screen.Screen
     * */
    @Override
    public void init() {
        super.init();
    }

    /**
     * Adds a module to the ClickGui iframe
     * (using javascript)
     *
     * @param module the module
     * */
    public void addModule(Module module) {
        runOnIFrame("addModule(\"" + module.getCategory().getName() + "\", \"" + module.getName() + "\")");

        for(Setting setting : module.getSettings()) {
            if(setting instanceof BooleanValue bv) {
                runOnIFrame("addSettingToModule(\"" + module.getName() + "\", \"BooleanValue\", \"" + bv.getName() + "\"," + "\"" + bv.getValue() + "\"" + ")");
            }
            if(setting instanceof NumberValue nv) {
                runOnIFrame("addSettingToModule(\"" + module.getName() + "\", \"NumberValue\", \"" + nv.getName() + "\"," + "\"" + nv.getValue() + "\"," + nv.getMin() + ", " + nv.getMax() + ")");
            }
            if(setting instanceof ModeValue mv) {
                StringBuilder args = new StringBuilder();
                args.append("\"").append(module.getName()).append("\"").append(",");
                args.append("\"").append("ModeValue").append("\"").append(",");
                args.append("\"").append(mv.getName()).append("\"").append(",");
                args.append("\"").append(mv.getValue()).append("\"").append(",");
                int index = 0;
                for(String s : mv.getValues()) {
                    index++;
                    if(index == mv.getValues().length) args.append("\"").append(s).append("\"");
                    else args.append("\"").append(s).append("\"").append(",");
                }
                runOnIFrame("addSettingToModule(" + args + ")");
            }
            if (module.isEnabled()) {
                setEnabled(module.getName(), true);
            }
        }
    }

    /**
     * runs javascript on the ClickGui's iframe
     *
     * @param js the javascript to execute on the frame
     * */
    public void runOnIFrame(String js) {
        javascript("document.querySelector(\"iframe\").contentWindow." + js);
    }

    /**
     * Executes javascript
     *
     * @param js the javascript
     * */
    private void javascript(String js) {
        getBrowser().runJS(js, getBrowser().getURL());
    }

    /**
     * Inits the modules
     * */
    public void queryInit() {
        for(Module module : Slice.INSTANCE.getModuleManager().getModules()) addModule(module);
    }

    public void setEnabled(String module, Boolean enabled) {
        runOnIFrame("setEnabled(\"" + module + "\", " + enabled + ")");
    }

    public void setHidden(Module module, Setting setting, boolean hidden) {
        runOnIFrame("hideSetting(\"" + module.getName() + "\", \"" + setting.getName() + "\", " + hidden + ")");
    }

    public void updateBooleanValue(Module module, BooleanValue booleanValue, boolean value) {
        runOnIFrame("updateBooleanValue(\"" + module.getName() + "\", \"" + booleanValue.getName() + "\", " + value + ")");
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

}
