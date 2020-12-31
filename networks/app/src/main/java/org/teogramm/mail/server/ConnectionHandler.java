package org.teogramm.mail.server;

import com.google.gson.Gson;
import org.teogramm.mail.common.requests.MailRequest;
import org.teogramm.mail.common.MailResponse;
import org.teogramm.mail.server.processing.RequestProcessor;

import java.io.*;
import java.net.Socket;

/**
 * ConnectionHandler handles a single client request.
 */
public class ConnectionHandler implements Runnable{
    private final Socket socket;
    private final BufferedReader in;
    private final BufferedWriter out;
    private final RequestProcessor requestProcessor;

    /**
     * Create a new ConenctionHandler object
     * @param socket The socket that will be used for communication with the client
     * @param processor RequestProcessor object used to process the client's requests
     */
    public ConnectionHandler(Socket socket,RequestProcessor processor) throws IOException {
        this.socket = socket;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        this.requestProcessor = processor;
    }

    @Override
    public void run() {
        System.out.println("New connection from " + socket.getInetAddress() + ":" + socket.getPort());
        String json;
        try{
            json = in.readLine();
        }catch (IOException e){
            return;
        }
        Gson gson = new Gson();
        MailRequest request = gson.fromJson(json,MailRequest.class);
        MailResponse response = requestProcessor.processRequest(request);
        String serializedResponse = gson.toJson(response);
        try {
            out.write(serializedResponse);
            out.write('\n');
            out.flush();
            socket.close();
        } catch (IOException e) {
            System.err.println("Could send write response for " + socket.getInetAddress());
        }
    }
}
