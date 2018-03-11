import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class Server {
	private Socket socket = null;
	static final String IP = "127.0.0.1";
	static final int PORT = 6969;
	static final int NUM_TEST_ITERS = 10;
	static final int KB_BYTES = 1024;
	static final int[] MESSAGE_SIZES_KB = {1024};
	// static final int[] MESSAGE_SIZES_KB = {1,10,100,200,300,400,500,800,1024};

	static String generateRandString(int size) {
		String randStr = "";
		while (randStr.length() < size) {
			randStr += UUID.randomUUID();
		}
		return "{\"message\":\"" + randStr.substring(0, size - 14) + "\"}";
	}

	public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException {
		Map<Integer, Double> testDurAvgs = new HashMap<>();
		for (int messageSizeKB : MESSAGE_SIZES_KB) {
			System.out.println("Running benchmark for message size of " + messageSizeKB + "KB");
			// String message = generateRandString(messageSizeKB*KB_BYTES);
			// Files.write(Paths.get("test_payloads/" + messageSizeKB + "kb"), message.getBytes(), StandardOpenOption.CREATE);
			String message = new String(Files.readAllBytes(Paths.get("test_payloads/" + messageSizeKB + "kb")));

			Server client = new Server();
			double[] testDurationsMis = new double[NUM_TEST_ITERS];
			for (int iter = 0; iter < NUM_TEST_ITERS; iter++) {
				System.out.println("Echo testing message of size " + message.length() + " bytes");
				client.socketConnect(IP, PORT);
				System.out.println("Sending message of size: " + message.length() + " bytes");
				long testStartTimeStampNano = System.nanoTime();
				String returnStr = client.echo(message);
				double socketTransactionDurMs = (System.nanoTime() - testStartTimeStampNano) / 10e6;
				System.out.println("Received message of size: " + returnStr.length() + " bytes");
				System.out.println("Echo test completed in " + socketTransactionDurMs + "ms\n");
				testDurationsMis[iter] = socketTransactionDurMs;
			}

			double testDurAvgMis = Arrays.stream(testDurationsMis).average().getAsDouble();
			testDurAvgs.put(messageSizeKB, testDurAvgMis);
			System.out.println("Echo tests avg duration was: " + testDurAvgMis + " ms\n");
		}

		System.out.println(String.format("Echo Tests Duration Summary (N=%d):", NUM_TEST_ITERS));
		for (int messageSizeKB : MESSAGE_SIZES_KB) {
			System.out.println(String.format("\t* %dKB: %.2fms", messageSizeKB, testDurAvgs.get(messageSizeKB)));
		}
	}

	// make the connection with the socket
	private void socketConnect(String ip, int port) throws UnknownHostException, IOException {
		System.out.println("[Connecting to socket...]");
		this.socket = new Socket(ip, port);
	}

	// writes and receives the full message int the socket (String)
	public String echo(String message) {
		try {
			// out & in
			PrintWriter out = new PrintWriter(getSocket().getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(getSocket().getInputStream()));
			// writes str in the socket and read
			out.println(message);
			String returnStr = in.readLine();
			return returnStr;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	// get the socket instance
	private Socket getSocket() {
		return socket;
	}
}