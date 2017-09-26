
/**
 * Created by Vlad on 13/09/2017.
 */
public enum ConnectionStatus {
    Not_Connected("Not connected"),
    Connected("Connected"),
    Failed ("Failed");

    private String message;

    ConnectionStatus(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return message;
    }
}
