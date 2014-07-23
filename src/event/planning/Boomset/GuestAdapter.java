package event.planning.Boomset;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class GuestAdapter extends ArrayAdapter<Guests> implements Filterable {

	private List<Guests> guestList;
	private Context adapterContext;
	private LayoutInflater inflater;
	private String stampString, notificationString;
	private static final String DATEFORMAT = "yyyy-MM-dd HH:mm:ss";
	private static final int INVISIBLE = 4;
	private static final int GONE = 8;
	private static final int VISIBLE = 0;
	private SharedPreferences preferences;
	private class ViewHolder {
		public TextView guestName;
		public TextView guestTitle;
		public ImageView stampIcon;
		public TextView currentGuestValue;
		public TextView totalGuestValue;
		public ImageView notificationIcon;
		public Button addButton;
		public Button subtractButton;
		public Button deleteButton;
		public Button reactivateButton;
		public Button notifyButton;
	}
	
	public GuestAdapter(Context context, int resourceID, List<Guests> values) {
		super(context, resourceID, values);
		values = guestList;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		adapterContext = context;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		final ViewHolder holder;
		final int positionInt = position;
		int increase;
		if (convertView == null) {
			view = inflater.inflate(R.layout.guest_list_row, null);
			holder = new ViewHolder();
			holder.guestName = (TextView) view.findViewById(R.id.guestNameText);
			holder.guestTitle = (TextView) view.findViewById(R.id.guestDescriptionText);
			holder.stampIcon = (ImageView) view.findViewById(R.id.guestStatusImage);
			holder.notificationIcon = (ImageView) view.findViewById(R.id.notificationImage);
			holder.currentGuestValue = (TextView) view.findViewById(R.id.currentGuestValueText);
			holder.totalGuestValue = (TextView) view.findViewById(R.id.totalGuestValueText);
			holder.addButton = (Button) view.findViewById(R.id.addButton);
			holder.subtractButton = (Button) view.findViewById(R.id.subtractButton);
			holder.deleteButton = (Button) view.findViewById(R.id.deleteButton);
			holder.reactivateButton = (Button) view.findViewById(R.id.reactiviateButton);
			holder.notifyButton = (Button) view.findViewById(R.id.notifyButton);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		
		holder.guestName.setText(getItem(position).getGuestName());
		
		if (getItem(position).isDeleted()) {
			holder.guestName.setPaintFlags(holder.guestName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
			holder.addButton.setVisibility(INVISIBLE);
			holder.subtractButton.setVisibility(INVISIBLE);
			holder.deleteButton.setVisibility(GONE);
			holder.reactivateButton.setVisibility(VISIBLE);
		} else {
			holder.guestName.setPaintFlags(holder.guestName.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
			holder.addButton.setVisibility(VISIBLE);
			holder.subtractButton.setVisibility(VISIBLE);
			holder.deleteButton.setVisibility(VISIBLE);
			holder.reactivateButton.setVisibility(GONE);
		}
		
		holder.guestTitle.setText(getItem(position).getGuestTitle());
		holder.currentGuestValue.setText(getItem(position).getCurrentGuestValue());
		holder.totalGuestValue.setText(getItem(position).getTotalGuestValue());
		holder.addButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				holder.addButton.setEnabled(false);
				ReservationAdapter reservationAdapter = new ReservationAdapter(adapterContext);
				reservationAdapter.open();
				String reservationID = getItem(positionInt).getReservationID();
				int increase = Integer.parseInt(getItem(positionInt).getCurrentGuestValue());
				if (increase < Integer.parseInt(getItem(positionInt).getTotalGuestValue())) {
					increase++;
					getItem(positionInt).setCurrentGuestValue(Integer.toString(increase));
					SimpleDateFormat dateFormat = new SimpleDateFormat(DATEFORMAT);
					dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
					String gmtDate = dateFormat.format(new Date());
					String gmtDateNoSpace = gmtDate.replace(" ", "").replace(":", "").replace("-", "");
					preferences = PreferenceManager.getDefaultSharedPreferences(adapterContext);
					String userID = preferences.getString("userID", "");
					reservationAdapter.createOperation("-1", userID, "1", gmtDateNoSpace, "0", reservationID, "1");
					reservationAdapter.close();
					
				}
				new CountDownTimer(1000, 1000) {
					
					@Override
					public void onTick(long millisUntilFinished) {		
					}
					
					@Override
					public void onFinish() {
						holder.addButton.setEnabled(true);
					}
				}.start();
				holder.currentGuestValue.setText(Integer.toString(increase));
			}
		});
		
		holder.subtractButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				holder.subtractButton.setEnabled(false);
				int decrease = Integer.parseInt(getItem(positionInt).getCurrentGuestValue());
				if (decrease > 0) {
					decrease--;
					getItem(positionInt).setCurrentGuestValue(Integer.toString(decrease));
					ReservationAdapter reservationAdapter = new ReservationAdapter(adapterContext);
					reservationAdapter.open();
					String reservationID = getItem(positionInt).getReservationID();
					SimpleDateFormat dateFormat = new SimpleDateFormat(DATEFORMAT);
					dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
					String gmtDate = dateFormat.format(new Date());
					String gmtDateNoSpace = gmtDate.replace(" ", "").replace(":", "").replace("-", "");
					preferences = PreferenceManager.getDefaultSharedPreferences(adapterContext);
					String userID = preferences.getString("userID", "");
					reservationAdapter.createOperation("-1", userID, "-1", gmtDateNoSpace, "0", reservationID, "1");
					reservationAdapter.close();
				}
					new CountDownTimer(1000, 1000) {
					
					@Override
					public void onTick(long millisUntilFinished) {		
					}
					
					@Override
					public void onFinish() {
						holder.subtractButton.setEnabled(true);
					}
				}.start();
				holder.currentGuestValue.setText(Integer.toString(decrease));
			}
		});
		
		holder.deleteButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (!getItem(positionInt).isDeleted) {
					ReservationAdapter reservationAdapter = new ReservationAdapter(adapterContext);
					reservationAdapter.open();
					String reservationID = getItem(positionInt).getReservationID();
					reservationAdapter.updateReservationStatus(reservationID, "2");
					holder.guestName.setPaintFlags(holder.guestName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
					holder.addButton.setVisibility(INVISIBLE);
					holder.subtractButton.setVisibility(INVISIBLE);
					holder.deleteButton.setVisibility(GONE);
					holder.reactivateButton.setVisibility(VISIBLE);
				}
				
			}
		});
		
		holder.reactivateButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ReservationAdapter reservationAdapter = new ReservationAdapter(adapterContext);
				reservationAdapter.open();
				String reservationID = getItem(positionInt).getReservationID();
				reservationAdapter.updateReservationStatus(reservationID, "1");
				holder.guestName.setPaintFlags(holder.guestName.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
				holder.addButton.setVisibility(VISIBLE);
				holder.subtractButton.setVisibility(VISIBLE);
				holder.deleteButton.setVisibility(VISIBLE);
				holder.reactivateButton.setVisibility(GONE);
			}
		});
		
		holder.notifyButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});
		
		stampString = getItem(position).getStampType();
		if (stampString != null) {
			
		if (stampString.equals("cash")) {
			holder.stampIcon.setImageResource(R.drawable.cashstamp);
		} else if (stampString.equals("free")) {
			holder.stampIcon.setImageResource(R.drawable.freestamp);
		} else if (stampString.equals("maybe")) {
			holder.stampIcon.setImageResource(R.drawable.maybestamp);
		} else if (stampString.equals("rsvp")) {
			holder.stampIcon.setImageResource(R.drawable.rsvpstamp);
		} else if (stampString.equals("unpaid")) {
			holder.stampIcon.setImageResource(R.drawable.unpaidstamp);
		} else if (stampString.equals("paid")) {
			holder.stampIcon.setImageResource(R.drawable.paidstamp);
		} else if (stampString.equals("going")) {
			holder.stampIcon.setImageResource(R.drawable.goingstamp);
		} else if (stampString.equals("comp")) {
			holder.stampIcon.setImageResource(R.drawable.compstamp);
		} else {
			holder.stampIcon.setVisibility(4);
		}
		} else {
			holder.stampIcon.setVisibility(4);
		}
		
		notificationString = getItem(position).getNotificationStatus();
		if (notificationString.equals("star")) {
			holder.notificationIcon.setImageResource(R.drawable.notificationstar);
		} else if (notificationString.equals("bubble")) {
			holder.notificationIcon.setImageResource(R.drawable.bubblebutton);
		} else {
			holder.notificationIcon.setVisibility(4);
		}
		
		return view;
	}

}
