package darthleonard.andyplayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class ItemListAdapter extends BaseAdapter {
    private Context context;
    private List<String> nombreLista;

    public ItemListAdapter(Context context, List<String> nombreLista) {
        this.context = context;
        this.nombreLista = nombreLista;
    }

    @Override
    public int getCount() {
        return this.nombreLista.size();
    }

    @Override
    public Object getItem(int position) {
        return this.nombreLista.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.list_item_lista, parent, false);
        }

        TextView titulo = (TextView) rowView.findViewById(R.id.txtItemNombreLista);
        titulo.setText(this.nombreLista.get(position));
        return rowView;
    }
}
