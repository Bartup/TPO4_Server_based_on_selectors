import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.sql.SQLOutput;
import java.util.*;

public class Server {

    public static List<User> userList = new ArrayList<>();

    public static List<Topic> topicList = new ArrayList<>();
    public static void main(String[] args) throws IOException{
        topicList.add(new Topic("Football"));
        topicList.add(new Topic("Books"));
        for (Topic topic : topicList){
            if (topic.name.equals("Football")){
                topic.addArticle("Pilka nozna to piekny sport");
            }if (topic.name.equals("Books")){
                topic.addArticle("Nowe ksiazki w zasiegu reki");
            }if (topic.name.equals("Books")){
                topic.addArticle("Kazda ksiazka minus 50%");
            }
        }
        new Server();
    }

    Server() throws IOException{

        String host = "localhost";
        int port = 12345;
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.socket().bind(new InetSocketAddress(host,port));

        serverChannel.configureBlocking(false);

        Selector selector = Selector.open();

        serverChannel.register(selector , SelectionKey.OP_ACCEPT);

        System.out.println("Serwer: czekam... ");

        while(true){

            selector.select();
            Set<SelectionKey> keys = selector.selectedKeys();

            Iterator<SelectionKey> iter = keys.iterator();

            while(iter.hasNext()){

                SelectionKey key = iter.next();
                iter.remove();

                if(key.isAcceptable()){

                    System.out.println("Serwer: ktoś się połączył i akceptuję go... ");

                    SocketChannel cc = serverChannel.accept();
                    cc.configureBlocking(false);

                    cc.register(selector,SelectionKey.OP_READ | SelectionKey.OP_WRITE);

                    continue;
                }
            if (key.isReadable()) {
                SocketChannel cc = (SocketChannel) key.channel();

                serviceRequest(cc);

                continue;

            }
            if(key.isWritable()) {

                continue;
            }
            }
        }
    }
    private static Charset charset  = Charset.forName("ISO-8859-2");
    private static final int BSIZE = 1024;

    // Bufor bajtowy - do niego są wczytywane dane z kanału
    private ByteBuffer bbuf = ByteBuffer.allocate(BSIZE);

    // Tu będzie zlecenie do pezetworzenia
    private StringBuffer reqString = new StringBuffer();

    private void serviceRequest(SocketChannel sc) {
        if (!sc.isOpen()) return; // jeżeli kanał zamknięty

        System.out.print("Serwer: czytam komunikat od klienta ... ");
        // Odczytanie zlecenia
        reqString.setLength(0);
        bbuf.clear();

        try {
            readLoop:                    // Czytanie jest nieblokujące
            while (true) {               // kontynujemy je dopóki
                int n = sc.read(bbuf);   // nie natrafimy na koniec wiersza
                if (n > 0) {
                    bbuf.flip();
                    CharBuffer cbuf = charset.decode(bbuf);
                    while(cbuf.hasRemaining()) {
                        char c = cbuf.get();
                        //System.out.println(c);
                        if (c == '\r' || c == '\n') break readLoop;
                        else {
                            //System.out.println(c);
                            reqString.append(c);
                        }
                    }
                }
            }

            String cmd = reqString.toString();
            System.out.println(reqString);


            if(cmd.contains("ADD.TOPIC:")){
                String tmp = cmd.toString();
                tmp = tmp.replace("ADD.TOPIC:","");
                boolean flag = true;
                for (Topic t : topicList){
                    if (t.name.equals(tmp)){
                        flag = false;
                    }
                }
                if(flag){
                    topicList.add(new Topic(tmp));
                    System.out.println("Serwer: Admin dodał temat : " + tmp);
                    sc.write(charset.encode(CharBuffer.wrap("Dodano temat : " + tmp)));
                }else{
                    System.out.println("Serwer: Temat juz istnieje : " + tmp);
                    sc.write(charset.encode(CharBuffer.wrap("Temat juz istnieje : " + tmp)));
                }
            }
            else if(cmd.contains("NEWARTICLE:")){
                String tmp = cmd.toString();
                tmp = tmp.replace("NEWARTICLE:","");
                String[] arr = tmp.split(":");
                String top = arr[0];
                String art = arr[1];

                for (Topic t : topicList){
                    System.out.println(t);
                    if(t.name.equals(top)){
                        t.addArticle(art);
                        System.out.println(t.articlesToString());
                    }
                }
                System.out.println("Serwer: Admin dodał artykuł do tematu : " + top);
                sc.write(charset.encode(CharBuffer.wrap("Dodano artykul do tematu : " + top)));
            }

            else if(cmd.contains("REMOVETOPIC:")) {
                String tmp = cmd.toString();
                System.out.println("TMP: " + tmp);
                tmp = tmp.replace("REMOVETOPIC:","");
                String top = tmp;
                System.out.println("TOP: " + top);

                for (Iterator<User> iter = userList.iterator(); iter.hasNext();){
                    User u = iter.next();
                    for (Iterator<Topic> iterT = u.subscriptionList.iterator(); iterT.hasNext();){
                        Topic t = iterT.next();
                        if (t.name.equals(tmp)){
                            System.out.println("Name usuwane" + t.name);
                            iterT.remove();

                        }
                    }
                    for (Topic topic : u.subscriptionList){
                        System.out.println(topic.name);
                    }
                }


                for(Iterator<Topic> iterTT = topicList.iterator(); iterTT.hasNext();){
                    Topic tt = iterTT.next();
                    if(tt.name.equals(tmp)){
                        iterTT.remove();
                    }
                }

                System.out.println("Serwer: Admin usunal temat : " + top);
                sc.write(charset.encode(CharBuffer.wrap("Usunieto temat : " + top)));

            }

            else if (cmd.contains("Hi")) {
                String tmp = cmd.toString();
                if (tmp.contains(":")){
                String[] arr = tmp.split(":");

                int id = Integer.parseInt(arr[1]);
                userList.add(new User(id));
                System.out.println("Dodano uzytkownika: " + userList.get(0).ID);
                    sc.write(charset.encode(CharBuffer.wrap("Hi, zalogowano jako użytkownik nr: " + id)));
                } else{
                    sc.write(charset.encode(CharBuffer.wrap("Hi")));
                }


            }else if (cmd.contains("UNSUB:")){
                String tmp = cmd.toString();
                tmp = tmp.replace("UNSUB:","");
                String[] arr = tmp.split(":");
                int id = Integer.parseInt(arr[1]);
                String top = arr[0];

                for(Iterator<User> iter = userList.iterator(); iter.hasNext();){
                    User u = iter.next();
                    if (u.ID == id){
                        for (Iterator<Topic> iterT = u.subscriptionList.iterator(); iterT.hasNext();){
                            Topic t = iterT.next();
                            if (t.name.equals(top)){
                                iterT.remove();
                            }
                    }
                }
                }


                System.out.println("Serwer : user " + id + " odsubskrybowal " + top);
                sc.write(charset.encode(CharBuffer.wrap("Odsubsrkrybowano : " + top)));
            }
            else if (cmd.contains("SUB:")){
                String tmp = cmd.toString();
                tmp = tmp.replace("SUB:","");
                String[] arr = tmp.split(":");
                int id = Integer.parseInt(arr[1]);
                String top = arr[0];
                boolean flag = true;
                boolean topExist = false;
                for (Topic x : topicList){
                    if (x.name.equals(top)){
                        topExist = true;
                    }
                }

                if (topExist) {

                    for (User user : userList) {
                        if (user.ID == id) {
                            for (Topic topic : user.subscriptionList) {
                                if (topic.name.equals(top)) {
                                    flag = false;
                                }
                            }
                            if (flag) {
                                for (Topic t : topicList) {
                                    if (t.name.equals(top)) {
                                        user.subscriptionList.add(t);
                                        System.out.println("Lista subskrybcji dla user : " + id + " ... " + user.subListToString());
                                    }
                                }
                                System.out.println("Serwer : user " + id + " zasubskrybowal " + top);
                                sc.write(charset.encode(CharBuffer.wrap("Zasubsrkrybowano : " + top)));
                            }
                            else {
                                System.out.println("Serwer : user " + id + " juz subskrybuje " + top);
                                sc.write(charset.encode(CharBuffer.wrap("Juz subskrybujesz : " + top)));
                            }
                        }
                    }
                }
                else{
                    System.out.println("Serwer : Brak tematu: " +  top);
                    sc.write(charset.encode(CharBuffer.wrap("Temat " + top + " nie istnieje")));
                }

            }
            else if (cmd.equals("Bye")) {           // koniec komunikacji

                sc.write(charset.encode(CharBuffer.wrap("Bye")));
                System.out.println("Serwer: mówię \"Bye\" do klienta ...\n\n");

                sc.close();                      // - zamknięcie kanału
                sc.socket().close();			 // i gniazda

            }
            else if(cmd.contains("GETTOPICS")){
                String answer = " ";
                for (Topic topic : topicList){
                    answer = answer + topic.name + ", ";
                }
                System.out.println("Serwer: zwracam liste tematow");
                sc.write(charset.encode(CharBuffer.wrap(answer)));
            }else if(cmd.contains("GETSUUB:")){
                String tmp = cmd.toString();
                tmp = tmp.replace("GETSUUB:","");
                int id = Integer.parseInt(tmp);
                String answer = " ";
                for (User user : userList){
                    System.out.println(user.ID);
                    if (user.ID == id){
                        for (Topic t : user.subscriptionList){
                            answer = answer + t.name + " ";
                        }
                    }
                }
                System.out.println("Serwer: zwracam liste subskrybowanych tematow przez uzytkownika " + id);
                sc.write(charset.encode(CharBuffer.wrap(answer)));
            }
            else if(cmd.contains("GETARTICLES:")){
                String answer = " ";
                String tmp = cmd.toString();
                tmp = tmp.replace("GETARTICLES:","");
                int id = Integer.parseInt(tmp);
                for(User user : userList){
                    if (user.ID == id){
                        if (user.subscriptionList.size() > 0) {
                            for (Topic topic : user.subscriptionList) {
                                answer = answer + "<b>" + topic.name + "</b> <br> ";
                                int i = 1;
                                for(String string : topic.articles){
                                    answer = answer + i + " " + string + "<br>";
                                    i++;
                                }
                            }
                        }
                    }
                }
                System.out.println("Serwer: zwracam artykuly");
                sc.write(charset.encode(CharBuffer.wrap(answer)));
            }
            else
                // echo do Klienta
                sc.write(charset.encode(CharBuffer.wrap(reqString)));



        } catch (Exception exc) { // przerwane polączenie?
            exc.printStackTrace();
            try { sc.close();
                sc.socket().close();
            } catch (Exception e) {}
        }

    }
}
