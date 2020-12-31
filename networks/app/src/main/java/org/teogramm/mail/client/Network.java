package org.teogramm.mail.client;

import com.google.gson.Gson;
import org.teogramm.mail.common.requests.MailRequest;
import org.teogramm.mail.common.MailResponse;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Object responsible for communication with the server
 */
public class Network {
    private final InetAddress serverAddress;
    private final int serverPort;

    /**
     * Creates a Network object that connects to the given server and
     * tries to connect.
     * @throws IOException If connection to the server fails
     */
    Network(InetAddress serverAddress, int port) throws IOException {
        this.serverAddress = serverAddress;
        this.serverPort = port;
        // Try to connect to see if given details are valid
        try(Socket s = new Socket(serverAddress,serverPort)){
        }
    }

    /**
     * Sends the given MailRequest object to the server
     * @throws IOException If an error occurs while communicating with the server
     * @return A MailResponse object with the server response
     */
    public MailResponse makeRequest(MailRequest request) throws IOException {
        try(Socket socket = new Socket(serverAddress,serverPort)) {

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            Gson gson = new Gson();

            // Serialize request and send it
            String serializedRequest = gson.toJson(request);
            out.write(serializedRequest);
            out.write('\n');
            out.flush();

            // Receive response and deserialize it
            return gson.fromJson(in.readLine(), MailResponse.class);
        }
    }
}
