import javax.imageio.IIOException;
import javax.swing.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Scanner;

public class Client {
    public static int userId;
    public static void main(String[] args) throws IOException {
        userId = Integer.parseInt(args[0]);

        SocketChannel channel = null;

        String server = "localhost";
        int port = 12345;

        try{
            channel = SocketChannel.open();
            channel.configureBlocking(false);
            channel.connect(new InetSocketAddress(server,port));

            System.out.println("Klient: łącze się z serwerem ... ");

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

        System.out.println("\nKlient: jestem połączony z serwerem ...");

        SocketChannel finalChannel = channel;
        SwingUtilities.invokeLater(() -> {
            new GUIClient(finalChannel,userId);
        });

        Charset charset  = Charset.forName("ISO-8859-2");
        int rozmiarBufora = 1024;
        ByteBuffer inBuf = ByteBuffer.allocateDirect(rozmiarBufora);
        CharBuffer cBuff = null;

        System.out.println("Klient: wysylam - Hi");
        channel.write(charset.encode("Hi:" + userId+"\n"));

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

        cBuff = CharBuffer.wrap(outMessage + ":" +userId+ "\n");

        ByteBuffer outBuf = charset.encode(cBuff);
        channel.write(outBuf);

        System.out.println("Klient: piszę " + outMessage + ":" +userId);

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

                System.out.println("Klient: serwer właśnie odpisał ... " + odSerwera);
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

        cBuff = CharBuffer.wrap(outMessage + ":" + userId+ "\n");

        ByteBuffer outBuf = charset.encode(cBuff);
        channel.write(outBuf);

        System.out.println("Klient: piszę " + outMessage + ":" + userId);

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
