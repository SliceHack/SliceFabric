//package com.sliceclient.command.commands;
//
//import com.sliceclient.Slice;
//import com.sliceclient.command.Command;
//import com.sliceclient.command.data.CommandInfo;
//import com.sliceclient.script.Script;
//
//@CommandInfo(name = "reload", description = "Reloads a script", aliases = {"reloadscript"})
//public class CommandReload extends Command {
//
//    @Override
//    public boolean handle(String name, String[] args) {
//        if(args.length == 0) {
//            addMessage("&cUsage&7: reload <script>");
//            return false;
//        }
//        String path = args[0];
//
//        Script script = Slice.INSTANCE.getScriptManager().getScript(path);
//        if(script == null) {
//
//            ScriptLoader scriptLoader = Slice.INSTANCE.getScriptManager().getJarScript(path);
//            if(scriptLoader == null) {
//                addMessage("&cScript not found.");
//                return false;
//            }
//
//            scriptLoader.getScript().onShutdown();
//            scriptLoader.reload();
//            addMessage("&aScript reloaded.");
//            return false;
//        }
//        script.reloadScript();
//        addMessage("&aScript reloaded");
//        return false;
//    }
//}
