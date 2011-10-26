package alpv_ws1112.ub1.webradio.webradio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URL;

import alpv_ws1112.ub1.webradio.communication.tcp.ClientTCP;
import alpv_ws1112.ub1.webradio.communication.tcp.ServerTcp;
import alpv_ws1112.ub1.webradio.ui.cmd.ClientCMD;
import alpv_ws1112.ub1.webradio.ui.cmd.ServerCMD;

public class Main {
	private static final String USAGE = String
			.format("usage: java -jar UB%%X_%%NAMEN [-options] server tcp|udp|mc PORT%n"
					+ "         (to start a server)%n"
					+ "or:    java -jar UB%%X_%%NAMEN [-options] client tcp|udp|mc SERVERIPADDRESS SERVERPORT USERNAME%n"
					+ "         (to start a client)");

	/**
	 * Starts a server/client according to the given arguments, using a GUI or
	 * just the command-line according to the given arguments.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			boolean useGUI = false;
			int i = -1;

			// Parse options. Add additional options here if you have to. Do not
			// forget to mention their usage in the help-string!
			while (args[++i].startsWith("-")) {
				if (args[i].equals("-help")) {
					System.out.println(USAGE
							+ String.format("%n%nwhere options include:"));
					System.out.println("  -help      Show this text.");
					System.out
							.println("  -gui       Show a graphical user interface.");
					System.exit(0);
				} else if (args[i].equals("-gui")) {
					useGUI = true;
				}
			}

			if (args[i].equals("server")) {
				String protocol = args[i + 1];
				int port = Integer.parseInt(args[i + 2]);
				if (protocol.equals("tcp")) {
					ServerTcp server = new ServerTcp(port);
					server.writeHelloWorld();
				} else if (protocol.equals("udp")) {
					System.err.println("udp not supported.");
				} else if (protocol.equals("mc")) {
					System.err.println("mc not supported.");
				} else {
					System.err.println("protcol " + protocol
							+ " is not supported.");
				}
				if (useGUI) {
					//TBD
				} else {
					Runnable serverCmd = new ServerCMD();
					Thread serverCmdThread = new Thread(serverCmd);
					serverCmdThread.start();
				}

			} else if (args[i].equals("client")) {
				URL server = new URL(args[i + 1]);
				String protocol = args[i + 1];
				
				if (protocol.equals("tcp")) {
					ClientTCP client = new ClientTCP();
					
					String host = args[i + 2];
					int port = Integer.parseInt(args[i + 2]);
					client.connect(InetSocketAddress.createUnresolved(host, port));
					
				} else if (protocol.equals("udp")) {
					System.err.println("udp not supported.");
				} else if (protocol.equals("mc")) {
					System.err.println("mc not supported.");
				} else {
					System.err.println("protcol " + protocol
							+ " is not supported.");
				}
				if (useGUI) {
					//TBD
				} else {
					Runnable clientCmd = new ClientCMD(args[i + 3]);
					Thread clientCmdThread = new Thread(clientCmd);
					clientCmdThread.start();
				}
			} else {
				throw new IllegalArgumentException();
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			System.err.println(USAGE);
		} catch (NumberFormatException e) {
			System.err.println(USAGE);
		} catch (IllegalArgumentException e) {
			System.err.println(USAGE);
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}
}
