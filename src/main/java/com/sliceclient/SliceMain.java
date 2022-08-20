package com.sliceclient;

import net.fabricmc.api.ModInitializer;
import net.montoyo.mcef.MCEF;

public class SliceMain implements ModInitializer {
    public static MCEF MCEF;
    public static Slice INSTANCE;

    @Override
    public void onInitialize() {
        MCEF = new MCEF();
        MCEF.onInitialize();
        INSTANCE = Slice.INSTANCE;
    }
}
