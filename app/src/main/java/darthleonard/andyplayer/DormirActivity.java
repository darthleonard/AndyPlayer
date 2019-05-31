package darthleonard.andyplayer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import darthleonard.andyplayer.control.Player;
import darthleonard.andyplayer.control.Tools;

public class DormirActivity extends AppCompatActivity {
    private ToggleButton btn_TimerControl;
    private EditText timer_Minutos;
    private TextView txtTimer, txtTimeTarget;
    private Player player;
    private Thread tTimer;
    private boolean threadFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dormir);
        btn_TimerControl = (ToggleButton) findViewById(R.id.btnTimerControl);
        timer_Minutos = (EditText) findViewById(R.id.timer_Minutos);
        txtTimer = (TextView) findViewById(R.id.txtTimer);
        txtTimeTarget = (TextView) findViewById(R.id.tvTiempoFinal);
        txtTimeTarget.setText("Programado para "+MainActivity.player.getTiempoFinalTimer());

        player = MainActivity.player;

        boolean flagActivo = player.isProgramado();
        btn_TimerControl.setChecked(flagActivo);
        timer_Minutos.setEnabled(!flagActivo);
        txtTimer.setText(player.getTiempoTimer());

        if(flagActivo)
            comenzar();
    }

    public void onClick_btnTimerControl(View v) {
        if(btn_TimerControl.isChecked()){
            int tiempo = Integer.parseInt("0"+timer_Minutos.getText().toString());

            if(tiempo > 0) {
                player.Apagar(tiempo);
                txtTimeTarget.setText(MainActivity.player.getTiempoFinalTimer());
                timer_Minutos.setEnabled(false);
                comenzar();
            }else{
                Tools.mostrarMensaje(this, "Error", "El tiempo introducido no es valido", "Aceptar", Tools.MENSAJE_ERROR);
                timer_Minutos.setText("0");
                btn_TimerControl.setChecked(false);
            }
        }else{
            player.CancelarApagado();
            timer_Minutos.setEnabled(true);
            terminar();
        }

    }

    private void comenzar() {
        threadFlag = false;
        tTimer = new Thread(new Runnable() {
            @Override
            public void run() {
                while(!threadFlag){
                    try {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                txtTimer.setText(player.getTiempoTimer());
                            }
                        });
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        tTimer.start();
    }

    private void terminar() {
        threadFlag = true;
        tTimer = null;
    }
}
