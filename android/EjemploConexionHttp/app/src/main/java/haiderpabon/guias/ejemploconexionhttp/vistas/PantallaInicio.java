// PantallaInicio.java - Haider Pabon Mejia
package haiderpabon.guias.ejemploconexionhttp.vistas;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import org.apache.commons.httpclient.NameValuePair;

import java.util.ArrayList;

import haiderpabon.guias.ejemploconexionhttp.R;
import haiderpabon.guias.ejemploconexionhttp.controladores.ConexionHttpPostServer;
import haiderpabon.guias.ejemploconexionhttp.datos.Mensaje;
import haiderpabon.guias.ejemploconexionhttp.datos.Usuario;

public class PantallaInicio extends AppCompatActivity {
    private EditText campoEmail;
    private EditText campoPassword;
    private Button btnLogin;
    private Button botonCancelar;
    private ConexionHttpPostServer conexionServidor;
    // Usuario que inicio sesion (lo usan las otras pantallas)
    public static Usuario fulanito;
    private ProgressDialog barraDeprogreso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pantalla_inicio);
        campoEmail = (EditText) findViewById(R.id.campoEmail);
        campoPassword = (EditText) findViewById(R.id.campoClave);
        btnLogin = (Button) findViewById(R.id.btnIniciarSesion);
        botonCancelar = (Button) findViewById(R.id.btnCancelar);

        // Boton "Registrate": abre el formulario para crear un usuario nuevo
        botonCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fulanito = null; // no hay sesion: el formulario funciona en modo "Agregar"
                Intent intento = new Intent(PantallaInicio.this, PantallaCrudUsuario.class);
                startActivity(intento);
            }
        });

        // Boton "Entrar": valida los datos y hace el login en segundo plano
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verificarDatos()) {
                    String codigo = campoEmail.getText().toString().trim();
                    String password = campoPassword.getText().toString();

                    conexionServidor = new ConexionHttpPostServer();
                    TareaLoginEnSegunPlano tareaDeLogin = new TareaLoginEnSegunPlano();
                    tareaDeLogin.execute(codigo, password);
                }
            }
        });
    }

    private void mostrarDatosDeUsuarioEnOtraActividad() {
        if (fulanito != null) {
            Intent intento = new Intent(PantallaInicio.this, PantallaListado.class);
            intento.putExtra("sesion", fulanito);
            startActivity(intento);
        }
    }

    // Se ejecuta en segundo plano: NO se puede usar Toast aqui dentro
    private Object iniciarSesion(String codigo, String password) {
        fulanito = null;
        ArrayList<NameValuePair> listaDeParametros = new ArrayList<NameValuePair>();
        listaDeParametros.add(new NameValuePair("accion", "login"));
        listaDeParametros.add(new NameValuePair("email", codigo));
        listaDeParametros.add(new NameValuePair("psw", password));

        String respuestaDelServidorEnJSon;
        try {
            respuestaDelServidorEnJSon = conexionServidor.conexionConElServidor(
                    listaDeParametros, ConexionHttpPostServer.direccionDelServidor);
            System.out.println(respuestaDelServidorEnJSon);
        } catch (Exception error) {
            error.printStackTrace();
            Mensaje m = new Mensaje();
            m.setMensaje(error.getMessage());
            return m;
        }
        if (respuestaDelServidorEnJSon != null && respuestaDelServidorEnJSon.length() > 0) {
            try {
                Gson formatoJson = new Gson();
                Usuario resp = formatoJson.fromJson(respuestaDelServidorEnJSon, Usuario.class);
                if (resp == null || resp.getEmail() == null) {
                    // El servidor respondio {"mensaje": "..."}
                    return formatoJson.fromJson(respuestaDelServidorEnJSon, Mensaje.class);
                } else {
                    return resp;
                }
            } catch (Exception error) {
                Mensaje m = new Mensaje();
                m.setMensaje("Respuesta no valida del servidor: " + respuestaDelServidorEnJSon);
                return m;
            }
        } else {
            return null;
        }
    }

    private boolean verificarDatos() {
        String codigo = campoEmail.getText().toString();
        String password = campoPassword.getText().toString();
        if (codigo.trim().length() <= 0) {
            Toast.makeText(this, "Debe ingresar el Email", Toast.LENGTH_LONG).show();
            return false;
        } else if (password.trim().length() <= 0) {
            Toast.makeText(this, "Debe ingresar el Password", Toast.LENGTH_LONG).show();
            return false;
        } else {
            return true;
        }
    }

    class TareaLoginEnSegunPlano extends AsyncTask<String, String, String> {

        // propiedades
        String codigo;
        String password;

        @Override
        protected void onPreExecute() {
            barraDeprogreso = new ProgressDialog(PantallaInicio.this);
            barraDeprogreso.setMessage("Conectando...");
            barraDeprogreso.setIndeterminate(false);
            barraDeprogreso.setCancelable(false);
            barraDeprogreso.show();
        }

        @Override
        protected String doInBackground(String... parametros) {
            codigo = parametros[0];
            password = parametros[1];
            Object resp = iniciarSesion(codigo, password);
            if (resp != null) {
                if (resp instanceof Usuario) {
                    fulanito = (Usuario) resp;
                    return "OK";
                } else {
                    return ((Mensaje) resp).getMensaje();
                }
            } else {
                return "Acceso Negado, Error en el Servidor";
            }
        }

        @Override
        protected void onPostExecute(String resp) {
            barraDeprogreso.dismiss();
            if ("OK".equals(resp)) {
                Toast.makeText(PantallaInicio.this, "Bienvenido " + fulanito.getNombre(), Toast.LENGTH_SHORT).show();
                mostrarDatosDeUsuarioEnOtraActividad();
            } else {
                Toast.makeText(PantallaInicio.this, resp, Toast.LENGTH_LONG).show();
            }
        }
    }
}
