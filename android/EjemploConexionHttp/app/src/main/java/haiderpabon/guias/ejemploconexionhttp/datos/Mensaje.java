package haiderpabon.guias.ejemploconexionhttp.datos;

/**
 * Representa las respuestas del servidor del tipo {"mensaje": "..."}
 * Created by Haider Pabon Mejia
 */
public class Mensaje {
    private String mensaje;

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getMensaje() {
        return mensaje;
    }
}
