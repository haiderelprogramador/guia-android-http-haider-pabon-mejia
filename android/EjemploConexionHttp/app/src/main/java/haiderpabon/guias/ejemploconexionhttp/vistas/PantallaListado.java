// PantallaListado.java - Haider Pabon Mejia
package haiderpabon.guias.ejemploconexionhttp.vistas;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.httpclient.NameValuePair;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import haiderpabon.guias.ejemploconexionhttp.R;
import haiderpabon.guias.ejemploconexionhttp.controladores.ConexionHttpPostServer;
import haiderpabon.guias.ejemploconexionhttp.datos.Mensaje;
import haiderpabon.guias.ejemploconexionhttp.datos.Usuario;

public class PantallaListado extends AppCompatActivity {

    private ProgressDialog barraProgreso;
    private ConexionHttpPostServer conexionServidor;
    private ListView listaUsuariosView;
    private Button btnGuardar;
    private List<Usuario> listaUsuarios;
    private ArrayAdapter<String> items;
    private String mensajeError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pantalla_listado);
        listaUsuariosView = (ListView) findViewById(R.id.listaUsuarios);
        btnGuardar = (Button) findViewById(R.id.btnGuardar);
        items = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        listaUsuariosView.setAdapter(items);
        conexionServidor = new ConexionHttpPostServer();

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intento = new Intent(PantallaListado.this, PantallaCrudUsuario.class);
                startActivity(intento);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recargamos la lista cada vez que se muestra la pantalla
        // (por ejemplo, al volver despues de editar tus datos)
        new TareaListarTodo().execute();
    }

    class TareaListarTodo extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            barraProgreso = new ProgressDialog(PantallaListado.this);
            barraProgreso.setMessage("Conectando...");
            barraProgreso.setIndeterminate(false);
            barraProgreso.setCancelable(false);
            barraProgreso.show();
        }

        @Override
        protected String doInBackground(String... params) {
            return procesarRespuestaPeticion() ? "OK" : "NO";
        }

        @Override
        protected void onPostExecute(String resultado) {
            barraProgreso.dismiss();
            if (resultado.equals("OK")) {
                mostrarUsuariosEnLista();
            } else {
                Toast.makeText(PantallaListado.this,
                        mensajeError != null ? mensajeError : "Error en la Tarea",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private void mostrarUsuariosEnLista() {
        items.clear();
        int i = 0;
        for (Usuario alguien : listaUsuarios) {
            i++;
            System.out.println("No. " + i + " " + alguien);
            items.add(i + ". " + alguien.getEmail() + " - " + alguien.getNombre());
        }
        items.notifyDataSetChanged();
    }

    // Se ejecuta en segundo plano: NO se puede usar Toast aqui dentro
    private boolean procesarRespuestaPeticion() {
        mensajeError = null;
        ArrayList<NameValuePair> listaParametros = new ArrayList<NameValuePair>();
        listaParametros.add(new NameValuePair("accion", "listar"));
        try {
            String resultadoDelServidor = conexionServidor.conexionConElServidor(
                    listaParametros, ConexionHttpPostServer.direccionDelServidor);
            if (resultadoDelServidor != null && resultadoDelServidor.length() > 0) {
                System.out.println("JSON: " + resultadoDelServidor);
                Gson json = new Gson();
                if (resultadoDelServidor.trim().startsWith("[")) {
                    // Llego una lista de usuarios
                    Type lista = new TypeToken<List<Usuario>>() {
                    }.getType();
                    listaUsuarios = json.fromJson(resultadoDelServidor, lista);
                    return true;
                } else {
                    // Llego un mensaje, por ejemplo {"mensaje":"No hay Usuarios"}
                    Mensaje m = json.fromJson(resultadoDelServidor, Mensaje.class);
                    mensajeError = m.getMensaje();
                    return false;
                }
            } else {
                return false;
            }
        } catch (Exception error) {
            error.printStackTrace();
            mensajeError = error.getMessage();
            return false;
        }
    }
}
