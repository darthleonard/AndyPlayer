package darthleonard.andyplayer;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.Toast;

import java.io.IOException;

import darthleonard.andyplayer.control.ItemAdapter;
import darthleonard.andyplayer.control.Player;
import darthleonard.andyplayer.control.Tools;

public class OpcionesActivity extends AppCompatActivity {
    private ListView lvListaRepTodo, lvListaRepActual, lvListasRep;
    private Context thisContext;
    private RadioGroup rgOrdenLista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opciones);
        thisContext = this.getApplicationContext();

        initTab();

        lvListaRepTodo = (ListView) findViewById(R.id.listaVerTodo);
        lvListaRepTodo.setAdapter(new ItemAdapter(this, Player.items));
        lvListaRepActual = (ListView) findViewById(R.id.listaRepActual);
        lvListaRepActual.setAdapter(new ItemAdapter(this, Player.listaRep));
        muestraListas();

        rgOrdenLista = (RadioGroup) findViewById(R.id.rgOrdenLista);
        if(Player.OrdenListaID == 0)
            Player.OrdenListaID = rgOrdenLista.getCheckedRadioButtonId();
        else
            rgOrdenLista.check(Player.OrdenListaID);

        setListListeners();
    }

    @Override
    protected void onRestart() {
        muestraListas();
        super.onRestart();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        /*if (v.getId()==R.id.listasRep) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_listas_reproduccion, menu);
        }*/
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Toast.makeText(this,"onContextItemSelected",Toast.LENGTH_SHORT).show();
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int itemSeleccionado = info.position;
        switch(item.getItemId()) {
            /*case R.id.menu_editarLista:
                Toast.makeText(this, "No implementado aun", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_eliminarLista:
                MainActivity.player.eliminaLista(itemSeleccionado);
                muestraListas();
                return true;*/
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void muestraListas() {
        if(Player.listasUsuario.size() > 0) {
            lvListasRep = (ListView) findViewById(R.id.listasRep);
            lvListasRep.setAdapter(new ItemListAdapter(this, Player.listasUsuario));
            registerForContextMenu(lvListasRep);
        }
    }

    private void initTab() {
        Resources res = getResources();

        TabHost tabs=(TabHost)findViewById(android.R.id.tabhost);
        tabs.setup();

        TabHost.TabSpec spec=tabs.newTabSpec("tabRepActual");
        spec.setContent(R.id.tabRepActual);
        spec.setIndicator("Actual",
                res.getDrawable(android.R.drawable.ic_btn_speak_now));
        tabs.addTab(spec);

        spec=tabs.newTabSpec("tabRepTodo");
        spec.setContent(R.id.tabRepTodo);
        spec.setIndicator("Todo",
                res.getDrawable(android.R.drawable.ic_dialog_map));
        tabs.addTab(spec);

        spec=tabs.newTabSpec("tabListas");
        spec.setContent(R.id.tabListas);
        spec.setIndicator("Listas",
                res.getDrawable(android.R.drawable.ic_dialog_map));
        tabs.addTab(spec);

        tabs.setCurrentTab(0);
    }

    private void setListListeners() {
        lvListaRepTodo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
                try {
                    MainActivity.player.ReproduceLista("todo");
                } catch (ClassNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                MainActivity.player.Play(position);
                lvListaRepActual.removeAllViewsInLayout();
                lvListaRepActual.setVisibility(View.INVISIBLE);
                lvListaRepActual.setAdapter(new ItemAdapter(getApplicationContext(), Player.listaRep));
                lvListaRepActual.setVisibility(View.VISIBLE);
            }
        });

        lvListaRepTodo.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapter, View view, int position, long id) {
                Toast.makeText(thisContext, "Aun noimplementado", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        lvListaRepActual.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
                MainActivity.player.Play(position);
            }
        });

        if(lvListasRep != null) {
            lvListasRep.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
                    try {
                        MainActivity.player.ReproduceLista(Player.listasUsuario.get(position));
                        MainActivity.player.Play(0);
                        lvListaRepActual.setAdapter(new ItemAdapter(getApplicationContext(), Player.listaRep));
                        actualizaListaRepActual();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        rgOrdenLista.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                lvListaRepTodo.removeAllViewsInLayout();
                lvListaRepTodo.setVisibility(View.INVISIBLE);
                if(checkedId == R.id.rbOrdenTitulo)
                    Tools.orderByTitulo(Player.items);
                else if(checkedId == R.id.rbOrdenInterprete)
                    Tools.orderByInterprete(Player.items);
                lvListaRepTodo.setVisibility(View.VISIBLE);
                Player.OrdenListaID = checkedId;
            }
        });
    }

    public void actualizaListaRepActual() {
        lvListaRepActual.removeAllViewsInLayout();
        lvListaRepActual.setVisibility(View.INVISIBLE);
        lvListaRepActual.setVisibility(View.VISIBLE);
    }

    public void onClickCrear(View v){
        Intent i = new Intent(this, CrearListaActivity.class);
        startActivity(i);
    }
}
