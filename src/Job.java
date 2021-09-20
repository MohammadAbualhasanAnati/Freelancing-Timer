
public class Job {
	
	private String jobName;
	private int secondsTotal;
	private int timerHours;
	private int timerMinutes;
	private int timerSeconds;
	private boolean isTimerOn;
	
	
	public String getJobName() {
		return jobName;
	}
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	
	public int getSecondsTotal() {
		return secondsTotal;
	}
	public void setSecondsTotal(int secondsTotal) {
		this.secondsTotal = secondsTotal;
		calcTimeInHoursAndMinutes();
	}
	
	
	private void calcTimeInHoursAndMinutes() {
		double hoursD=(double) secondsTotal/60/60;
		timerHours=(int)hoursD;
		double minutesD=(double) (hoursD-timerHours)*60;
		timerMinutes=(int) minutesD;
		double secondsD=(double) (minutesD-timerMinutes)*60;
		setTimerSeconds((int)secondsD);
	}
	
	public int getTimerHours() {
		return timerHours;
	}
	public void setTimerHours(int timerHours) {
		this.timerHours = timerHours;
	}
	public int getTimerMinutes() {
		return timerMinutes;
	}
	public void setTimerMinutes(int timerMinutes) {
		this.timerMinutes = timerMinutes;
	}
	public int getTimerSeconds() {
		return timerSeconds;
	}
	public void setTimerSeconds(int timerSeconds) {
		this.timerSeconds = timerSeconds;
	}
	
	public String timerToString() {
		return String.valueOf(timerHours)+"h : "+String.valueOf(timerMinutes)+"m : "+String.valueOf(timerSeconds)+"s";
	}
	
	public boolean isTimerOn() {
		return isTimerOn;
	}
	public void setTimerOn(boolean isTimerOn) {
		this.isTimerOn = isTimerOn;
	}
	
	
	
	
	
	

}
