package org.example;


import org.junit.*;
import org.junit.Assert.*;
import static org.junit.Assert.*;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.imageio.ImageIO;


public class HttpServerTest {

    private static final int PORT = 35000; // Puerto de prueba

    @Before
    public void setUp() throws Exception {
        // Inicializar cualquier configuración necesaria antes de las pruebas
    }

    @After
    public void tearDown() throws Exception {
        // Limpiar cualquier recurso después de las pruebas
    }

    @Test
    public void testServerConnection() {
        // Prueba de conexión al puerto del servidor
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            // Éxito si no se lanza una excepción al abrir el ServerSocket
        } catch (IOException e) {
            fail("No se pudo abrir el puerto del servidor: " + e.getMessage());
        }
    }
}

