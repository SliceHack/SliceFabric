package com.sliceclient;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SliceMain implements ModInitializer {

    public static Slice INSTANCE;


    @Override
    public void onInitialize() {
        INSTANCE = Slice.INSTANCE;
    }
}
