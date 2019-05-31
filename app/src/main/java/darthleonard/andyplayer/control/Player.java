package darthleonard.andyplayer.control;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Environment;

import darthleonard.andyplayer.MainActivity;
import darthleonard.andyplayer.MusicaService;
import darthleonard.andyplayer.filemanager.FileManager;

public class Player {
    public static List<Item> items;
    public static List<Item> listaRep;
    public static List<String> listasUsuario;
    public static List<Integer> itemsAnteriores;
    public static int OrdenListaID = 0;

    public MainActivity mainActivity;
    private MediaPlayer mp;
    private Item actual;
    private int Index = 0;
    private Timer timer;
    private boolean programado = false;
    private boolean aleatorio = false;
    private FileManager fileManager;


    private ProgressDialog pd = null;
    private Object data = null;

    public Player(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        fileManager = new FileManager(mainActivity.getApplicationContext(), pd);
        try {
            cargaArchivos();
        } catch (ClassNotFoundException | IOException e1) {
            /*Tools.mostrarMensaje(mainActivity.getApplicationContext(),
                    "¡ERROR!",
                    "Error al cargar la lista default (-1)",
                    "Aceptar",
                    Tools.MENSAJE_ERROR);*/
            e1.printStackTrace();
        }

        if(listaRep != null && listaRep.size()>0){
            actual = listaRep.get(Index);
            //mp = MediaPlayer.create(mainActivity.getApplicationContext(), Uri.parse(actual.getUrl()));
            mp = new MediaPlayer();
            try {
                mp.setDataSource(actual.getUrl());
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            mp.setOnPreparedListener(new OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    Play();
                }
            });

            mp.setOnCompletionListener(new OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    Siguiente();
                }
            });
        }
    }

    public void Play() {
        mp.start();
    }

    public void Play(int path) {
        mp.stop();
        mp.reset();

        Index = path;
        try{
            actual = listaRep.get(Index);
        }catch(IndexOutOfBoundsException ex){
            Index = 0;
            actual = listaRep.get(0);
        }

        try {
            mp.setDataSource(actual.getUrl());
            mp.prepare();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Notifica();

        //mp.start();
    }

    public void Pause() {
        if(mp.isPlaying())
            mp.pause();
    }

    public void Siguiente() {
        if(aleatorio) {
            itemsAnteriores.add(Index);
            if(itemsAnteriores.size()>=50)
                itemsAnteriores.remove(0);
            Index = (int)(Math.random()*listaRep.size()+0);
            Play(Index);
            return;
        }

        if(Index <= listaRep.size())
            Index++;
        else
            Index = 0;
        Play(Index);
    }

    public void Anterior() {
        if(aleatorio) {
            if(itemsAnteriores.size()>0) {
                Play(itemsAnteriores.get(itemsAnteriores.size()-1));
                itemsAnteriores.remove(itemsAnteriores.size()-1);
            }else
                mp.stop();
            return;
        }

        if(Index == 0)
            Index = listaRep.size()-1;
        else
            Index--;
        Play(Index);
    }

    public void Repetir() {
        if(mp.isLooping())
            mp.setLooping(false);
        else
            mp.setLooping(true);
    }

    public void Aleatorio() {
        if(isAleatorio())
            aleatorio = false;
        else
            aleatorio = true;
    }

    @SuppressWarnings("unchecked")
    public void ReproduceLista(String nombre) throws IOException, ClassNotFoundException {
        if(nombre.equals("todo"))
            listaRep = items;
        else {
            File lista = new File("mnt/ext_card/AndyPlayer/Listas/"+nombre);
            if(fileManager.getSourcesFile().exists()) {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(lista));
                listaRep = (List<Item>) ois.readObject();
                ois.close();
            }else
                Tools.mostrarMensaje(mainActivity,
                        "Alerta",
                        "no se reconoce el nombre de la lista:\n" +
                                "   \""+nombre+"\n",
                        "Aceptar",
                        Tools.MENSAJE_ALERTA);
        }
    }

    /**
     * Apaga el reproductor
     * @param tiempo
     * @return <strong>false</strong> si el timer ya existe
     */
    public boolean Apagar(int tiempo) {
        if(timer == null || timer.Inactivo()) {
            timer = new Timer(tiempo, mainActivity);
            timer.start();
            programado = true;
            return true;
        } else {
            return false;
        }
    }

    public void Notifica() {
        MusicaService.Aviso(mainActivity.getApplicationContext());
    }

    /**
     * Cancela el apagado
     * @return <strong>false</strong> si el timer era igual a null
     */
    public boolean CancelarApagado() {
        if(timer != null) {
            timer.Cancelar();
            timer = null;
            programado = false;
            return true;
        } else
            return false;
    }

    public void cargaArchivos() throws IOException, ClassNotFoundException {
        String estado = Environment.getExternalStorageState();
        String raiz = mainActivity.getExternalFilesDir(null).getAbsolutePath();

        if (estado.equals(Environment.MEDIA_MOUNTED)) {
            fileManager.setSourcesFile(new File(Environment.getExternalStorageDirectory(), "ArchivosRegistrados.lst"));
            if(fileManager.getSourcesFile().exists()) {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileManager.getSourcesFile()));
                items = (List<Item>) ois.readObject();
                listaRep = items;
                ois.close();
            }else {
                this.pd = ProgressDialog.show(mainActivity, "Espere unos segundos...", "Buscando archivos de musica", true, false);
                fileManager.execute("");
            }
        } else if (estado.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
            Tools.mostrarMensaje(mainActivity.getApplicationContext(),"Error en la tarjeta SD","La tarjeta se encuentra en estado de solo lectura","Aceptar",Tools.MENSAJE_ALERTA);
        } else {
            Tools.mostrarMensaje(mainActivity.getApplicationContext(),"Error en la tarjeta SD","No se puede acceder a la tarjeta SD","Aceptar",Tools.MENSAJE_ALERTA);
        }


        /**
         * Carga las listas de reproduccion creadas por el usuario
         */
        listasUsuario = new ArrayList<String>();
        /*File directorioListas = new File("/Listas/");
        File listas[] = directorioListas.listFiles();
        if(listas.length > 0) {
            for (int i = 0; i < listas.length; i++) {
                if(listas[i].isDirectory() || !listas[i].getAbsolutePath().endsWith(".lst"))
                    continue;
                if(listas[i].getAbsolutePath().equals(archivoTodo.getAbsolutePath()) || listasUsuario.contains(listas[i]))
                    continue;
                listasUsuario.add(listas[i].getName());
            }
        }*/

        itemsAnteriores = new ArrayList<Integer>();

    }

    public void cargaNuevaLista(String nombreLista) {
        listasUsuario.add(nombreLista);
    }

    public void eliminaLista(int index) {
        File lista = new File("mnt/ext_card/AndyPlayer/Listas/"+listasUsuario.get(index));
        listasUsuario.remove(index);
        lista.delete();
    }





    public void Terminar() {
        mp.release();
    }

    public Item getActual() {
        return actual;
    }

    public MediaPlayer getMP() {
        return mp;
    }

    public boolean isPlaying() {
        if(mp != null)
            return mp.isPlaying();
        return false;
    }

    public boolean isLooping() {
        return mp.isLooping();
    }

    public boolean isAleatorio() {
        return aleatorio;
    }

    public boolean isProgramado() {
        return programado;
    }

    public String getTiempoTimer() {
        if(timer != null)
            return timer.getStrTiempo();
        else
            return "00:00:00";
    }

    public String getTiempoFinalTimer() {
        if(timer != null) {
            int tiempo =  timer.getTimeTarget();
            int hrs, min;
            String strHrs="00", strMin="00", strSeg="00";
            if(tiempo >= 60) {
                hrs = (int)tiempo/60;
                if(hrs < 10)
                    strHrs = "0"+hrs;
                else
                    strHrs = ""+hrs;

                min = tiempo%60;
                if(min >= 10)
                    strMin = "0"+min;
                else
                    strMin = ""+min;

                return strHrs+":"+strMin+":"+strSeg;

            }else
                return "00:"+tiempo+":00";
        }else
            return "00:00:00";
    }
}
