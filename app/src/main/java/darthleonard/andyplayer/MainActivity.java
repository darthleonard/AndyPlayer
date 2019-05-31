package darthleonard.andyplayer;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import darthleonard.andyplayer.control.Player;
import darthleonard.andyplayer.control.Tools;

public class MainActivity extends AppCompatActivity {
    private final int PERMISSIONS_REQUEST_READ = 1;
    private final int PERMISSIONS_REQUEST_WRITE = 2;

    NotificationCompat.Builder builder;
    NotificationManager nm;

    private ImageButton btnPlay, btnLoop, btnRandom;
    private SeekBar seekBarRep;
    private TextView txtSeekCur, txtSeekFin, txtMediaTitulo, txtMediaInterprete;
    public static Player player;

    Thread tTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        askForPermission();

        btnPlay = (ImageButton) findViewById(R.id.imgButtonPlay);
        btnLoop = (ImageButton) findViewById(R.id.imgButtonLoop);
        btnRandom = (ImageButton) findViewById(R.id.imgButtonRandom);
        seekBarRep = (SeekBar) findViewById(R.id.seekBarRep);
        txtSeekCur = (TextView) findViewById(R.id.txtSeekCur);
        txtSeekFin = (TextView) findViewById(R.id.txtSeekFin);
        txtMediaTitulo = (TextView) findViewById(R.id.txtMediaTitulo);
        txtMediaInterprete = (TextView) findViewById(R.id.txtMediaInterprete);
        agregaListeners();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(MusicaService.player == null)
            player = new Player(this);
        else {
            player = MusicaService.player;
            Bitmap bm;
            if(player.isPlaying())
                bm = BitmapFactory.decodeResource(getResources(), R.drawable.play);
            else
                bm = BitmapFactory.decodeResource(getResources(), R.drawable.pause);
            btnPlay.setImageBitmap(bm);

            if(player.isAleatorio())
                bm = BitmapFactory.decodeResource(getResources(), R.drawable.random_on);
            else
                bm = BitmapFactory.decodeResource(getResources(), R.drawable.random_off);
            btnRandom.setImageBitmap(bm);

        }
        AnimaSeekBar();
        nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        startService(new Intent(this, MusicaService.class));
    }

    private void askForPermission() {
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_READ);
            }
        }
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_WRITE);
            }
        }
    }

    /**
     * Agrega los listeners de los componentes que lo necesiten
     */
    protected void agregaListeners() {
        seekBarRep.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(player.isPlaying() && fromUser)
                    player.getMP().seekTo(progress);
            }
        });
    }

    /**
     * Reproducir/Pausar
     * @param v
     */
    public void onClickPlay(View v) {
        Bitmap bm;
        if(player.isPlaying()){
            bm = BitmapFactory.decodeResource(getResources(), R.drawable.play);
            player.Pause();
        }else{
            bm = BitmapFactory.decodeResource(getResources(), R.drawable.pause);
            player.Play();
        }

        btnPlay.setImageBitmap(bm);
    }

    /**
     * Siguiente
     * @param v
     */
    public void onClickSiguiente(View v) {
        if(!player.isPlaying())
            btnPlay.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.pause));
        player.Siguiente();
    }

    /**
     *
     * @param v
     */
    public void onClickAnterior(View v) {
        if(!player.isPlaying())
            btnPlay.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.pause));
        player.Anterior();
    }


    public void onClickAleatorio(View v) {
        Bitmap bm;
        if(player.isAleatorio())
            bm = BitmapFactory.decodeResource(getResources(), R.drawable.random_off);
        else
            bm = BitmapFactory.decodeResource(getResources(), R.drawable.random_on);
        player.Aleatorio();
        btnRandom.setImageBitmap(bm);
    }

    /**
     * Repeat
     * @param v
     */
    public void onClickRepetir(View v) {
        Bitmap bm;
        if(player.isLooping())
            bm = BitmapFactory.decodeResource(getResources(), R.drawable.loop_off);
        else
            bm = BitmapFactory.decodeResource(getResources(), R.drawable.loop_on);
        btnLoop.setImageBitmap(bm);

        player.Repetir();
    }

    /**
     * Lanza Activity Opciones
     * @param v
     */
    public void onClickOpciones(View v) {
        Intent i = new Intent(this, OpcionesActivity.class);
        startActivity(i);
    }

    /**
     * Lanza Activity de control de Apagado
     * @param v
     */
    public void onClickTimer(View v) {
        Intent i = new Intent(this, DormirActivity.class);
        startActivity(i);
    }

    /**
     * Crea el hilo que actualiza la SeekBar
     */
    public void AnimaSeekBar() {
        tTimer = new Thread(new Runnable() {
            @Override
            public void run() {
                while(MusicaService.SUPERFLAG){
                    try {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(player.isPlaying()){
                                    seekBarRep.setMax(player.getMP().getDuration());
                                    txtSeekCur.setText(Tools.MilisToHuman(player.getMP().getCurrentPosition()));
                                    seekBarRep.setProgress(player.getMP().getCurrentPosition());
                                    txtSeekFin.setText(Tools.MilisToHuman(player.getMP().getDuration()));

                                    txtMediaTitulo.setText(player.getActual().getTitulo());
                                    txtMediaInterprete.setText(player.getActual().getInterprete());
                                }
                            }
                        });
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        tTimer.start();
    }

    public void Cerrar(int status) {
        player.Terminar();
        stopService(new Intent(this, MusicaService.class));

        finish();
        System.exit(status);
    }

    public void clickLayout(View v) {
        Toast.makeText(this, "click en layout", Toast.LENGTH_LONG).show();
    }
}
