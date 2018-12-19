import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import net.sf.json.JSONObject;

public class client {
    private static final String IP = "192.168.43.129";
    private static final int port = 8123;

    public static void main(String[] args) {
        try {
            person person = new person("drc", "123456");
//            person = (JSONObject) person;
            JSONObject jsonObject = JSONObject.fromObject(person);
            Socket client = new Socket(IP, port);
            OutputStream os = client.getOutputStream();//输出流
            ObjectOutputStream ois = new ObjectOutputStream(os);
            ois.writeObject(jsonObject);
            ois.flush();
            String info = null;
            InputStream is = client.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            while ((info = br.readLine()) != null) {
                System.out.println("server1:" + info);
            }
//            PrintWriter pw =new PrintWriter(os);//将输出流包装成打印流
//            InputStream is = client.getInputStream();
//            BufferedReader br = new BufferedReader(new InputStreamReader(is));
//            pw.write("try to connect to server!");
//            pw.flush();
//            String info = null;
//            while((info=br.readLine()) != null){
//                System.out.println("server1:" + info);
//            }
//            pw.write(1);
//            pw.flush();
//            String info1 = null;
//            while((info1=br.readLine()) != null){
//                System.out.println("server2:" + info1);
//            }
//            Scanner scanner = new Scanner(System.in);
//            String temp = scanner.nextLine();
//            pw.write(temp);
//            pw.flush();
//            String info2 = null;
//            while((info2=br.readLine()) != null){
//                System.out.println("server3:" + info2);
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /*
    //客户端
  //1、创建客户端Socket，指定服务器地址和端口
  Socket socket =newSocket("localhost",10086);
  //2、获取输出流，向服务器端发送信息
  OutputStream os = socket.getOutputStream();//字节输出流
  PrintWriter pw =newPrintWriter(os);//将输出流包装成打印流
  pw.write("用户名：admin；密码：123");
  pw.flush();
  socket.shutdownOutput();
 //3、获取输入流，并读取服务器端的响应信息
 InputStream is = socket.getInputStream();
 BufferedReader br = new BufferedReader(new InputStreamReader(is));
 String info = null;
 while((info=br.readLine())!null){
  System.out.println("我是客户端，服务器说："+info);
 }
     */
}
