package haiderpabon.guias.ejemploconexionhttp.datos;

import java.io.Serializable;

/**
 * Clase entidad: representa un registro de la tabla Usuarios.
 * Created by Haider Pabon Mejia
 */
public class Usuario implements Serializable {

    // PROPIEDADES
    private String email;
    private String password;
    private String nombre;

    // CONSTRUCTOR POR DEFECTO
    public Usuario() {

    }

    // CONSTRUCTOR CON PARAMETROS
    public Usuario(String email, String password, String nombre) {
        this.email = email;
        this.password = password;
        this.nombre = nombre;
    }

    // METODOS SET Y GET
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "email='" + email + '\'' +
                ", Password='" + password + '\'' +
                ", Nombre='" + nombre + '\'' +
                '}';
    }
}
