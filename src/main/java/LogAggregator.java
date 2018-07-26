import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.function.Function;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LogAggregator {
  ArrayList<LogEntry> logs = new ArrayList<>();
  Gson gson = new Gson();

  public LogAggregator() {
  }

  public LogAggregator(LogAggregator logAgg1, LogAggregator logAgg2) {
    logs.addAll(logAgg1.logs);
    logs.addAll(logAgg2.logs);
  }

  public LogAggregator(String jsonString) {
    ArrayList<LogEntry> logEntries = gson.fromJson(jsonString, new TypeToken<List<LogEntry>>(){}.getType());
    logs.addAll(logEntries);
  }

  public LogAggregator(byte[] bytes) {
    this(new String(bytes));
  }

  public LogAggregator add(String log) {
    LogEntry logEntry = gson.fromJson(log, LogEntry.class);
    logs.add(logEntry);
    return this;
  }

  public String asJsonString() {
    return gson.toJson(logs);
  }

  public byte[] asByteArray() {
    return asJsonString().getBytes(StandardCharsets.UTF_8);
  }

  public String groupedLimitedBy(Integer limitSize) {
    Map<String, Long> counted = logs.stream()
      .map(logEntry -> logEntry.asJsonString())
      .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

    ArrayList<LogEntry> listSubset = new ArrayList<>();

    counted.entrySet().stream()
      .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
      .limit(limitSize)
      .forEachOrdered(e -> {
        LogEntry logEntry = LogEntry.fromJson(e.getKey(), e.getValue());
        listSubset.add(logEntry);
      });

    return gson.toJson(listSubset);
  }

}
