import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.nio.channels.SocketChannel;

import static java.awt.Color.*;

public class GUIClient {



    public GUIClient(SocketChannel channel, int userId) {
        JFrame jFrame = new JFrame("Client " + userId);

        JPanel jPanel1 = new JPanel(new FlowLayout());

        jFrame.add(jPanel1);

        //TopicList
        JLabel topicsLabel = new JLabel();
        topicsLabel.setBorder(BorderFactory.createTitledBorder("Avialable Topics"));
        jPanel1.add(topicsLabel);
        topicsLabel.setPreferredSize(new Dimension(400,50));
        try {
            topicsLabel.setText(Client.requestBack("GETTOPICS",channel));
        } catch (IOException e) {
            e.printStackTrace();
        }

        JLabel subedTopicsLabel = new JLabel();
        subedTopicsLabel.setBorder(BorderFactory.createTitledBorder("Subscribed Topics"));
        jPanel1.add(subedTopicsLabel);
        subedTopicsLabel.setPreferredSize(new Dimension(400,50));



        //Subscribe
        JButton subButton = new JButton("Subscribe");
        JTextField subTField = new JTextField();

        jPanel1.add(subButton);
        jPanel1.add(subTField);

        subTField.setBorder(BorderFactory.createTitledBorder("Enter Topic"));
        subTField.setPreferredSize(new Dimension(150,50));

        subButton.setPreferredSize(new Dimension(150,50));

        //Unsubscribe

        JButton unsubButton = new JButton("Unubscribe");

        jPanel1.add(unsubButton);

        unsubButton.setPreferredSize(new Dimension(150,50));

        //Topics + articles

        JLabel jLabel = new JLabel("<html><b>PRESS UPDATE ARTICLES TO SHOW ARTICLES</b></html>");

        jPanel1.add(jLabel);
        jLabel.setPreferredSize(new Dimension(450,500));
        jLabel.setBorder(BorderFactory.createTitledBorder("Articles"));


        JButton updateButton = new JButton("Update Topics");
        jPanel1.add(updateButton);
        updateButton.setPreferredSize(new Dimension(130,50));

        JButton showarticlesButton = new JButton("Update Articles");
        jPanel1.add(showarticlesButton);
        showarticlesButton.setPreferredSize(new Dimension(130,50));

        jFrame.setPreferredSize(new Dimension(500,800));
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setVisible(true);
        jFrame.pack();

        subButton.addActionListener(e ->{
            String top = subTField.getText().trim();
            String out = "SUB:" + top;
            try {
                Client.request(out,channel);
                subedTopicsLabel.setText(Client.requestBack("GETSUUB",channel));
                jLabel.setText("");
                String tmp = Client.requestBack("GETARTICLES",channel);
                tmp = "<html>" + tmp + "<html>";
                jLabel.setText(tmp);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        unsubButton.addActionListener(e -> {
            String top = subTField.getText().trim();
            String out = "UNSUB:" + top;
            try {
                Client.request(out,channel);
                subedTopicsLabel.setText(Client.requestBack("GETSUUB",channel));
                jLabel.setText("");
                String tmp = Client.requestBack("GETARTICLES",channel);
                tmp = "<html>" + tmp + "<html>";
                jLabel.setText(tmp);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        updateButton.addActionListener(s -> {
            try {
                topicsLabel.setText(Client.requestBack("GETTOPICS",channel));
                subedTopicsLabel.setText(Client.requestBack("GETSUUB",channel));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        showarticlesButton.addActionListener(e -> {
            try {
                jLabel.setText("");
                String tmp = Client.requestBack("GETARTICLES",channel);
                tmp = "<html>" + tmp + "<html>";
                jLabel.setText(tmp);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

    }


}
