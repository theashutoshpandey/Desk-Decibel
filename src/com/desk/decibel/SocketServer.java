package com.desk.decibel;

import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

/**
 * A simple SSL-less socket server that listens on a specified port and handles
 * incoming client connections.
 * Clients can interact with this server by connecting through a web browser.
 *
 * Usage:
 * - Create an instance of SocketServer and call startServer() to begin
 * listening for client connections.
 * - To stop the server, call stopServer().
 * - The server provides information about its connection status using the
 * isConnected() method.
 *
 * @author Ashutosh Pandey
 */
public class SocketServer {

    private int port;
    private boolean isConnected = false;
    private ServerSocket serverSocket;

    /**
     * Creates a new SocketServer instance that listens on the default port 8080.
     */
    public SocketServer() {
        this(8080);
    }

    /**
     * Creates a new SocketServer instance that listens on the specified port.
     */
    public SocketServer(int port) {
        this.port = port;
    }

    /**
     * Starts the socket server in a separate thread, allowing it to accept incoming
     * connections.
     * Once started, clients can connect through a web browser to the specified
     * port.
     */
    public void startServer() {
        new Thread(() -> {
            isConnected = true;
            openServerSocket();
        }).start();
    }

    /**
     * Opens the server socket and handles incoming client connections.
     * If the port is already in use, it will increment the port and retry.
     * If an exception occurs, the server's connection status is updated
     * accordingly.
     */
    private void openServerSocket() {
        try {
            // Create server socket
            serverSocket = new ServerSocket(port);
            System.out.println("SSL-less server started at port: " + this.port);
            String ip = InetAddress.getLocalHost().getHostAddress();
            System.out.println("Access the server at: http://" + ip + ":" + port + "/decibel using a web browser");

            while (!serverSocket.isClosed()) {
                // Session accepted by socket
                Socket socket = serverSocket.accept();
                // Start the server handler thread
                new ServerSocketController(socket).start();
            }
        } catch (BindException bex) {
            this.port++;
            openServerSocket();
        } catch (SocketException exs) {
            isConnected = false;
            System.out.println("Socket exception: " + exs.getMessage());
        } catch (Exception ex) {
            isConnected = false;
            ex.printStackTrace();
        }
    }

    /**
     * Checks whether the server is currently connected and accepting incoming
     * connections.
     *
     * @return true if the server is connected, false otherwise.
     */
    public boolean isConnected() {
        return this.isConnected;
    }

    /**
     * Stops the socket server by closing the server socket.
     * Any ongoing connections will be gracefully terminated.
     */
    public void stopServer() {
        try {
            serverSocket.close();
        } catch (Exception e) {
            System.out.println("stopServer method: " + e.getMessage());
        }
    }
}
