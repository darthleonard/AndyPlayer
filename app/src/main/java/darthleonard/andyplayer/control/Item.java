package darthleonard.andyplayer.control;

import java.io.Serializable;

public class Item implements Serializable {
    private static final long serialVersionUID = 2898457105147819575L;
    public String Titulo;
    public String Interprete;
    public String url;
    private boolean seleccionado = false;

    public Item(String titulo, String url) {
        this(titulo, "Desconocido", url);
    }

    public Item(String titulo, String interprete, String url) {
        this.Titulo = titulo;
        this.Interprete = interprete;
        this.url = url;
    }

    public String getTitulo() {
        return Titulo;
    }

    public void setTitulo(String titulo) {
        this.Titulo = titulo;
    }

    public String getInterprete() {
        return Interprete;
    }

    public void setInterprete(String interprete) {
        Interprete = interprete;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isSeleccionado() {
        return seleccionado;
    }

    public void setSeleccionado(boolean seleccionado) {
        this.seleccionado = seleccionado;
    }
}
