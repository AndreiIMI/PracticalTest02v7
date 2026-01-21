package ro.pub.cs.systems.eim.practicaltest02v7;

public class AlarmInformation {
    private String minute;
    private String seconds;

    public AlarmInformation(String minute, String seconds) {
        this.minute = minute;
        this.seconds = seconds;
    }

    public String getMinute() { return minute; }
    public String getSeconds() { return seconds; }
}