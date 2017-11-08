package org.rev317.min.debug;

import org.parabot.core.paint.AbstractDebugger;
import org.rev317.min.Loader;
import org.rev317.min.accessors.Interface;
import org.rev317.min.api.events.MessageEvent;
import org.rev317.min.api.events.listeners.MessageListener;
import org.rev317.min.api.methods.Players;

import java.awt.*;

/**
 * kekgod - org.rev317.min.debug.
 */
public class DSearcher extends AbstractDebugger implements MessageListener {
    private boolean enabled;
    int startX = 40;
    int startY = 70;
    int index = 15;

    String searchFor = "";

    @Override
    public void paint(Graphics g) {
        g.drawString(searchFor, startX, startY);

        if (searchFor != "") {
            Interface[] iface = Loader.getClient().getInterfaceCache();
            for (int i = 0; i < iface.length; i++) {
                if (iface[i] != null) {
                    if (iface[i].getMessage() != null) {
                        if (iface[i].getMessage().contains(searchFor)) {
                            g.drawString(String.format("%s: [%s]", iface[i].getMessage(), i), startX, startY + index);
                            index += 15;
                        }
                    }
                }
            }
        }

    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void toggle() {
        enabled = !enabled;
    }

    @Override
    public void messageReceived(MessageEvent m) {
        String msg = m.getMessage();
        if(m.getSender() == Players.getMyPlayer().getName()) {
            searchFor = msg;
            index = 15;
        }

    }
}
