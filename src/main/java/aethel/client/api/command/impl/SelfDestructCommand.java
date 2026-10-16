package aethel.client.api.command.impl;

import net.minecraft.ChatFormatting;
import aethel.client.api.command.Command;
import aethel.client.manager.Manager;

/**
 * Vape-style self destruct from the chat: <prefix>selfdestruct
 */
public final class SelfDestructCommand extends Command {
    public SelfDestructCommand() {
        super("selfdestruct", "Hide Aethel instantly until the next game restart", "destruct");
    }

    @Override
    public void execute(String label, String[] args) {
        Manager manager = Manager.getInstance();
        if (manager == null) {
            logDirect("Client is not ready yet.", ChatFormatting.RED);
            return;
        }
        manager.selfDestruct();
    }
}
