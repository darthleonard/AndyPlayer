package darthleonard.andyplayer.control;

import darthleonard.andyplayer.MainActivity;

public class Timer extends Thread {
    private boolean Cancelar = true;
    private int tiempoFinal = 0, tiempoActual;
    private String strTiempo = "00:00:00";
    private MainActivity mainActivity;

    public Timer(int TimeTarget, MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        this.tiempoFinal = TimeTarget;
    }

    @Override
    public void run() {
        tiempoActual = 0;
        Cancelar = false;

        Integer horas = 0, minutos = 0 , segundos = 0, milesimas = 0;
        String hr = "00", min = "00", seg = "00";

        while((tiempoActual < tiempoFinal) && !Cancelar){
            segundos++;
            //tiempoActual++;
            if(segundos == 60) {
                segundos = 0;
                minutos += 1;
                tiempoActual++;
                if(minutos == 60) {
                    minutos = 0;
                    horas++;
                }
                //TimeCurrent++;
            }

            if(horas < 10)
                hr = "0" + milesimas;
            else
                hr = horas.toString();
            if(minutos < 10)
                min = "0" + minutos;
            else
                min = minutos.toString();
            if(segundos < 10)
                seg = "0" + segundos;
            else
                seg = segundos.toString();

            strTiempo = hr+":"+min+":"+seg;

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if(!Cancelar)
            mainActivity.Cerrar(0);
    }

    public void Cancelar() {
        Cancelar = true;
    }

    public boolean Inactivo() {
        return Cancelar;
    }

    public int getTimeTarget() {
        return tiempoFinal;
    }

    public void setTimeTarget(int timeTarget) {
        tiempoFinal = timeTarget;
    }

    public int getTimeCurrent() {
        return tiempoActual;
    }

    public String getStrTiempo() {
        return strTiempo;
    }
}
