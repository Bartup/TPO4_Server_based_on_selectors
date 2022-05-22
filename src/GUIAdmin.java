import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class GUIAdmin {

    public GUIAdmin(SocketChannel channel) throws IOException {

        JFrame jFrame = new JFrame("Admin");

        JPanel jPanel1 = new JPanel(new FlowLayout());

        jFrame.add(jPanel1);

        //TopicList
        JLabel topicsLabel = new JLabel();
        topicsLabel.setBorder(BorderFactory.createTitledBorder("Topics"));
        jPanel1.add(topicsLabel);
        topicsLabel.setPreferredSize(new Dimension(400,50));
        try {
            topicsLabel.setText(Admin.requestBack("GETTOPICS",channel));
        } catch (IOException ex) {
            ex.printStackTrace();
        }


        JTextField jTextField = new JTextField();
        jTextField.setBorder(BorderFactory.createTitledBorder("Enter topic"));
        JButton addButton = new JButton("Add");
        JButton removeButton = new JButton("Remove");

        jPanel1.add(jTextField);
        jPanel1.add(addButton);
        jPanel1.add(removeButton);

        jTextField.setPreferredSize(new Dimension(130,50));
        addButton.setPreferredSize(new Dimension(90,50));
        removeButton.setPreferredSize(new Dimension(90,50));

        JTextArea articleField = new JTextArea();
        articleField.setPreferredSize(new Dimension(450,300));
        articleField.setBorder(BorderFactory.createTitledBorder("Enter article"));
        jPanel1.add(articleField);

        JButton addArticle = new JButton("Add Article");
        addArticle.setPreferredSize(new Dimension(130,50));
        jPanel1.add(addArticle);



        jFrame.setPreferredSize(new Dimension(500,700));
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setVisible(true);
        jFrame.pack();

        addButton.addActionListener(e ->{
            String top = jTextField.getText().trim();
            String out = "ADD.TOPIC:" + top;
            try {
                Admin.request(out,channel);
                topicsLabel.setText(Admin.requestBack("GETTOPICS",channel));
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        });
        addArticle.addActionListener(e -> {
            String article = articleField.getText().replaceAll("\n","");
            String top = jTextField.getText();
            String out = "NEWARTICLE:" + top + ":" + article;

            try {
                Admin.request(out,channel);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

//        updateButton.addActionListener(s -> {
//            try {
//                topicsLabel.setText(Admin.requestBack("GETTOPICS",channel));
//            } catch (IOException ex) {
//                ex.printStackTrace();
//            }
//        });
        removeButton.addActionListener(e -> {
            String top = jTextField.getText().trim();
            String out = "REMOVETOPIC:" + top;
            try {
                Admin.request(out,channel);
                topicsLabel.setText(Admin.requestBack("GETTOPICS",channel));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }

}
