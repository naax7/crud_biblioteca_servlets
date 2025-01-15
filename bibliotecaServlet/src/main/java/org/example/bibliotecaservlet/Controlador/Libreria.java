package org.example.bibliotecaservlet.Controlador;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.bibliotecaservlet.Modelo.DAOGenerico;
import org.example.bibliotecaservlet.Modelo.Libro;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet(name = "Libreria", value = "/Libreria")
public class Libreria extends HttpServlet {

    DAOGenerico<Libro, String> daolibro;

    public void init() {
        daolibro = new DAOGenerico<>(Libro.class, String.class);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");

        String operacion = request.getParameter("operacion");
        String titulo = request.getParameter("titulo");
        String autor = request.getParameter("autor");
        String isbn = request.getParameter("isbn");

        PrintWriter impresora = response.getWriter();
        ObjectMapper conversorJson = new ObjectMapper();
        conversorJson.registerModule(new JavaTimeModule());
        String jsonResponse = null;

        switch (operacion) {
            case "Crear":
                // Crear un libro nuevo
                Libro libroCrear = new Libro(isbn, titulo, autor);
                daolibro.add(libroCrear);
                jsonResponse = conversorJson.writeValueAsString(libroCrear);
                break;

            case "Buscar":
                // Buscar libro por ISBN
                Libro libroBuscar = daolibro.getById(isbn);
                if (libroBuscar != null) {
                    jsonResponse = conversorJson.writeValueAsString(libroBuscar);
                } else {
                    jsonResponse = "{\"message\": \"Libro no encontrado\"}";
                }
                break;

            case "Modificar":
                // Modificar libro
                Libro libroModificar = daolibro.getById(isbn);
                if (libroModificar != null) {
                    libroModificar.setTitulo(titulo);
                    libroModificar.setAutor(autor);
                    daolibro.update(libroModificar);
                    jsonResponse = conversorJson.writeValueAsString(libroModificar);
                } else {
                    jsonResponse = "{\"message\": \"Libro no encontrado\"}";
                }
                break;

            case "Eliminar":
                // Eliminar libro
                Libro libroEliminar = daolibro.getById(isbn);
                if (libroEliminar != null) {
                    daolibro.delete(libroEliminar);
                    jsonResponse = "{\"message\": \"Libro eliminado\"}";
                } else {
                    jsonResponse = "{\"message\": \"Libro no encontrado\"}";
                }
                break;

            case "Todos":
                // Obtener todos los libros
                List<Libro> listaLibros = daolibro.getAll();
                jsonResponse = conversorJson.writeValueAsString(listaLibros);
                break;
        }

        impresora.println(jsonResponse);
    }

    public void destroy() {
    }
}