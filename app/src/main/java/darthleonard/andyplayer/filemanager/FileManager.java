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
    public static final String FILE_EXTENSIONS[] = { ".mp3" };
    private Context context;
    private ProgressDialog progressDialog;
    private File sourcesFile;

    public FileManager(Context context, ProgressDialog progressDialog) {
        this.context = context;
        this.progressDialog = progressDialog;
    }

    @Override
    protected Object doInBackground(Object[] params) {
        try {
            sourcesFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
        items = new ArrayList<>();
        recursiveLoad(items, file);
        Tools.orderByTitulo(items);
        Player.listaRep = items;
        ObjectOutputStream oos;
        try {
            oos = new ObjectOutputStream(new FileOutputStream(sourcesFile)); // sometimes fails when file exist
            oos.writeObject(items);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Object();
    }

    protected void onPostExecute(Object result) {
        Toast.makeText(context, "Se encontraron " + items.size() + " archivos", Toast.LENGTH_LONG).show();
        progressDialog.cancel();
    }

    public File getSourcesFile() {
        return sourcesFile;
    }

    public void setSourcesFile(File file) {
        sourcesFile = file;
    }

    private void recursiveLoad(List<Item> items, File f) {
        if (!f.isDirectory()) {
            return;
        }
        File[] file = f.listFiles();
        String path, name[];
        for (int i = 0; i < file.length; i++) {
            if (file[i].isDirectory())
                recursiveLoad(items, file[i]);
            else {
                path = file[i].getAbsolutePath();
                name = path.split("/");
                name = name[name.length - 1].split(" - ");
                if (!isSupported(name[name.length - 1]))
                    continue;

                Item item;
                if (name.length > 1)
                    item = new Item(name[name.length - 1], name[0], path);
                else
                    item = new Item(name[name.length - 1], path);

                items.add(item);
                //nombre.add(name[name.length-1]);
            }
        }
    }

    private boolean isSupported(String name) {
        for (int i = 0; i < FILE_EXTENSIONS.length; i++) {
            if (name.endsWith(FILE_EXTENSIONS[i])) {
                name = name.substring(0, FILE_EXTENSIONS[i].length());
                return true;
            }
        }
        return false;
    }
}
