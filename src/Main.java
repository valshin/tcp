import com.sun.deploy.net.HttpUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by valshin on 27.08.15.
 */
public class Main {
    public static void main(String[] args) throws IOException {
        ServerSocket ss = new ServerSocket(8081);
        while (true){
            System.out.println("wait for tcp-connection...");
            Socket socket = ss.accept();
            System.out.println("get one!");
            try {
                InputStream in = socket.getInputStream();
                OutputStream out = socket.getOutputStream();
                byte[] buff = new byte[8192];
                int headerLen = 0;
                while (true){
                    int count = in.read(buff);
                    if (count < 0){
                        throw new RuntimeException("connection close");
                    } else {
                        headerLen += count;
                        if (headerLen > 3 &&
                                buff[headerLen - 4] == '\r' &&
                                buff[headerLen - 3] == '\n' &&
                                buff[headerLen - 2] == '\r' &&
                                buff[headerLen - 1] == '\n'){
                            String response = "HTTP/1.1 200 Ok\n" +
                                    "Server: nginx\n" +
                                    "Date: Thu, 27 Aug 2015 18:52:13 GMT\n" +
                                    "Content-Type: text/html; charset=UTF-8\n" +
                                    "Connection: keep-alive\n\r\n\rYeah!!!";
                            out.write(response.getBytes());
                            break;
                        }
                        if (headerLen == buff.length){
                            throw new RuntimeException("Too big header");
                        }
                    }
                }
            } finally {
                socket.close();
            }
        }
    }
}
