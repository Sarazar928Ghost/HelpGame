package org.starloco.locos.game;

import org.starloco.locos.common.CryptManager;
import org.starloco.locos.game.world.World;
import org.starloco.locos.kernel.Config;
import org.starloco.locos.kernel.Main;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

public class GameHandler implements IoHandler {

    @Override
    public void sessionCreated(IoSession arg0) throws Exception {
        World.world.logger.info("Session " + arg0.getId() + " created");
        arg0.setAttachment(new GameClient(arg0));
        Main.refreshTitle();
    }

    @Override
    public void messageReceived(IoSession arg0, Object arg1) throws Exception {
        GameClient client = (GameClient) arg0.getAttachment();
        String packet = (String) arg1;

        String[] s = packet.split("\n");

        Integer i = new Integer(0);
        do {
            client.parsePacket(s[i]);
            if (Main.modDebug)
                World.world.logger.trace((client.getPlayer() == null ? "" : client.getPlayer().getName()) + " <-- " + s[i]);
            i++;
        } while (i == s.length - 1);
    }


    @Override
    public void sessionClosed(IoSession arg0) throws Exception {
        GameClient client = (GameClient) arg0.getAttachment();
        if(client != null)
            client.disconnect();
        World.world.logger.info("Session " + arg0.getId() + " closed");
    }

    @Override
    public void exceptionCaught(IoSession arg0, Throwable arg1) throws Exception {
        if(arg1 instanceof org.apache.mina.filter.codec.RecoverableProtocolDecoderException) {

        }
        arg1.printStackTrace();
        if (Main.modDebug)
            World.world.logger.error("Exception connexion client : " + arg1.getMessage());
        this.kick(arg0);
    }

    @Override
    public void messageSent(IoSession arg0, Object arg1) throws Exception {
        GameClient client = (GameClient) arg0.getAttachment();

        if (client != null) {
            if (Main.modDebug) {
                String packet = (String) arg1;
                if (packet.startsWith("am")) return;
                World.world.logger.trace((client.getPlayer() == null ? "" : client.getPlayer().getName()) + " --> " + packet);
            }
        }
    }

    @Override
    public void inputClosed(IoSession ioSession) throws Exception {
        ioSession.close(true);
    }

    @Override
    public void sessionIdle(IoSession arg0, IdleStatus arg1) throws Exception {
        World.world.logger.info("Session " + arg0.getId() + " idle");
}

    @Override
    public void sessionOpened(IoSession arg0) throws Exception {
        World.world.logger.info("Session " + arg0.getId() + " opened");
    }

    private void kick(IoSession arg0) {
        GameClient client = (GameClient) arg0.getAttachment();
        if (client != null) {
            client.kick();
            arg0.setAttachment(null);
        }
        Main.refreshTitle();
    }
}
