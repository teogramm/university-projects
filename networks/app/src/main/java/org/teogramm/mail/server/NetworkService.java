package org.teogramm.mail.server;

import org.teogramm.mail.server.processing.RequestProcessor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Service that listens for new network connections and launches mew Connection handlers for each request
 */
public class NetworkService implements Runnable {
    private final ServerSocket serverSocket;
    private final ExecutorService pool;
    private final RequestProcessor requestProcessor;


    /**
     * Create a new NetworkService object
     * @param port Port on which the NetworkService will listen for connections
     * @param requestProcessor RequestProcessor object used for responding to client's requests
     */
    public NetworkService(int port, RequestProcessor requestProcessor) throws IOException {
        serverSocket = new ServerSocket(port);
        pool = Executors.newCachedThreadPool();
        this.requestProcessor = requestProcessor;
    }

    @Override
    public void run() {
        try {
            //noinspection InfiniteLoopStatement
            for (; ; ) {
                Socket clientSocket = serverSocket.accept();
                try{
                    pool.execute(new ConnectionHandler(clientSocket,requestProcessor));
                }catch (IOException e){
                    // Error while processing the request
                    System.err.println("Error while processing request for " + clientSocket.getInetAddress());
                }
            }
        } catch (IOException e) {
            // Error while accepting socket
            // If socket has been closed process the remaining
            // requests and shut down
            pool.shutdown();
        }
    }

    public void stop(){
        try {
            serverSocket.close();
        }catch (IOException ignored){}
    }
}
