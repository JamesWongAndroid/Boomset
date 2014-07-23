package event.planning.Boomset;

import java.util.ArrayList;
import java.util.List;

import event.planning.Boomset.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Filterable;
import android.widget.TextView;

public class GroupAdapter extends ArrayAdapter<String> implements Filterable {
	private LayoutInflater inflater;
	
	
    public GroupAdapter(Context context, int resourceID, List<String> values) {
    	super(context, resourceID, values);
    	inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
	public View getView(int position, View convertView, ViewGroup parent) {
    	
		View row;
		
		if (convertView == null) {
			row = inflater.inflate(R.layout.group_list_row, null);
		} else {
			row = convertView;
		}
		
		TextView groupTitle = (TextView) row.findViewById(R.id.groupNameText);
		groupTitle.setText(getItem(position));
		return row;
	}

}
