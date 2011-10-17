/*
 *  This file is part of the Haven & Hearth game client.
 *  Copyright (C) 2009 Fredrik Tolf <fredrik@dolda2000.com>, and
 *                     Björn Johannessen <johannessen.bjorn@gmail.com>
 *
 *  Redistribution and/or modification of this file is subject to the
 *  terms of the GNU Lesser General Public License, version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  Other parts of this source tree adhere to other copying
 *  rights. Please see the file `COPYING' in the root directory of the
 *  source tree for details.
 *
 *  A copy the GNU Lesser General Public License is distributed along
 *  with the source tree of which this file is a part in the file
 *  `doc/LPGL-3'. If it is missing for any reason, please see the Free
 *  Software Foundation's website at <http://www.fsf.org/>, or write
 *  to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 *  Boston, MA 02111-1307 USA
 */

package haven;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.security.MessageDigest;

public class AuthClient {
    private static final SslHelper ssl;
    private static final int CMD_USR = 1;
    private static final int CMD_PASSWD = 2;
    private static final int CMD_GETTOKEN = 3;
    private static final int CMD_USETOKEN = 4;
    private Socket sk;
    private InputStream skin;
    private OutputStream skout;
    public byte[] cookie, token;

    static {
        ssl = new SslHelper();
        try {
            ssl.trust(SslHelper.loadX509(Resource.class.getResourceAsStream("authsrv.crt")));
        } catch (Exception e) {
            throw (new RuntimeException(e));
        }
    }

    public AuthClient(final String host, final String username) throws IOException {
        sk = ssl.connect(host, 1871);
        skin = sk.getInputStream();
        skout = sk.getOutputStream();
        binduser(username);
    }

    public void binduser(final String username) throws IOException {
        final Message msg = new Message(CMD_USR);
        msg.addstring2(username);
        sendmsg(msg);
        final Message rpl = recvmsg();
        if (rpl.type != 0)
            throw (new IOException("Unhandled reply " + rpl.type + " when binding username"));
    }

    private static byte[] digest(final String pw) {
        final MessageDigest dig;
        final byte[] buf;
        try {
            dig = MessageDigest.getInstance("SHA-256");
            buf = pw.getBytes("utf-8");
        } catch (java.security.NoSuchAlgorithmException e) {
            throw (new RuntimeException(e));
        } catch (java.io.UnsupportedEncodingException e) {
            throw (new RuntimeException(e));
        }
        dig.update(buf);
        for (int i = 0; i < buf.length; i++)
            buf[i] = 0;
        return (dig.digest());
    }

    public boolean trypasswd(final String pw) throws IOException {
        final byte[] phash = digest(pw);
        sendmsg(new Message(CMD_PASSWD, phash));
        final Message rpl = recvmsg();
        if (rpl.type == 0) {
            cookie = rpl.blob;
            return (true);
        } else {
            return (false);
        }
    }

    public boolean trytoken(final byte[] token) throws IOException {
        sendmsg(new Message(CMD_USETOKEN, token));
        final Message rpl = recvmsg();
        if (rpl.type == 0) {
            cookie = rpl.blob;
            return (true);
        } else {
            return (false);
        }
    }

    public boolean gettoken() throws IOException {
        sendmsg(new Message(CMD_GETTOKEN));
        final Message rpl = recvmsg();
        if (rpl.type == 0) {
            token = rpl.blob;
            return (true);
        } else {
            return (false);
        }
    }

    public void close() throws IOException {
        sk.close();
    }

    private void sendmsg(final Message msg) throws IOException {
        if (msg.blob.length > 255)
            throw (new RuntimeException("Too long message in AuthClient (" + msg.blob.length + " bytes)"));
        final byte[] buf = new byte[msg.blob.length + 2];
        buf[0] = (byte) msg.type;
        buf[1] = (byte) msg.blob.length;
        System.arraycopy(msg.blob, 0, buf, 2, msg.blob.length);
        skout.write(buf);
    }

    private static void readall(final InputStream in, final byte[] buf) throws IOException {
        int rv;
        for (int i = 0; i < buf.length; i += rv) {
            rv = in.read(buf, i, buf.length - i);
            if (rv < 0)
                throw (new IOException("Premature end of input"));
        }
    }

    private Message recvmsg() throws IOException {
        final byte[] header = new byte[2];
        readall(skin, header);
        final byte[] buf = new byte[header[1]];
        readall(skin, buf);
        return (new Message(header[0], buf));
    }

    public static void main(final String[] args) throws Exception {
        final AuthClient test = new AuthClient("127.0.0.1", args[0]);
        System.out.println(test.trypasswd(args[1]));
        if (test.cookie != null) {
            for (final byte b : test.cookie)
                System.out.print(String.format("%02X ", b));
            System.out.println();
        }
        test.close();
    }
}
