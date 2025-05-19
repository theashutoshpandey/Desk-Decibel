package com.desk.decibel;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.imageio.ImageIO;


/**
 * Handles individual client socket connections for the Desk Decibel streaming server.
 * Responds to various HTTP GET endpoints for static content and screen streaming.
 *
 * Supports:
 * - Favicon/logo serving
 * - HTML page delivery
 * - Live MJPEG desktop streaming
 * - Authorization handshake
 *
 * Author: Ashutosh Pandey
 */
public class ServerSocketController extends Thread {

    private final Socket socket;
    private final String boundary = "stream";
    private OutputStream outputStream;

    /**
     * Constructs a new controller thread for an incoming socket.
     */
    public ServerSocketController(Socket socket) {
        this.socket = socket;
        System.out.println("New ServerSocketController thread started for client: " + socket.getInetAddress());
    }

    /**
     * Entry point for thread execution. Reads the HTTP request and delegates handling.
     */
    @Override
    public void run() {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true)) {

            String line;
            String requestLine = null;
            StringBuilder headers = new StringBuilder();

            System.out.println("Reading HTTP request from client...");

            while ((line = br.readLine()) != null && !line.isEmpty()) {
                if (line.startsWith("GET")) requestLine = line;
                headers.append(line).append("\n");
            }

            if (requestLine == null) {
                System.out.println("No valid GET request found. Closing socket.");
                socket.close();
                return;
            }

            System.out.println("Received request: " + requestLine);
            handleRequest(requestLine, pw);

        } catch (Exception ex) {
            System.err.println("Exception in handling client: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Parses the request line and routes it to the appropriate handler.
     */
    private void handleRequest(String requestLine, PrintWriter pw) throws IOException, InterruptedException {
        if (requestLine.contains("/authorize")) {
            System.out.println("Client requested /authorization endpoint.");
            Map<String, String> params = getQueryParams(requestLine);
            System.out.println("Authorization Details - Host: " + params.get("hostname") + ", Password: " + params.get("pwd"));
            sendResponse(pw, 200, "Authorization processed");
            socket.close();
            System.out.println("Connection closed after /authorization.");

        } else if (requestLine.contains("/logo.ico")) {
            System.out.println("Client requested favicon.");
            File favicon = new File("static/logo.ico");

            if (favicon.exists()) {
                sendBinaryFile("static/logo.ico", "image/x-icon");
                System.out.println("Favicon sent successfully.");
            } else {
                sendResponse(pw, 404, "Favicon not found.");
                System.out.println("Favicon not found. Sent 404.");
            }
            socket.close();

        } else if (requestLine.contains("/logo.png")) {
            System.out.println("Client requested logo image.");
            sendImageResponse("static/logo.png");
            socket.close();
            System.out.println("Connection closed after sending logo image.");

        } else if (requestLine.contains("/decibel")) {
            System.out.println("Client requested /desk-decibel page.");
            sendHtmlResponse(pw, "static/desk-decibel.html");
            socket.close();
            System.out.println("Connection closed after sending viewer HTML.");

        } else if (requestLine.contains("/viewer")) {
            System.out.println("Client requested /stream viewer page.");
            sendHtmlResponse(pw, "static/desk-viewer.html");
            socket.close();
            System.out.println("Connection closed after sending stream HTML.");

        } else if (requestLine.startsWith("GET /live")) {
            System.out.println("Client requested /live for live screen stream.");
            writeHeader(socket.getOutputStream(), boundary);
            System.out.println("MJPEG stream headers sent. Starting screen capture...");

            while (!socket.isClosed()) {
                Thread.sleep(1000 / 60);
                pushImage();
            }

        } else {
            System.out.println("Invalid path requested: " + requestLine);
            sendResponse(pw, 404, "404 Not Found - The requested resource was not found on this server.");
            socket.close();
            System.out.println("Connection closed after 404 response.");
        }
    }

    /**
     * Writes HTTP multipart headers for MJPEG streaming.
     */
    private void writeHeader(OutputStream stream, String boundary) throws IOException {
        String header = "HTTP/1.0 200 OK\r\n" +
                "Connection: close\r\n" +
                "Cache-Control: no-store, no-cache, must-revalidate\r\n" +
                "Pragma: no-cache\r\n" +
                "Content-Type: multipart/x-mixed-replace; boundary=" + boundary + "\r\n\r\n";
        stream.write(header.getBytes());
        stream.write(("--" + boundary + "\r\n").getBytes());
        System.out.println("Streaming header written to client.");
    }

    public void pushImage() throws IOException {
        BufferedImage img = getDesktopScreenshot();
        if (img == null) {
            System.out.println("Failed to capture screen. Skipping frame.");
            return;
        }

        try {
            if (outputStream == null) outputStream = socket.getOutputStream();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(img, "jpg", baos);
            byte[] imageBytes = baos.toByteArray();

            outputStream.write(("Content-type: image/jpeg\r\n" +
                    "Content-Length: " + imageBytes.length + "\r\n\r\n").getBytes());
            outputStream.write(imageBytes);
            outputStream.write(("\r\n--" + boundary + "\r\n").getBytes());
            System.out.println("Frame pushed to client: " + imageBytes.length + " bytes");
        } catch (IOException e) {
            socket.close();
            System.out.println("Client disconnected during stream.");
        }
    }

    private BufferedImage getDesktopScreenshot() {
        try {
            Robot robot = new Robot();
            Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
            BufferedImage screenshot = robot.createScreenCapture(screenRect);

            Image cursor = Toolkit.getDefaultToolkit().getImage("white.png");
            Point location = MouseInfo.getPointerInfo().getLocation();
            Graphics2D g2d = screenshot.createGraphics();
            g2d.drawImage(cursor, location.x, location.y, null);
            g2d.dispose();

            return screenshot;

        } catch (AWTException e) {
            System.err.println("Failed to capture screen: " + e.getMessage());
            return null;
        }
    }

    public static String readFileContent(String filePath) throws IOException {
        System.out.println("Reading file: " + filePath);
        return new String(Files.readAllBytes(new File(filePath).toPath()));
    }

    private Map<String, String> getQueryParams(String query) {
        Map<String, String> params = new LinkedHashMap<>();
        String[] parts = query.replace("?", "&").split("&");
        for (String part : parts) {
            if (part.contains("=")) {
                String[] kv = part.split("=", 2);
                params.put(kv[0].trim(), kv[1].trim());
            }
        }
        return params;
    }

    private void sendResponse(PrintWriter pw, int statusCode, String body) {
        pw.println("HTTP/1.1 " + statusCode + " OK");
        pw.println("Access-Control-Allow-Origin: *");
        pw.println("Content-Type: text/plain");
        pw.println("Content-Length: " + body.length());
        pw.println();
        pw.println(body);
        System.out.println("Sent response with status " + statusCode + ": " + body);
    }

    private void sendHtmlResponse(PrintWriter pw, String filePath) throws IOException {
        String content = readFileContent(filePath);
        pw.println("HTTP/1.1 200 OK");
        pw.println("Access-Control-Allow-Origin: *");
        pw.println("Content-Type: text/html");
        pw.println("Content-Length: " + content.length());
        pw.println();
        pw.println(content);
        System.out.println("Sent HTML file: " + filePath);
    }

    private void sendImageResponse(String path) throws IOException {
        File imageFile = new File(path);
        byte[] imageBytes = Files.readAllBytes(imageFile.toPath());

        OutputStream os = socket.getOutputStream();
        PrintWriter pw = new PrintWriter(os, true);
        pw.println("HTTP/1.1 200 OK");
        pw.println("Content-Type: image/png");
        pw.println("Content-Length: " + imageBytes.length);
        pw.println("Access-Control-Allow-Origin: *");
        pw.println();
        pw.flush();

        os.write(imageBytes);
        os.flush();
        System.out.println("Sent image file: " + path + " (" + imageBytes.length + " bytes)");
    }

    private void sendBinaryFile(String filePath, String mimeType) throws IOException {
        File file = new File(filePath);
        byte[] data = Files.readAllBytes(file.toPath());

        OutputStream os = socket.getOutputStream();
        PrintWriter pw = new PrintWriter(os, true);

        pw.println("HTTP/1.1 200 OK");
        pw.println("Content-Type: " + mimeType);
        pw.println("Content-Length: " + data.length);
        pw.println("Access-Control-Allow-Origin: *");
        pw.println();  // Ends headers
        pw.flush();

        os.write(data);
        os.flush();
    }

}
