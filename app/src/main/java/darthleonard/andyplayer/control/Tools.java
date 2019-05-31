package darthleonard.andyplayer.control;

import android.app.AlertDialog;
import android.content.Context;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import darthleonard.andyplayer.R;

public class Tools {
    public static final int MENSAJE_CORRECTO = 0;
    public static final int MENSAJE_ALERTA = 1;
    public static final int MENSAJE_ERROR = 2;

    public static String MilisToHuman(int milis) {
        long seg, min;
        if(milis < 100)
            return "00:00";
        else {
            seg = (int)milis/1000;
            min = (int)milis/60000;
            if(seg >= 60){
                seg -= 60*min;
            }
            if(seg<10)
                return min+":0"+seg;
            return min+":"+seg;
        }
    }

    public static List<Item> orderByTitulo(List<Item> item) {
        List<Item> newList = new ArrayList<Item>();
        Collections.sort(item, new Comparator<Item>() {
            @Override
            public int compare(Item  item1, Item  item2){
                return  item1.getTitulo().toLowerCase().compareTo(item2.getTitulo().toLowerCase());
            }
        });
        return newList;
    }

    public static List<Item> orderByInterprete(List<Item> item) {
        List<Item> newList = new ArrayList<Item>();
        Collections.sort(item, new Comparator<Item>() {
            @Override
            public int compare(Item  item1, Item  item2){
                return  item1.getInterprete().toLowerCase().compareTo(item2.getInterprete().toLowerCase());
            }
        });
        return newList;
    }

    public static void mostrarMensaje(Context context, String titulo, String mensaje, String txtBoton, int tipo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(titulo);
        builder.setMessage(mensaje);
        builder.setPositiveButton(txtBoton,null);
        switch (tipo){
            case MENSAJE_CORRECTO:
                builder.setIcon(R.drawable.ic_correcto);
                break;
            case MENSAJE_ALERTA:
                builder.setIcon(R.drawable.ic_alerta);
                break;
            case MENSAJE_ERROR:
                builder.setIcon(R.drawable.ic_error);
                break;
        }
        builder.create();
        builder.show();
    }
}
