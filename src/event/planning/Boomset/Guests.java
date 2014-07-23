package event.planning.Boomset;

public class Guests {

	private String guestName, guestTitle, currentGuestValue, totalGuestValue, notificationStatus, stampType, totalEventGuests, reservationID;
	public boolean isDeleted;
	
	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public String getReservationID() {
		return reservationID;
	}

	public void setReservationID(String reservationID) {
		this.reservationID = reservationID;
	}

	public String getTotalEventGuests() {
		return totalEventGuests;
	}

	public void setTotalEventGuests(String totalEventGuests) {
		this.totalEventGuests = totalEventGuests;
	}

	public String getGuestName() {
		return guestName;
	}

	public void setGuestName(String guestName) {
		this.guestName = guestName;
	}

	public String getGuestTitle() {
		return guestTitle;
	}

	public void setGuestTitle(String guestTitle) {
		this.guestTitle = guestTitle;
	}

	public String getCurrentGuestValue() {
		return currentGuestValue;
	}

	public void setCurrentGuestValue(String currentGuestValue) {
		this.currentGuestValue = currentGuestValue;
	}

	public String getTotalGuestValue() {
		return totalGuestValue;
	}

	public void setTotalGuestValue(String totalGuestValue) {
		this.totalGuestValue = totalGuestValue;
	}

	public String getNotificationStatus() {
		return notificationStatus;
	}

	public void setNotificationStatus(String notificationStatus) {
		this.notificationStatus = notificationStatus;
	}

	public String getStampType() {
		return stampType;
	}

	public void setStampType(String stampType) {
		this.stampType = stampType;
	}
	
	
	
	public String toString() {
		return this.getGuestName() == null ? "" :this.getGuestName();
	}
	
	
}
