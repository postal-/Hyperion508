package org.hyperion.rs2;

import org.apache.mina.core.buffer.IoBuffer;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.PlayerDetails;
import org.hyperion.rs2.util.NameUtils;
import org.hyperion.util.Streams;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GenericWorldLoader implements WorldLoader {

    @Override
    public LoginResult checkLogin(PlayerDetails pd) {
        Player player = null;
        int code = 2;
        final File f = new File("data/savedGames/"
                + NameUtils.formatNameForProtocol(pd.getName()) + ".dat.gz");
        if (f.exists()) {
            try {
                final InputStream is = new GZIPInputStream(new FileInputStream(
                        f));
                final String name = Streams.readRS2String(is);
                final String pass = Streams.readRS2String(is);
                if (!name.equals(NameUtils.formatName(pd.getName()))) {
                    code = 3;
                }
                if (!pass.equals(pd.getPassword())) {
                    code = 3;
                }
            } catch (final IOException ex) {
                code = 11;
            }
        }
        if (code == 2) {
            player = new Player(pd);
        }
        return new LoginResult(code, player);
    }

    @Override
    public boolean savePlayer(Player player) {
        try {
            final OutputStream os = new GZIPOutputStream(new FileOutputStream(
                    "data/savedGames/"
                    + NameUtils.formatNameForProtocol(player.getName())
                    + ".dat.gz"));
            final IoBuffer buf = IoBuffer.allocate(1024);
            buf.setAutoExpand(true);
            player.serialize(buf);
            buf.flip();
            final byte[] data = new byte[buf.limit()];
            buf.get(data);
            os.write(data);
            os.flush();
            os.close();
            return true;
        } catch (final IOException ex) {
            return false;
        }
    }

    @Override
    public boolean loadPlayer(Player player) {
        try {
            final File f = new File("data/savedGames/"
                    + NameUtils.formatNameForProtocol(player.getName())
                    + ".dat.gz");
            final InputStream is = new GZIPInputStream(new FileInputStream(f));
            final IoBuffer buf = IoBuffer.allocate(1024);
            buf.setAutoExpand(true);
            while (true) {
                final byte[] temp = new byte[1024];
                final int read = is.read(temp, 0, temp.length);
                if (read == -1) {
                    break;
                } else {
                    buf.put(temp, 0, read);
                }
            }
            buf.flip();
            player.deserialize(buf);
            return true;
        } catch (final IOException ex) {
            return false;
        }
    }
}
