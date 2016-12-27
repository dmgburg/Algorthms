import java.util.HashMap;
import java.util.Map;

class Metric {
    public static boolean debug = false;
    private Map<String, Long> startByName = new HashMap<>();

    public void start(String name) {
        if (debug) {
            startByName.put(name, System.currentTimeMillis());
        }
    }

    public void log(String string){
        if (debug) {
            System.out.println(string);
        }
    }

    public void finish(String name) {
        if (debug) {
            long finished = System.currentTimeMillis();
            Long startedAt = startByName.remove(name);
            if (startedAt == null) {
                throw new IllegalStateException(name + " not started");
            }
            System.out.println(name + " took " + (finished - startedAt) + " millis");
        }
    }
}