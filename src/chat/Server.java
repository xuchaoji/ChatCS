package chat;
/**
 * 聊天室服务端
 * @author xucha
 *
 */

import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.text.AbstractDocument.BranchElement;

public class Server {
	/*
	 * 运行在服务端的serverSocket主要负责：
	 * 1.向系统申请端口
	 * 2.监听端口，客户端尝试连接是，在服务端创建一个socket与客户端建立连接
	 */
	private ServerSocket server;
	/*
	 * 保存所有客户端输出流的集合
	 */	
	private List<PrintWriter> allOut;
	/*
	 * 初始化服务端
	 */
	public Server() throws Exception {
		/*
		 * 申请服务端口
		 */
		server = new ServerSocket(8089);
		allOut = new ArrayList<PrintWriter>();
		
	}
	//将输出流存入共享集合
	private synchronized void addOut(PrintWriter out) {
		allOut.add(out);
	}
	//将输出流从共享集合删除
	private synchronized void removeOut(PrintWriter out) {
		allOut.remove(out);
	}
	//将指定消息发给所有客户端
	private synchronized void sendMessage(String message) {
		for(PrintWriter out:allOut) {
			out.println(message);
		}
	}
	/*
	 * 启动服务端
	 */
	public void start() {
		try {
			/*
			 * serversocket的accept方法
			 * 是一个阻塞方法，作用是坚挺服务端口。直到有客户端连接并创建一个Socket
			 * 使用该Socket即可与刚连接的客户端进交互。
			 */
			while(true) {
				System.out.println("等待客户端连接...");
				Socket socket= server.accept();
				System.out.println("一个客户端已连接！");
				/*
				 * 启动线程，完成与客户端的交互
				 */
				ExecutorService threadPool = Executors.newCachedThreadPool();
				threadPool.execute(new ClientHandler(socket));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) {
		try {
			Server server = new Server();
			server.start();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("服务端启动失败！");
		}
	}
	/**
	 * 该线程负责处理一个客户端的交互
	 * @author xucha
	 *
	 */
	class ClientHandler implements Runnable{
		/*
		 * 该线程处理客户端的Socket
		 */
		private Socket socket;
		//客户端地址信息
		private String host;
		public ClientHandler(Socket socket) {
			this.socket = socket;
			InetAddress adderss = socket.getInetAddress();
			//获取IP地址
			host = adderss.getHostAddress();
		}
		
		public void run() {
			PrintWriter pw = null;
			String nickname = null;
			try {
				/*
				 * Socket提供方法 InputStream getInputStream()
				 * 该方法可获取一个输入流，从该流读取的数据就是从客户端发送过来的。
				 */
				InputStream is = socket.getInputStream();
				InputStreamReader isr=new InputStreamReader(is,"utf-8");
				BufferedReader br = new  BufferedReader(isr);
				nickname = br.readLine();
				System.out.println("来自["+host+"]的"+nickname+"上线了！");
				/*
				 * 通过Socket创建输出流用于将消息发给客户端
				 * 
				 */
				OutputStream os= socket.getOutputStream();
				OutputStreamWriter osw= new OutputStreamWriter(os, "utf-8");
				pw= new PrintWriter(osw,true);
				//将该客户端的输出流存入共享集合
				addOut(pw);
				
				String message = null;
				while((message=br.readLine())!=null) {
					//System.out.println(host+"发送："+message);
					//广播消息
					sendMessage("["+host+"]"+nickname+":"+message);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally {
				//客户端断开后显示下线
				System.out.println("["+host+"]"+nickname+"下线了！");
				//将该客户端输出流从共享集合留删除
				removeOut(pw);
			}
			
		}
	}
}
