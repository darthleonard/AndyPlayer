package darthleonard.andyplayer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import darthleonard.andyplayer.control.Item;
import darthleonard.andyplayer.control.ItemAdapter;
import darthleonard.andyplayer.control.Player;
import darthleonard.andyplayer.control.Tools;

public class CrearListaActivity extends AppCompatActivity {
    private ListView listView;
    private EditText etNombreArchivo;
    private Button btnCrear;
    @SuppressWarnings("unused")
    private boolean LISTA_CREADA;

    private List<Item> lista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_lista);
        LISTA_CREADA = false;
        lista = new ArrayList<Item>();

        etNombreArchivo = (EditText) findViewById(R.id.etNombreLista);

        listView = (ListView) findViewById(R.id.listaVerTodosArchivos);
        listView.setAdapter(new ItemAdapter(this, Player.items));
        listView.setSelector(R.drawable.selector_item);

        btnCrear = (Button) findViewById(R.id.btnCrearAceptar);
        btnCrear.setEnabled(false);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(!btnCrear.isEnabled())
                    btnCrear.setEnabled(true);

                if(!Player.items.get(position).isSeleccionado()) {
                    Player.items.get(position).setSeleccionado(true);
                    lista.add(Player.items.get(position));
                }else {
                    Player.items.get(position).setSeleccionado(false);
                    lista.remove(Player.items.get(position));
                }

                if(lista.size() == 0)
                    btnCrear.setEnabled(false);
            }
        });
    }

    @Override
    protected void onDestroy() {
        RestauraEstados();
        super.onDestroy();
    }

    /**
     * Valida los datos necesarios para generar la lista y llama a <b>crearLista<b>
     * @param v
     */
    public void onClickCrear(View v) {
        if(lista.size() > 0) {
            String nombreArchivo = etNombreArchivo.getText().toString();
            if(nombreArchivo.length() > 0 && !nombreArchivo.contains(".") && !nombreArchivo.contains("/")) {
                switch (crearLista(nombreArchivo)){
                    case 0:
                        LISTA_CREADA = true;
                        RestauraEstados();
                        MainActivity.player.cargaNuevaLista(nombreArchivo+".lst");
                        finish();
                        break;
                    case 1:
                        Tools.mostrarMensaje(CrearListaActivity.this,
                                "Operacion no completada",
                                "El nombre \"" + nombreArchivo + "\" ya fue asignado",
                                "Aceptar",
                                Tools.MENSAJE_ALERTA);
                        break;
                    case 2:
                        Tools.mostrarMensaje(CrearListaActivity.this,
                                "¡ERROR!",
                                "Contacta al desarrollador e informale de un error " +
                                        "tipo \"FileNotFoundException\" al crear una nueva " +
                                        "lista de reproduccion.",
                                "cerrar dialogo",
                                Tools.MENSAJE_ERROR);
                        break;
                    case 3:
                        Tools.mostrarMensaje(CrearListaActivity.this,
                                "¡ERROR!",
                                "Contacta al desarrollador e informale de un error " +
                                        "tipo \"IOException\" al crear una nueva lista de " +
                                        "reproduccion.",
                                "cerrar dialogo",
                                Tools.MENSAJE_ERROR);
                        break;
                    default:
                        Tools.mostrarMensaje(CrearListaActivity.this,
                                "¡ERROR!",
                                "Contacta al desarrollador e informale de un error " +
                                        "tipo \"-1\" al crear una nueva lista de reproduccion.",
                                "cerrar dialogo",
                                Tools.MENSAJE_ERROR);
                }
            } else
                Tools.mostrarMensaje(CrearListaActivity.this,
                        "Operacion no completada",
                        "Nombre de archivo no valido",
                        "Aceptar",
                        Tools.MENSAJE_ALERTA);
        } else
            Tools.mostrarMensaje(CrearListaActivity.this,
                    "Operacion no completada",
                    "Debes seleccionar por lo meos una cancion",
                    "Aceptar",
                    Tools.MENSAJE_ALERTA);
    } // fin de metodo onClickCrear

    /**
     * Llama a RestaruaEstados y cierra el Activity
     * @param v
     */
    public void onClickCancelar(View v) {
        RestauraEstados();
        finish();
    }

    /**
     * Restaura los "estados de seleccion" de cada item
     */
    private void RestauraEstados() {
        if(lista.size() > 0) {
            for (int i = 0; i < Player.items.size(); i++) {
                if(Player.items.get(i).isSeleccionado()) {
                    Player.items.get(i).setSeleccionado(false);
                }
            }
            lista.clear();
        }
    } // fin de metodo RestauraEstados

    /**
     * Crea el archivo .lst de la nueva lista de reproduccion
     * @param nombreArchivo
     * @return
     */
    private int crearLista(String nombreArchivo) {
        //File archivo_lista = new File("mnt/ext_card/AndyPlayer/Listas/"+nombreArchivo+".lst");
        String raiz = getExternalFilesDir(null).getAbsolutePath();
        File archivo_lista = new File(raiz,nombreArchivo+".lst");
        if(archivo_lista.exists())
            return 1;

        ObjectOutputStream oos;
        try {
            oos = new ObjectOutputStream(new FileOutputStream(archivo_lista));
            oos.writeObject(lista);
            oos.close();
            return 0;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return 2;
        } catch (IOException e) {
            e.printStackTrace();
            return 3;
        }

    } // fin de metodo crearLista
}
