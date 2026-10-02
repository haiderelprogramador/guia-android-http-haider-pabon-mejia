// ConexionHttpPostServer - Haider Pabon Mejia
package haiderpabon.guias.ejemploconexionhttp.controladores;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class ConexionHttpPostServer {

    /*
     * URL del servicio PHP. CAMBIALA SEGUN DONDE PRUEBES LA APP:
     *  - Emulador de Android Studio:  http://10.0.2.2/crudphpjson/crud/operacion.php
     *    (10.0.2.2 es el "localhost" de tu PC visto desde el emulador)
     *  - Celular fisico en la misma red WiFi: usa la IP de tu PC (comando ipconfig),
     *    por ejemplo http://192.168.1.31/crudphpjson/crud/operacion.php
     */
    public static final String URL_SERVICIO = "http://10.0.2.2/crudphpjson/crud/operacion.php";

    // variable para guardar la URL del servidor al cual nos vamos a conectar
    public static String direccionDelServidor = URL_SERVICIO;
    // Variable para guardar la respuesta en formato JSON enviada por el servidor
    private String respuesta;
    // Variable para capturar y procesar los flujos de datos de entrada
    private InputStream datosEntrada;

    //--------------------------------------------------------
    /*
      Metodo para realizar conexiones HTTP al servidor Web.
      Recibe una lista con los parametros que enviaremos en la peticion POST
      y la URL del servidor. Retorna la respuesta que ha enviado el servidor.
    */
    public String conexionConElServidor(List<NameValuePair> parametros, String rutaDeLaAplicacionWeb) throws Exception {

        // Objeto para realizar la conexion con el servidor web
        HttpClient clienteHTTP = new HttpClient();
        // Tiempo maximo de espera (ms) para no quedar colgados si el servidor no responde
        clienteHTTP.getHttpConnectionManager().getParams().setConnectionTimeout(8000);
        clienteHTTP.getHttpConnectionManager().getParams().setSoTimeout(8000);
        // Objeto para realizar la Peticion Http POST
        PostMethod peticionPOST = new PostMethod(rutaDeLaAplicacionWeb);
        peticionPOST.getParams().setContentCharset("UTF-8");
        // Ciclo para recorrer la lista de parametros y agregarlos a la peticion POST
        for (NameValuePair parametro : parametros) {
            peticionPOST.addParameter(parametro);
        }
        // Bloque donde se realiza la peticion y se recibe la respuesta
        try {
            // Realizar la peticion POST y recibimos un codigo HTTP como respuesta
            int codigoRespuesta = clienteHTTP.executeMethod(peticionPOST);
            // Validamos si el codigo HTTP indica un problema con la peticion
            if (codigoRespuesta == HttpStatus.SC_NOT_IMPLEMENTED) {
                throw new Exception("ERROR 1: Parametros mal codificados");
            } else if (codigoRespuesta != HttpStatus.SC_OK) {
                throw new Exception("ERROR 2: URL invalida (HTTP " + codigoRespuesta + ")");
            }
            // Si el codigo es correcto, leemos los datos JSON de la respuesta
            else {
                datosEntrada = peticionPOST.getResponseBodyAsStream();
                if (datosEntrada == null) {
                    return null;
                }
                respuesta = procesarRespuestaDelServidor();
                return respuesta;
            }
        } // procesamos los posibles errores y finalmente cerramos la conexion
        catch (Exception e) {
            throw new Exception("ERROR 3: Conexion fallida:\n" + e.getMessage());
        } finally {
            peticionPOST.releaseConnection();
        }
    }

    //--------------------------------------------------------
    /*
      Metodo para procesar la respuesta a partir del flujo de entrada
      obtenido de la conexion con el servidor. Retorna el JSON como texto.
    */
    private String procesarRespuestaDelServidor() throws Exception {
        BufferedReader lectorDatos;
        try {
            // El servidor PHP responde en UTF-8 (tildes y enies)
            lectorDatos = new BufferedReader(new InputStreamReader(datosEntrada, "UTF-8"), 8);
        } catch (UnsupportedEncodingException error) {
            throw new Exception("ERROR 4: Sin respuesta\n" + error.getMessage());
        }
        String linea;                                  // cada linea leida de la respuesta
        StringBuilder json2 = new StringBuilder();     // la respuesta completa en JSON
        try {
            while ((linea = lectorDatos.readLine()) != null) {
                if (!linea.trim().isEmpty()) {
                    json2.append(linea);
                }
            }
        } catch (IOException error) {
            throw new Exception("ERROR 5: Sin respuesta\n" + error.getMessage());
        } finally {
            try {
                lectorDatos.close();
            } catch (Exception ignorado) {
            }
        }
        return json2.toString();
    }
}
