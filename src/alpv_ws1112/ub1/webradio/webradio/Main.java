package alpv_ws1112.ub1.webradio.webradio;

import java.io.IOException;
import java.net.InetSocketAddress;

import alpv_ws1112.ub1.webradio.communication.tcp.ClientTCP;
import alpv_ws1112.ub1.webradio.communication.tcp.ServerTCP;
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
					ServerTCP server = new ServerTCP(port);
					
					Thread serverThread = new Thread(server);
					serverThread.start();
					
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
				String protocol = args[i + 1];
				
				if (protocol.equals("tcp")) {
					ClientTCP client = new ClientTCP();
					
					String host = args[i + 2];
					int port = Integer.parseInt(args[i + 3]);
					client.connect(InetSocketAddress.createUnresolved(host, port));
					
					Thread clientThread = new Thread(client);
					clientThread.start();
					
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
			e.printStackTrace();
		} catch (NumberFormatException e) {
			System.err.println(USAGE);
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			System.err.println(USAGE);
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}
}
