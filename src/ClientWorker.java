import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientWorker {
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private JFrame frame;
    private JLabel labelMoney;
    private JTextField textPay;
    private JTextField textIP;
    private Worker worker;
    private JButton buttonGet;
    private JPanel panelGet;
    private JPanel panelStart;
    private JButton buttonConnect;


    public static void main(String[] args) {
        new ClientWorker().start();
    }

    private void start() {
        setUpWorker();
        buildGui();

    }

    private void buildGui() {
        frame = new JFrame("Получи выручку");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        panelStart = new JPanel();
        JLabel labelStart = new JLabel("Введите ip адрес сервера:");
        textIP = new JTextField(20);
        textIP.setMaximumSize(new Dimension(20,20));
        buttonConnect = new JButton("Подключиться к серверу");
        buttonConnect.addActionListener(new ButtonConnectListener());
        panelStart.add(labelStart);
        panelStart.add(textIP);
        frame.getContentPane().add(BorderLayout.CENTER,panelStart);
        frame.getContentPane().add(BorderLayout.SOUTH,buttonConnect);

        panelGet = new JPanel();
        buttonGet = new JButton("Получить выручку");
        buttonGet.addActionListener(new ButtonGetListener());
        textPay = new JTextField(20);
        textPay.setMaximumSize(new Dimension(20, 20));
        JLabel labelDefault = new JLabel("Введите первые 4 буквы фамили:");
        labelMoney = new JLabel();

        panelGet.add(labelDefault);
        panelGet.add(textPay);
        panelGet.add(labelMoney);


        frame.setSize(350, 300);
        frame.setVisible(true);
    }

    private void setUpWorker() {
        worker = new Worker();
    }

    class ButtonConnectListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            Thread remote = new Thread(new RemoteReader());
            remote.start();
            frame.remove(panelStart);
            frame.remove(buttonConnect);
            frame.getContentPane().add(BorderLayout.CENTER, panelGet);
            frame.getContentPane().add(BorderLayout.SOUTH, buttonGet);
            frame.repaint();
        }
    }

    class ButtonGetListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            try {
                worker.setName(textPay.getText());
                out.writeObject(worker);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }
    public class RemoteReader implements Runnable {
        Object obj = null;

        RemoteReader() {
            try {
                Socket sock = new Socket(textIP.getText(), 4242);
                out = new ObjectOutputStream(sock.getOutputStream());
                in = new ObjectInputStream(sock.getInputStream());
                JOptionPane.showMessageDialog(frame,new String[] {"Соединение с сервером установлено!"},"Успех!",JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JDialog dialog = new JDialog(frame,"Ошибка",true);
                dialog.add(new JLabel("Ошибка подключения к серверу, программа будет закрыта!"));
                dialog.setSize(500,100);
                dialog.setVisible(true);
                System.exit(1);
            }
        }

        public void run() {

            try {
                while ((obj = in.readObject())!= null) {

                    worker = (Worker) obj;
                    textPay.setText(worker.getName());
                    labelMoney.setText(worker.getSells().toString());
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

}
