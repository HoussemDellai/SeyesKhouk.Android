package net.houssemdellai.myapplication2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Houssem Dellai on 9/13/2014.
 */
public class PodcastsBaseAdapter extends ArrayAdapter<XmlParser.Podcast>{

    private List<XmlParser.Podcast> _podcastsCollection;
    private Context _context;

    public PodcastsBaseAdapter(List<XmlParser.Podcast> podcastsCollection, Context context)
    {
        super(context, R.layout.simple_list_item, podcastsCollection);

        _context = context;
        _podcastsCollection = podcastsCollection;
    }

    @Override
    public int getCount() {
        return this._podcastsCollection.size();
    }

    @Override
    public XmlParser.Podcast getItem(int i) {
        return this._podcastsCollection.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        View v = view; // re-use an existing view, if one is supplied

        if (v == null) // otherwise create a new one
        {
            v = LayoutInflater.from(_context).inflate(R.layout.simple_list_item, null);
        }

        // set view properties to reflect data for the given row
        TextView textViewPosition = (TextView) v.findViewById(R.id.textViewPosition);
        textViewPosition.setText((i + 1) + "");

        TextView textViewTitle = (TextView) v.findViewById(R.id.textViewTitle);
        textViewTitle.setText(_podcastsCollection.get(i).description);

        return v;
    }

}