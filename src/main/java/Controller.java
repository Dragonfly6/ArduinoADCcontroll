import com.fazecast.jSerialComm.SerialPort;
import exception.VoltageOutOfBoundsException;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.PrintWriter;


/**
 * Created by Vlad on 12/09/2017.
 */
public class Controller {

    private SerialPort[] serialPorts;
    private SerialPort serialPort;
    private PrintWriter printWriter;

    private static StringBuilder sb;

    @FXML
    private Label connectionStatusLabel;

    @FXML
    private TextField voltage;

    @FXML
    private TextField duration;

    @FXML
    private TextField pause;

    @FXML
    private ChoiceBox<String> commPorts;

    static {
        sb = new StringBuilder();
        sb.append("1. Choose USB port from the expandable list.\n");
        sb.append("2. Click \"Connect\".\n");
        sb.append("3. If connection is successful, enter desired values into the textfields.\n");
        sb.append("   Voltage can only be between -15 and +15 volts.\n");
        sb.append("4. Click \"Send data\" to send the command to the microcontroller.\n");
        sb.append("5. Click \"Refresh\" if you need to update the list of available USB ports.\n");
    }

    @FXML
    private void handleConnect(){
        if (serialPort != null) serialPort.closePort();

        serialPort = SerialPort.getCommPort(commPorts.getSelectionModel().getSelectedItem());
        if (serialPort.openPort()){
            connectionStatusLabel.setText(ConnectionStatus.Connected + " to " + serialPort.getSystemPortName());
            printWriter = new PrintWriter(serialPort.getOutputStream());
            serialPort.setBaudRate(9600);
        } else connectionStatusLabel.setText("" + ConnectionStatus.Failed);
    }

    @FXML
    private void handleSend(){
        if (serialPort != null && serialPort.isOpen() && checkData()){
            String message = String.format("V%s D%s P%s\n", voltage.getText(), duration.getText(), pause.getText());
            System.out.println(message);
            printWriter.print(message);
            printWriter.flush();
        }
    }

    @FXML
    private void handleRefresh(){
        commPorts.getItems().setAll(getPortNames());
    }

    @FXML
    private void handleHelp(){
        showAlertWindow(Alert.AlertType.INFORMATION, "Help", sb.toString());
    }



    @FXML
    public void initialize(){
        commPorts.getItems().addAll(getPortNames());
        connectionStatusLabel.setText("" + ConnectionStatus.Not_Connected);
    }



    private String[] getPortNames() {
        serialPorts = SerialPort.getCommPorts();
        String[] serialPortsNames = new String[serialPorts.length];

        for (int i = 0; i < serialPorts.length; i++){
            serialPortsNames[i] = serialPorts[i].getSystemPortName();
        }
        return serialPortsNames;
    }

    private boolean checkData(){
        try {
            Float v = Float.parseFloat(voltage.getText());
            Float.parseFloat(duration.getText());
            Float.parseFloat(pause.getText());
            if (v < -15 || v > 15){
                throw new VoltageOutOfBoundsException();
            }
            return true;
        } catch (NumberFormatException e){
            showAlertWindow(Alert.AlertType.ERROR, "Wrong data format", "Please, verify data format in the input fields");
            return false;
        } catch (VoltageOutOfBoundsException e){
            showAlertWindow(Alert.AlertType.ERROR, "Voltage out of allowed bounds", "Voltage can be between -15 and 15 Volts");
            return false;
        }
    }

    private void showAlertWindow(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


}
