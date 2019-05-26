import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class GUI {
    private JFrame frame;
    private JLabel labelMoney;
    private JTextField textPay;
    private JTextField textIP;
    private JTextField textPort;
    private JButton buttonGet;
    private JPanel panelGet;
    private JPanel panelStart;
    private JButton buttonConnect;
    private ClientWorker clientWorker;
    private Thread remoteReader;
    private Integer port;

    GUI () {
        clientWorker = ClientWorker.getClientWorker();
        buildGui();
    }
    private void buildGui() {
        frame = new JFrame("Получи выручку");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        panelStart = new JPanel();
        JLabel labelStart = new JLabel("Введите ip адрес сервера:");
        textIP = new JTextField(20);
        textIP.setMaximumSize(new Dimension(20,20));
        buttonConnect = new JButton("Подключиться к серверу");
        buttonConnect.addActionListener(new ButtonConnectListener());
        JLabel labelPort = new JLabel("Введите порт");
        textPort = new JTextField(20);
        panelStart.add(labelStart);
        panelStart.add(textIP);
        panelStart.add(labelPort);
        panelStart.add(textPort);
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


        frame.setSize(300, 200);
        frame.setVisible(true);
    }

    class ButtonConnectListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (textIP.getText().length()!=0 && textPort.getText().length()!=0)
            {
                remoteReader = clientWorker.newRemoteReader();
                remoteReader.start();
                frame.remove(panelStart);
                frame.remove(buttonConnect);
                frame.repaint();
                frame.getContentPane().add(BorderLayout.CENTER, panelGet);
                frame.getContentPane().add(BorderLayout.SOUTH, buttonGet);
                frame.setVisible(false);
                frame.setVisible(true);
                frame.repaint();
            }  else JOptionPane.showMessageDialog(frame.getContentPane(),"Заполните все поля","Ошибка",JOptionPane.ERROR_MESSAGE);
        }
    }

    class ButtonGetListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (textPay.getText().length()>=4) {
                try {
                    clientWorker.getWorker().setName(textPay.getText());
                    clientWorker.getOut().writeObject(clientWorker.getWorker());
                } catch (Exception e1) {
                    JOptionPane.showMessageDialog(frame.getContentPane(), e1.toString(), "Ошибка", JOptionPane.INFORMATION_MESSAGE);
                }
            } else JOptionPane.showMessageDialog(frame.getContentPane(),"Нужно ввести хотя бы 4 первые буквы!","Ошибка",JOptionPane.INFORMATION_MESSAGE);
        }
    }

    JTextField getTextIP() {
        return textIP;
    }

    JFrame getFrame() {
        return frame;
    }

    JTextField getTextPay() {
        return textPay;
    }

    JLabel getLabelMoney() {
        return labelMoney;
    }

    JTextField getTextPort() {
        return textPort;
    }
}
