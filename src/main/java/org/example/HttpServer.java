package org.example;

import java.net.*;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class HttpServer {

    public static void main(String[] args) throws IOException, URISyntaxException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(35000);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 35000.");
            System.exit(1);
        }

        boolean running = true;
        while (running) {
            Socket clientSocket = null;
            try {
                System.out.println("Listo para recibir ...");
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }

            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String inputLine, outputLine;

            boolean firstLine = true;
            String uriStr = "";

            while ((inputLine = in.readLine()) != null) {
                if (firstLine) {
                    uriStr = inputLine.split(" ")[1];
                    firstLine = false;
                }
                System.out.println("Received: " + inputLine);
                if (!in.ready()) {
                    break;
                }
            }

            URI requestUri = new URI(uriStr);

            try {
                outputLine = htttpResponse(requestUri);
            } catch (Exception e) {
                e.printStackTrace();
                outputLine = httpError();
            }

            out.write(outputLine);

            out.close();
            in.close();
            clientSocket.close();
        }
        serverSocket.close();
    }

    private static String httpError() {
        String outputLine = "HTTP/1.1 400 Not Found\r\n"
                + "Content-Type:text/html\r\n"
                + "\r\n"
                + "<!DOCTYPE html>\n"
                + "<html>\n"
                + "   <head>\n"
                + "       <title>Error Not found</title>\n"
                + "       <meta charset=\"UTF-8\">\n"
                + "       <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n"
                + "   </head>\n"
                + "   <body>\n"
                + "       <h1>Error</h1>\n"
                + "   </body>\n";
        return outputLine;
    }

    public static String htttpResponse(URI requestUri) throws IOException {
        Path file = Paths.get("target/classes/public/" + requestUri.getPath());
        System.out.println("Este es el path que se está solicitando:" + requestUri);

        String contentType = getContentType(requestUri.getPath());
        System.out.println("Este es el tipo del archivo que se esta enviando" + contentType);
        String outputLine;

        if (contentType.equals("image/jpeg")) {
            byte[] imageBytes = convertImageToBytes(file);
            String encodedImage = Base64.getEncoder().encodeToString(imageBytes); 
            outputLine = "HTTP/1.1 200 OK\r\n"
                    + "Content-Type: " + contentType + "\r\n"
                    + "Content-Length: " + encodedImage.length() + "\r\n"
                    + "\r\n";
            return outputLine + encodedImage; 
        } else {
            outputLine = "HTTP/1.1 200 OK\r\n"
                    + "Content-Type: " + contentType + "\r\n"
                    + "\r\n";
            Charset charset = Charset.forName("UTF-8");
            try (BufferedReader reader = Files.newBufferedReader(file, charset)) {
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line);
                }
                outputLine += content.toString();
            }
            return outputLine;
        }
    }

    private static byte[] convertImageToBytes(Path imagePath) throws IOException {
        BufferedImage image = ImageIO.read(imagePath.toFile());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", baos);
        return baos.toByteArray();
    }

    private static String getContentType(String filePath) {
        if (filePath.endsWith(".html")) {
            return "text/html";
        } else if (filePath.endsWith(".css")) {
            return "text/css";
        } else if (filePath.endsWith(".js")) {
            return "application/javascript";
        } else if (filePath.endsWith(".jpg")) {
            return "image/jpeg";
        } else {
            return "application/octet-stream";
        }
    }
}