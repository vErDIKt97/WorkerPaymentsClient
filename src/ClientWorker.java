import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Properties;

public class ClientWorker {
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Worker worker;
    private GUI gui;
    private static ClientWorker clientWorker;
    private Thread remote;
    private Properties prop = new Properties();

    public static void main(String[] args) {
        clientWorker = new ClientWorker();
        clientWorker.start();
    }

    private void start() {
        setUpWorker();
        gui = new GUI();
    }

    static ClientWorker getClientWorker() {
        return clientWorker;
    }

    private void setUpWorker() {
        worker = new Worker();
    }

    public class RemoteReader implements Runnable {
        Object obj = null;

        RemoteReader() {
            while (true) {
                try {
                    Socket sock = new Socket(gui.getTextIP().getText(), Integer.parseInt(gui.getTextPort().getText()));
                    out = new ObjectOutputStream(sock.getOutputStream());
                    in = new ObjectInputStream(sock.getInputStream());
                    JOptionPane.showMessageDialog(gui.getFrame(), new String[]{"Соединение с сервером установлено!"}, "Успех!", JOptionPane.INFORMATION_MESSAGE);
                    break;
                } catch (IOException e) {
                  int choise =  JOptionPane.showConfirmDialog(gui.getFrame().getContentPane(), "Сервер недоступен! \"Ok\" что бы переподключиться.", " Ошибка", JOptionPane.YES_NO_OPTION);
                  if (choise == 1) System.exit(0);
                }
            }
        }

        public void run() {

            try {
                while ((obj = in.readObject())!= null) {

                    worker = (Worker) obj;
                    gui.getTextPay().setText(worker.getName());
                    gui.getLabelMoney().setText(worker.getSells().toString()+" руб");
                }
            } catch (IOException | ClassNotFoundException e) {
                JOptionPane.showMessageDialog(gui.getFrame().getContentPane(),e.toString(),"Ошибка",JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    Worker getWorker() {
        return worker;
    }

    ObjectOutputStream getOut() {
        return out;
    }

     Thread newRemoteReader() {
        remote = new Thread(new RemoteReader());
        return remote;
    }

    public Properties getProp() {
        return prop;
    }
}
