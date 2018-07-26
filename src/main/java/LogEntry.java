import com.google.gson.Gson;

public class LogEntry {
  public int code;
  public String message;
  public Long count;

  public LogEntry(int code, String message) {
    this.code = code;
    this.message = message;
  }

  public static LogEntry fromJson(String jsonString, Long count) {
    LogEntry logEntry = new Gson().fromJson(jsonString, LogEntry.class);
    logEntry.count = count;
    return logEntry;
  }

  public String asJsonString() {
    return new Gson().toJson(this);
  }
}
