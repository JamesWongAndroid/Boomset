package event.planning.Boomset;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.LayoutInflater.Filter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

public class EventAdapter extends ArrayAdapter<Events> implements Filterable {
	private LayoutInflater inflater;
	private ImageLoader imageLoader;
	private List<Events> eventList;
	private String eventID;
	private String url;
	DisplayImageOptions options;
//	ImageDownloader imageDownloader;
	
	private class ViewHolder {
		public TextView eventTitle;
		public ImageView eventIcon;
		public TextView eventStartDate;
		public TextView eventVenue;
		public TextView eventGroup;
	}
		
	public EventAdapter(Context context, int resourceID, List<Events> values) {
		super(context, resourceID, values);
		values = eventList;
		
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//		imageDownloader = new ImageDownloader();
		
		options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.ipadicon)
		.showImageForEmptyUri(R.drawable.ipadicon)
		.showImageOnFail(R.drawable.ipadicon)
		.cacheInMemory()
		.cacheOnDisc()
		.displayer(new RoundedBitmapDisplayer(10))
		.build();
		
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration.createDefault(context));
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		final ViewHolder holder;
		
		if (convertView == null) {
			view = inflater.inflate(R.layout.event_list_row, null);
			holder = new ViewHolder();
			holder.eventTitle = (TextView) view.findViewById(R.id.eventNameText);
			holder.eventIcon = (ImageView) view.findViewById(R.id.eventPicture);
			holder.eventStartDate = (TextView) view.findViewById(R.id.startDateText);
			holder.eventGroup = (TextView) view.findViewById(R.id.groupText);
			holder.eventVenue = (TextView) view.findViewById(R.id.venueText);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		
		
		
	//	ImageView eventIcon = (ImageView) view.findViewById(R.id.eventPicture);
		eventID = getItem(position).getEventID();
		url = "https://s3.amazonaws.com/eventimage.boomset.com/eventimage_" + eventID +"_thumb.jpg";
	//	url = "https://s3.amazonaws.com/eventimage.rsvpme.in/eventimage_" + eventID +"_thumb.jpg";
//		imageDownloader.download(url, eventIcon);
		imageLoader.displayImage(url, holder.eventIcon, options);
		
	//	TextView eventTitle = (TextView) view.findViewById(R.id.eventNameText);
		holder.eventTitle.setText(getItem(position).getEventName());
		
	//	TextView eventVenue = (TextView) view.findViewById(R.id.venueText);
		holder.eventVenue.setText(getItem(position).getVenueName());
		
	//	TextView eventStartDate = (TextView) view.findViewById(R.id.startDateText);
		holder.eventStartDate.setText(getItem(position).getStartDate());
		
		//TextView eventGroup = (TextView) view.findViewById(R.id.groupText);
		holder.eventGroup.setText(getItem(position).getGroupName());
		
		return view;
	}
	
	
	
}
