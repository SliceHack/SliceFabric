package com.sliceclient.ui;

import com.sliceclient.cef.Page;
import com.sliceclient.cef.ViewGui;
import net.minecraft.client.MinecraftClient;
import net.montoyo.mcef.MCEF;
import net.montoyo.mcef.client.ClientProxy;

public class HTMLMainMenu extends ViewGui {

    public HTMLMainMenu() {
        super(new Page("https://assets.sliceclient.com/mainmenu/index.html?name=" + MinecraftClient.getInstance().getSession().getUsername()));
    }

    @Override
    public void tick() {
        ClientProxy clientProxy = (ClientProxy) MCEF.PROXY;
        clientProxy.onTickStart();
        super.tick();
    }
}
