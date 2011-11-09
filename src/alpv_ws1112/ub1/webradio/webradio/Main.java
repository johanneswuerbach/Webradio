package alpv_ws1112.ub1.webradio.webradio;

import java.io.IOException;

import alpv_ws1112.ub1.webradio.communication.Client;
import alpv_ws1112.ub1.webradio.communication.Server;
import alpv_ws1112.ub1.webradio.communication.protobuf.ClientProtoBuf;
import alpv_ws1112.ub1.webradio.communication.protobuf.ServerProtoBuf;
import alpv_ws1112.ub1.webradio.communication.tcp.ClientTCP;
import alpv_ws1112.ub1.webradio.communication.tcp.ServerTCP;
import alpv_ws1112.ub1.webradio.ui.ClientUI;
import alpv_ws1112.ub1.webradio.ui.ServerUI;
import alpv_ws1112.ub1.webradio.ui.cmd.ClientCMD;
import alpv_ws1112.ub1.webradio.ui.cmd.ServerCMD;
import alpv_ws1112.ub1.webradio.ui.swing.ClientSwing;
import alpv_ws1112.ub1.webradio.ui.swing.ServerSwing;

public class Main {
	private static final String USAGE = String
			.format("usage: java -jar UB%%X_%%NAMEN [-options] server tcp|udp|mc PORT%n"
					+ "         (to start a server)%n"
					+ "or:    java -jar UB%%X_%%NAMEN [-options] client tcp|udp|mc SERVERIPADDRESS SERVERPORT USERNAME%n"
					+ "         (to start a client)");
	
	public static ServerUI serverUI = null;
	public static ClientUI clientUI = null;
	public static Thread serverUIThread = null;
	public static Thread clientUIThread = null;

	/**
	 * Starts a server/client according to the given arguments, using a GUI or
	 * just the command-line according to the given arguments.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			boolean useGUI = false;
			int argumentIndex = -1;

			// Parse options. Add additional options here if you have to. Do not
			// forget to mention their usage in the help-string!
			while (args[++argumentIndex].startsWith("-")) {
				if (args[argumentIndex].equals("-help")) {
					System.out.println(USAGE
							+ String.format("%n%nwhere options include:"));
					System.out.println("  -help      Show this text.");
					System.out
							.println("  -gui       Show a graphical user interface.");
					System.exit(0);
				} else if (args[argumentIndex].equals("-gui")) {
					useGUI = true;
				}
			}

			if (args[argumentIndex].equals("server")) {
				Server server = null;
				String protocol = args[argumentIndex + 1];
				int port = Integer.parseInt(args[argumentIndex + 2]);

				if (protocol.equals("tcp")) {
					server = new ServerTCP(port);
				} else if (protocol.equals("protobuf")) {
					server = new ServerProtoBuf(port);
				} else {
					System.err.println("protcol " + protocol
							+ " is not supported.");
					return;
				}
				Thread serverThread = new Thread(server);
				serverThread.start();
				
				// Run UI
				if (useGUI) {
					serverUI = new ServerSwing(server);
				} else {
					serverUI = new ServerCMD(server);
				}
				serverUIThread = new Thread(serverUI);
				serverUIThread.start();
				
			} else if (args[argumentIndex].equals("client")) {
				String protocol = args[argumentIndex + 1];
				String host = args[argumentIndex + 2];
				int port = Integer.parseInt(args[argumentIndex + 3]);
				String username = args[argumentIndex + 4];
				Client client = null;

				if (protocol.equals("tcp")) {
					client = new ClientTCP(host, port);
				} else if (protocol.equals("protobuf")) {
					client = new ClientProtoBuf(host, port);
				} else {
					System.err.println("protcol " + protocol
							+ " is not supported.");
					return;
				}
				Thread clientThread = new Thread(client);
				clientThread.start();
				
				// Run UI
				clientUI = null;
				if (useGUI) {
					clientUI = new ClientSwing(client, username);
				} else {
					clientUI = new ClientCMD(client, username);
				}
				clientUIThread = new Thread(clientUI);
				clientUIThread.start();
				
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
