import javax.swing.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Scanner;

public class Admin {
    public static void main(String[] args) throws IOException {


        SocketChannel channel = null;

        String server = "localhost";
        int port = 12345;

        try{
            channel = SocketChannel.open();
            channel.configureBlocking(false);
            channel.connect(new InetSocketAddress(server,port));

            System.out.println("Admin: łącze się z serwerem ... ");

            while (!channel.finishConnect()) {
                // ew. pokazywanie czasu łączenia (np. pasek postępu)
                // lub wykonywanie jakichś innych (krótkotrwałych) działań
            }
        }catch(UnknownHostException exc) {
            System.err.println("Uknown host " + server);
            // ...
        } catch(Exception exc) {
            exc.printStackTrace();
            // ...
        }

        System.out.println("\nAdmin: jestem połączony z serwerem ...");

        SocketChannel finalChannel = channel;
        SwingUtilities.invokeLater(() -> {
            try {
                new GUIAdmin(finalChannel);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });



        Charset charset  = Charset.forName("ISO-8859-2");
        int rozmiarBufora = 1024;
        ByteBuffer inBuf = ByteBuffer.allocateDirect(rozmiarBufora);
        CharBuffer cBuff = null;


        System.out.println("Admin: wysylam - Hi");
        channel.write(charset.encode("Hi\n"));

        while(true){
            inBuf.clear();
            int readBytes = channel.read(inBuf);

            if(readBytes == 0){
                continue;
            }
            else if (readBytes == -1){
                break;
            }
            else {
                inBuf.flip();

                cBuff = charset.decode(inBuf);

                String odSerwera = cBuff.toString();

                System.out.println("Admin: serwer właśnie odpisał ... " + odSerwera);
                cBuff.clear();
                break;
            }
        }

       }

       public static void request(String outMessage, SocketChannel channel) throws IOException {
           Charset charset  = Charset.forName("ISO-8859-2");

           int rozmiarBufora = 1024;
           ByteBuffer inBuf = ByteBuffer.allocateDirect(rozmiarBufora);
           CharBuffer cBuff = null;

           inBuf.clear();

           cBuff = CharBuffer.wrap(outMessage + "\n");

           ByteBuffer outBuf = charset.encode(cBuff);
           channel.write(outBuf);

           System.out.println("Admin: piszę " + outMessage);

           while(true){

               inBuf.clear();
               int readBytes = channel.read(inBuf);

               if(readBytes == 0){
                   continue;
               }
               else if (readBytes == -1){
                   break;
               }
               else {
                   inBuf.flip();

                   cBuff = charset.decode(inBuf);

                   String odSerwera = cBuff.toString();

                   System.out.println("Admin: serwer właśnie odpisał ... " + odSerwera);
                   cBuff.clear();
                   inBuf.clear();
               break;
               }
           }


       }
    public static String requestBack(String outMessage, SocketChannel channel) throws IOException {
        Charset charset  = Charset.forName("ISO-8859-2");

        int rozmiarBufora = 1024;
        ByteBuffer inBuf = ByteBuffer.allocateDirect(rozmiarBufora);
        CharBuffer cBuff = null;

        inBuf.clear();

        cBuff = CharBuffer.wrap(outMessage + "\n");

        ByteBuffer outBuf = charset.encode(cBuff);
        channel.write(outBuf);

        System.out.println("Admin: piszę " + outMessage);

        String answer = "blad";

        while(true){

            inBuf.clear();
            int readBytes = channel.read(inBuf);

            if(readBytes == 0){
                continue;
            }
            else if (readBytes == -1){
                break;
            }
            else {
                inBuf.flip();

                cBuff = charset.decode(inBuf);

                String odSerwera = cBuff.toString();

                System.out.println("Admin: serwer właśnie odpisał ... " + odSerwera);
                answer = odSerwera;
                cBuff.clear();
                inBuf.clear();
                break;
            }
        }
        return answer;
    }

}


