package org.rev317.min;

import org.parabot.core.Context;
import org.parabot.core.Core;
import org.parabot.core.Directories;
import org.parabot.core.asm.ASMClassLoader;
import org.parabot.core.asm.adapters.AddInterfaceAdapter;
import org.parabot.core.asm.hooks.HookFile;
import org.parabot.core.desc.ServerProviderInfo;
import org.parabot.core.reflect.RefClass;
import org.parabot.core.reflect.RefField;
import org.parabot.core.reflect.RefMethod;
import org.parabot.core.ui.components.GamePanel;
import org.parabot.core.ui.components.VerboseLoader;
import org.parabot.environment.api.utils.Time;
import org.parabot.environment.api.utils.WebUtil;
import org.parabot.environment.scripts.Script;
import org.parabot.environment.servers.ServerManifest;
import org.parabot.environment.servers.ServerProvider;
import org.parabot.environment.servers.Type;
import org.rev317.min.accessors.Client;
import org.rev317.min.script.ScriptEngine;
import org.rev317.min.ui.BotMenu;

import javax.swing.*;
import java.applet.Applet;
import java.awt.*;
import java.io.File;
import java.net.URL;

/**
 * @author Everel, JKetelaar
 * Revamped loader for SoulPlay 2018 by Shadowrs & EmmaStone
 */
@ServerManifest(author = "Everel & JKetelaar", name = "Server name here", type = Type.INJECTION, version = 2.1)
public class Loader extends ServerProvider {
    private boolean extended = true;

    public static Client getClient() {
        return (Client) Context.getInstance().getClient();
    }

    @Override
    public Applet fetchApplet() {
        try {
            final Context context = Context.getInstance();
            final ASMClassLoader classLoader = context.getASMClassLoader();
            final Class<?> clientClass = classLoader.loadClass(Context.getInstance().getServerProviderInfo().getClientClass());

            clientClass.getMethod("main", new Class[] {String[].class}).invoke(null, new Object[] {new String[]{ }});

            new Thread(new Runnable() {
                @Override
                public void run() {
                    int i = 0;
                    while (i++ < 100) {
                        try {
                            RefField clientInstance = new RefClass(classLoader.loadClass(Context.getInstance().getServerProviderInfo().getClientClass())).getField("CE");
                            if (clientInstance.asObject() != null) {
                                RefClass       appletClass = new RefClass(classLoader.loadClass("iiIIIIiiiiiIIii"), clientInstance.asObject());
                                final RefField refField    = appletClass.getField("import");
                                if (refField.asObject() != null) {
                                    JFrame frame = (JFrame) refField.asObject();
                                    if (frame.isVisible()) {
                                        System.out.println("Disposing the extra frame");
                                        frame.dispose();
                                        Context.getInstance().setClientInstance(clientInstance.asObject());
                                        final RefField canvasField = appletClass.getField("public");
                                        GamePanel      panel       = GamePanel.getInstance();
                                        Applet         applet      = (Applet) Context.getInstance().getClient();

                                        panel.add((Component) canvasField.asObject());
                                        applet.repaint();
                                        appletClass.getMethod("false").invoke();
                                        appletClass.getMethod("true").invoke();
                                        applet.paintAll(applet.getGraphics());
                                        break;
                                    }
                                }
                            }
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                        Time.sleep(200);
                    }
                }
            }).start();

            return null;
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }

    }

    @Override
    public URL getJar() {
        ServerProviderInfo serverProvider = Context.getInstance().getServerProviderInfo();

        File target = new File(Directories.getCachePath(), serverProvider.getClientCRC32() + ".jar");
        if (!target.exists()) {
            WebUtil.downloadFile(serverProvider.getClient(), target, VerboseLoader.get());
        }

        return WebUtil.toURL(target);
    }

    @Override
    public void addMenuItems(JMenuBar bar) {
        new BotMenu(bar);
    }

    @Override
    public void injectHooks() {
        AddInterfaceAdapter.setAccessorPackage("org/rev317/min/accessors/");
        try {
            super.injectHooks();
        } catch (Exception e) {
            if (Core.inVerboseMode()) {
                e.printStackTrace();
            }
            this.extended = false;
            super.injectHooks();
        }
    }

    @Override
    public void initScript(Script script) {
        ScriptEngine.getInstance().setScript(script);
        ScriptEngine.getInstance().init();
    }

    @Override
    public HookFile getHookFile() {
        if (this.extended) {
            return new HookFile(Context.getInstance().getServerProviderInfo().getExtendedHookFile(), HookFile.TYPE_XML);
        } else {
            return new HookFile(Context.getInstance().getServerProviderInfo().getHookFile(), HookFile.TYPE_XML);
        }
    }

    public void unloadScript(Script script) {
        ScriptEngine.getInstance().unload();
    }

    @Override
    public void init() {
    }
}
