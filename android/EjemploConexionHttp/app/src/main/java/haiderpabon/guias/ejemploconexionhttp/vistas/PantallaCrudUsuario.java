// PantallaCrudUsuario.java - Haider Pabon Mejia
package haiderpabon.guias.ejemploconexionhttp.vistas;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import org.apache.commons.httpclient.NameValuePair;

import java.util.ArrayList;
import java.util.List;

import haiderpabon.guias.ejemploconexionhttp.R;
import haiderpabon.guias.ejemploconexionhttp.controladores.ConexionHttpPostServer;
import haiderpabon.guias.ejemploconexionhttp.datos.Mensaje;
import haiderpabon.guias.ejemploconexionhttp.datos.Usuario;

public class PantallaCrudUsuario extends AppCompatActivity {

    private EditText campoCodigo;
    private EditText campoPassword;
    private EditText campoNombre;
    private Button botonModificar;
    private Button botonCancelar;
    private TextView titulo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pantalla_crud_usuario);
        titulo = (TextView) findViewById(R.id.textView4);
        campoCodigo = (EditText) findViewById(R.id.campoCodigo2);
        campoPassword = (EditText) findViewById(R.id.campoPassword2);
        campoNombre = (EditText) findViewById(R.id.campoNombre);
        botonModificar = (Button) findViewById(R.id.botonModificar);
        botonCancelar = (Button) findViewById(R.id.botonCancelar2);

        botonCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent intento = this.getIntent();
        Usuario juansito = null;
        if (intento.getExtras() != null && intento.getExtras().getSerializable("sesion") != null) {
            juansito = (Usuario) intento.getExtras().getSerializable("sesion");
        } else if (PantallaInicio.fulanito != null) {
            juansito = PantallaInicio.fulanito;
        }

        if (juansito != null) {
            // MODO EDITAR: el usuario ya inicio sesion
            titulo.setText("EDITAR MIS DATOS");
            botonModificar.setText("Modificar");
            campoNombre.setText(juansito.getNombre());
            campoCodigo.setText(juansito.getEmail());
            campoPassword.setText(juansito.getPassword());
            campoCodigo.setEnabled(false); // el email es la llave primaria, no se cambia
        } else {
            // MODO AGREGAR: registro de un usuario nuevo
            titulo.setText("REGISTRAR USUARIO");
            botonModificar.setText("Guardar");
        }

        botonModificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardar(null);
            }
        });
    }

    public void guardar(View v) {
        if (campoCodigo.getText().toString().trim().isEmpty()
                || campoPassword.getText().toString().trim().isEmpty()
                || campoNombre.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Debe llenar todos los campos", Toast.LENGTH_LONG).show();
            return;
        }
        TareaGuardar tarea = new TareaGuardar();
        tarea.execute(
                campoCodigo.getText().toString().trim(),
                campoPassword.getText().toString(),
                campoNombre.getText().toString().trim());
    }

    // Se ejecuta en segundo plano
    public String guardarUsuario(String email, String clave, String nombre) {
        String url = ConexionHttpPostServer.direccionDelServidor;

        NameValuePair parametroAccion;
        if (PantallaInicio.fulanito == null) {
            parametroAccion = new NameValuePair("accion", "Agregar");
        } else {
            parametroAccion = new NameValuePair("accion", "editar");
        }

        List<NameValuePair> parametros = new ArrayList<NameValuePair>();
        parametros.add(parametroAccion);
        parametros.add(new NameValuePair("email", email));
        parametros.add(new NameValuePair("psw", clave));
        parametros.add(new NameValuePair("nombre", nombre));
        ConexionHttpPostServer conexionHttp = new ConexionHttpPostServer();
        try {
            String jsonRespuesta = conexionHttp.conexionConElServidor(parametros, url);
            if (jsonRespuesta != null) {
                Gson json = new Gson();
                Mensaje m = json.fromJson(jsonRespuesta, Mensaje.class);
                return m.getMensaje();
            } else {
                return "ERROR";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    class TareaGuardar extends AsyncTask<String, String, String> {
        ProgressDialog barraDeprogreso;
        String codigo, password, nombre;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            barraDeprogreso = new ProgressDialog(PantallaCrudUsuario.this);
            barraDeprogreso.setMessage("Conectando...");
            barraDeprogreso.setIndeterminate(false);
            barraDeprogreso.setCancelable(false);
            barraDeprogreso.show();
        }

        @Override
        protected String doInBackground(String... datos) {
            codigo = datos[0];
            password = datos[1];
            nombre = datos[2];
            return guardarUsuario(codigo, password, nombre);
        }

        @Override
        protected void onPostExecute(String resultado) {
            barraDeprogreso.dismiss();
            if ("OK".equalsIgnoreCase(resultado)) {
                Toast.makeText(PantallaCrudUsuario.this, "Usuario Guardado con Exito", Toast.LENGTH_LONG).show();
                if (PantallaInicio.fulanito != null) {
                    // actualizamos los datos de la sesion
                    PantallaInicio.fulanito = new Usuario(codigo, password, nombre);
                }
                finish(); // volvemos a la pantalla anterior (login o listado)
            } else {
                Toast.makeText(PantallaCrudUsuario.this, "ERROR: " + resultado, Toast.LENGTH_LONG).show();
            }
        }
    }
}
