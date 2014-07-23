package event.planning.Boomset;

import java.util.Hashtable;
import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class VenueAdapter extends ArrayAdapter<Venues> {

	private LayoutInflater inflater;
	private Typeface typeface;
    private static Hashtable fontCache = new Hashtable();
	
	public VenueAdapter(Context context, int resourceID, List<Venues> values) {
    	super(context, resourceID, values);
    	inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	typeface = getTypeface(context, "roboto_light.ttf");
    }
	private class ViewHolder {
		public TextView venueName;
		public TextView venueAddress;
	}
	
	static Typeface getTypeface(Context context, String font) {
        Typeface typeface = (Typeface) fontCache.get(font);
        if (typeface == null) {
            typeface = Typeface.createFromAsset(context.getAssets(), font);
            fontCache.put(font, typeface);
        }
        return typeface;
    }
	
	 @Override
		public View getView(int position, View convertView, ViewGroup parent) {
		 	final ViewHolder holder;
			View row = convertView;
			
			if (convertView == null) {
				row = inflater.inflate(R.layout.venue_list_row, null);
				holder = new ViewHolder();
				holder.venueName = (TextView) row.findViewById(R.id.venueName);
				holder.venueAddress = (TextView) row.findViewById(R.id.venueAddress);
				row.setTag(holder);
				
			} else {
				holder = (ViewHolder) row.getTag();
			}
			
			holder.venueName.setText(getItem(position).getVenueName());
			holder.venueName.setTypeface(typeface);
			holder.venueAddress.setText(getItem(position).getVenueAddress());
			holder.venueAddress.setTypeface(typeface);
			return row;
		}

}
