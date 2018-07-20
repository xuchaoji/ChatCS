package chat;
/**
 * 聊天室客户端
 * @author xucha
 *
 */

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
	/*
	 * java.net.Socket
	 * 封装了TCP协议，使用它就可以基于Tcp协议进行网络通讯
	 * Socket是运行在客户端的
	 */
	private Socket socket;
	/*
	 * 构造方法，初始化客户端
	 * 实例化Socket的时候需要两个参数：服务器IP、服务器端口
	 * 实例化的过程就是连接的过程，若服务器没有响应，会抛出异常
	 */
	public Client() throws Exception{
		System.out.println("正在连接服务端...");
		socket = new Socket("localhost", 8089);
		System.out.println("连接服务端成功!");
	}
	/**
	 * 启动客户端的方法
	 */
	public void start() {
		try {
			/*
			 * socket的方法
			 * getOutputStream获取一个字节输出流，
			 * 通过该流写出的数据会被传送到服务端。
			 */
			OutputStream out= socket.getOutputStream();
			//转换字符流，指定字符集为utf-8,使用带自动行刷新的pritwriter。
			OutputStreamWriter osw=new OutputStreamWriter(out,"utf-8");
			PrintWriter pw = new PrintWriter(osw,true);
			Scanner scanner= new Scanner(System.in);
			//要求用户先输入昵称
			String nickname = null;
			while(true) {
				System.out.println("请输入昵称：");
				nickname = scanner.nextLine();
				if(nickname.length()>0) {
					break;
				}
				System.out.println("输入有误！");
			}
			System.out.println("Welcome~ "+nickname);
			//先发送昵称
			pw.println(nickname);
			/*
			 * 启动线显示服务端发来的消息
			 */
			ServerHandler serverHandler= new ServerHandler();
			Thread thread = new Thread(serverHandler);
			thread.start();
			/*
			 * 将字符串发送到服务端
			 */
			System.out.println("请输入消息：");
			while (true) {
				pw.println(scanner.nextLine());
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) {
		try {
			Client client = new Client();
			client.start();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("客户端启动失败！");
		}
	}
	/**
	 * 该线程用来读取服务端发来的消息
	 * 并输出到客户端控制台显示
	 */
	class ServerHandler implements Runnable{
		@Override
		public void run() {
			try {
				InputStream is = socket.getInputStream();
				InputStreamReader isr=new InputStreamReader(is,"utf-8");
				BufferedReader br = new  BufferedReader(isr);
				String message = null;
				while((message=br.readLine())!=null) {
					System.out.println(message);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
	}
}
