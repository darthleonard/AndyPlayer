package darthleonard.andyplayer;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import darthleonard.andyplayer.control.Player;

public class MusicaService extends Service {
    private static NotificationManager nm;
    public static final int ID_NOTIFICACION = 200591;
    public static boolean SUPERFLAG = true;
    public static Player player;

    @Override
    public void onCreate() {
        nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        if(player == null)
            player = MainActivity.player;
    }

    @Override
    /**
     *  intencion: Un objeto Intent que se indicó en la llamada startService(Intent).<br>
     *	flags: Información sobre como comienza la solicitud. Puede ser 0, START_FLAG_REDELIVERYo START_FLAG_RETRY.<br>
     *		Un valor distinto de 0 se utiliza para reiniciar un servicio tras detectar algún problema.<br>
     *  idArranque: Un entero único representando la solicitud de arranque específica. Usar este mismo estero en el<br>
     *  	método stopSelfResult(int idArranque).<br>
     *
     *  return:
     *  START_STICKY: Cuando sea posible el sistema tratará de recrear el servicio, se realizará una llamada a<br>
     *  	onStartCommand() pero con el parámetro intencion igual a null. Esto tiene sentido cuando el servicio<br>
     *  	puede arrancar sin información adicional, como por ejemplo, el servicio mostrado para la reproducción<br>
     *  	de música de fondo.
     *	START_NOT_STICKY: El sistema no tratará de volver a crear el servicio, por lo tanto el parámetro intencion<br>
     *	nunca podrá ser igual a null. Esto tiene sentido cuando el servicio no puede reanudarse una vez interrumpido.<br>
     *	START_REDELIVER_INTENT: El sistema tratará de volver a crear el servicio. El parámetro intencion será el que<br>
     *		se utilizó en la última llamada startService(Intent).
     *	START_STICKY_COMPATIBILITY: Versión compatible de START_STICKY, que no garantiza que onStartCommand() sea llamado<br>
     *		después de que el proceso sea matado.
     *
     */
    public int onStartCommand(Intent intencion, int flags, int idArranque) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        SUPERFLAG  = false;
        nm.cancel(ID_NOTIFICACION);
        //reproductor.stop();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intencion) {
        return null;
    }

    @SuppressWarnings("deprecation")
    public static void Aviso(Context context) {
        if(player == null)
            return;

        /*

        ************************ MARCA ERROR AL CONSTRUIR NOTIFICACION *****************************

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentTitle(player.getActual().getTitulo())
                .setContentText(player.getActual().getInterprete())
                //.setSmallIcon(R.drawable.ic_launcher)
                .setAutoCancel(true);

        PendingIntent intencionPendiente = PendingIntent.getActivity(
                context, 0, new Intent(context, MainActivity.class), 0);

        Notification notificacion = builder.build();

        notificacion.setLatestEventInfo(context, player.getActual().getTitulo(),
                player.getActual().getInterprete(), intencionPendiente);

        notificacion.tickerText = player.getActual().getTitulo() + " - " + player.getActual().getInterprete();
        nm.notify(ID_NOTIFICACION, notificacion);
        */
    }

    public static void OcultaAviso() {
        if(nm != null)
            nm.cancel(ID_NOTIFICACION);
    }
}
