package darthleonard.andyplayer.filemanager;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import darthleonard.andyplayer.control.Item;
import darthleonard.andyplayer.control.Player;
import darthleonard.andyplayer.control.Tools;

import static darthleonard.andyplayer.control.Player.items;

public class FileManager extends AsyncTask {
    public static final String extenciones[] = {".mp3"};
    private Context context;
    private ProgressDialog p;
    private Player player;
    private File archivoTodo;

    public FileManager(Context context, ProgressDialog pd) {
        this.context = context;
        p = pd;
    }

    @Override
    protected Object doInBackground(Object[] params) {
        try {
            archivoTodo.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath());

        items = new ArrayList<>();
        recursivoCarga(items, f);
        Tools.orderByTitulo(items);
        Player.listaRep = items;
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(new FileOutputStream(archivoTodo)); // sometimes fails when file exist
            oos.writeObject(items);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Object();
    }

    protected void onPostExecute(Object result) {
        Toast.makeText(context,"Se encontraron " + items.size() + " archivos", Toast.LENGTH_LONG).show();
        p.cancel();
    }

    private void recursivoCarga(List<Item> item, File f) {
        if(f.isDirectory()){
            File[] file = f.listFiles();
            String path, name[];
            for (int i = 0; i < file.length; i++) {
                if(file[i].isDirectory())
                    recursivoCarga(item, file[i]);
                else{
                    path = file[i].getAbsolutePath();
                    name = path.split("/");
                    name = name[name.length-1].split(" - ");
                    if(!esSoportado(name[name.length-1]))
                        continue;

                    Item k;
                    if(name.length > 1)
                        k = new Item(name[name.length-1], name[0], path);
                    else
                        k = new Item(name[name.length-1], path);

                    item.add(k);
                    //nombre.add(name[name.length-1]);
                }
            }
        }
    }

    private boolean esSoportado(String name) {
        for (int i = 0; i < extenciones.length; i++) {
            if(name.endsWith(extenciones[i])) {
                name = name.substring(0, extenciones[i].length());
                return true;
            }
        }
        return false;
    }

    public File getSourcesFile() {
        return archivoTodo;
    }

    public void setSourcesFile(File file) {
        archivoTodo = file;
    }
}
