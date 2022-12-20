package com.sliceclient.command.commands;

import com.sliceclient.Slice;
import com.sliceclient.command.Command;
import com.sliceclient.command.data.CommandInfo;
import com.sliceclient.util.LoggerUtil;

@CommandInfo(name = "connect", description = "reconnects to the server", aliases = {"c"})
public class CommandConnect extends Command {

    @Override
    public boolean handle(String name, String[] args) {
        if(Slice.INSTANCE.getIrc().getSocket().connected()) Slice.INSTANCE.getIrc().getSocket().disconnect();
        Slice.INSTANCE.getIrc().getSocket().connect();
        new Thread(() -> {
            try {
                while (!Slice.INSTANCE.getIrc().getSocket().connected()) {}
                LoggerUtil.addMessage("Connected to the server!");
                Slice.INSTANCE.getIrc().getSocketEvents().runConnected();
            } catch (Exception ignored) {}
        }).start();
        return false;
    }
}
