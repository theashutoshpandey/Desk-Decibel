package com.desk.decibel;

/**
 * Main entry point for the Desk Decibel backend server.
 *
 * This class initializes and starts the SocketServer on port 8080.
 * The SocketServer is responsible for handling authorization and streaming
 * connections for clients within the local network.
 *
 * Usage:
 * Run this class to start the server.
 * Ensure that port 8080 is available and not blocked by a firewall.
 *
 * Dependencies:
 * - SocketServer.java (handles actual socket and streaming logic)
 *
 * Author: Ashutosh Pandey
 */
public class Main {

    /**
     * Starts the Desk Decibel server on port 8080.
     */
    public static void main(String[] args) {
        SocketServer socketServer = new SocketServer(8080);
        socketServer.startServer();
    }
}
