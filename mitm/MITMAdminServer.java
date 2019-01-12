/**
 * CSE 490K Project 2
 */

package mitm;

import java.net.*;
import java.io.*;
import java.security.GeneralSecurityException;
import java.util.regex.*;

// You need to add code to do the following
// 1) use SSL sockets instead of the plain sockets provided
// 2) check user authentication
// 3) perform the given administration command

class MITMAdminServer implements Runnable {
    private ServerSocket m_serverSocket;
    private Socket m_socket = null;
    private mitm.HTTPSProxyEngine m_engine;
    private static final String SALT = "$2a$12$zGxzQujm3NRaUD34F0nyce";


    public MITMAdminServer(String localHost, int adminPort, mitm.HTTPSProxyEngine engine) throws IOException, GeneralSecurityException {
        mitm.MITMSSLSocketFactory socketFactory = new mitm.MITMSSLSocketFactory();

        m_serverSocket = socketFactory.createServerSocket(localHost, adminPort, 0);
        m_engine = engine;

    }

    public void run() {
        System.out.println("Admin server initialized, listening on port " + m_serverSocket.getLocalPort());
        while (true) {
            try {
                m_socket = m_serverSocket.accept();

                byte[] buffer = new byte[40960];

                Pattern userPwdPattern =
                        Pattern.compile("username:(\\S+)\\s+password:(\\S+)\\s+command:(\\S+)\\sCN:(\\S*)\\s");


                BufferedInputStream in =
                        new BufferedInputStream(m_socket.getInputStream(),
                                buffer.length);

                // Read a buffer full.
                int bytesRead = in.read(buffer);

                String line =
                        bytesRead > 0 ?
                                new String(buffer, 0, bytesRead) : "";

                Matcher userPwdMatcher =
                        userPwdPattern.matcher(line);

                // parse username and pwd
                if (userPwdMatcher.find()) {
                    String userName = userPwdMatcher.group(1);
                    String password = userPwdMatcher.group(2);

                    //************************************************************************************

                    // TODO: authenticate the user
                    boolean authenticated = false;

                    String hashedInput = mitm.BCrypt.hashpw(password, SALT);

                    if (hashedInput.equals(getHash(userName))) {
                        authenticated = true;
                    }

                    // if authenticated, do the command
                    if (authenticated) {
                        String command = userPwdMatcher.group(3);
                        String commonName = userPwdMatcher.group(4);
                        doCommand(command);
                    }

                    //************************************************************************************

                }
            } catch (InterruptedIOException e) {
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //************************************************************************************

    public static String getHash(String userName) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("hashedPwdFile"));
        String line;
        String hashedPass = null;
        while ((line = br.readLine()) != null) {
            if (line.equals(userName)) {
                hashedPass = br.readLine();
                break;
            }
        }
        br.close();
        return hashedPass;
    }

    // TODO implement the commands
    private void sendMessage(final String msg) throws IOException {
        PrintWriter writer = new PrintWriter(m_socket.getOutputStream());
        writer.println(msg);
        writer.flush();

    }

    private void doCommand(String cmd) throws IOException {
        if (cmd.equals("stats")) {
            sendMessage("Number of requests have been received : " + m_engine.proxiedRequests);
        } else if (cmd.equals("shutdown")) {
            sendMessage("System shutting down!!!");
            System.exit(0);
        } else {
            sendMessage("Command " + cmd + " not recognized!");
        }


        m_socket.close();

    }

    //************************************************************************************

}
