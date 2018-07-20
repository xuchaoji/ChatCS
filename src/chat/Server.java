package chat;
/**
 * �����ҷ����
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
	 * �����ڷ���˵�serverSocket��Ҫ����
	 * 1.��ϵͳ����˿�
	 * 2.�����˿ڣ��ͻ��˳��������ǣ��ڷ���˴���һ��socket��ͻ��˽�������
	 */
	private ServerSocket server;
	/*
	 * �������пͻ���������ļ���
	 */	
	private List<PrintWriter> allOut;
	/*
	 * ��ʼ�������
	 */
	public Server() throws Exception {
		/*
		 * �������˿�
		 */
		server = new ServerSocket(8089);
		allOut = new ArrayList<PrintWriter>();
		
	}
	//����������빲����
	private synchronized void addOut(PrintWriter out) {
		allOut.add(out);
	}
	//��������ӹ�����ɾ��
	private synchronized void removeOut(PrintWriter out) {
		allOut.remove(out);
	}
	//��ָ����Ϣ�������пͻ���
	private synchronized void sendMessage(String message) {
		for(PrintWriter out:allOut) {
			out.println(message);
		}
	}
	/*
	 * ���������
	 */
	public void start() {
		try {
			/*
			 * serversocket��accept����
			 * ��һ�����������������Ǽ�ͦ����˿ڡ�ֱ���пͻ������Ӳ�����һ��Socket
			 * ʹ�ø�Socket����������ӵĿͻ��˽�������
			 */
			while(true) {
				System.out.println("�ȴ��ͻ�������...");
				Socket socket= server.accept();
				System.out.println("һ���ͻ��������ӣ�");
				/*
				 * �����̣߳������ͻ��˵Ľ���
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
			System.out.println("���������ʧ�ܣ�");
		}
	}
	/**
	 * ���̸߳�����һ���ͻ��˵Ľ���
	 * @author xucha
	 *
	 */
	class ClientHandler implements Runnable{
		/*
		 * ���̴߳���ͻ��˵�Socket
		 */
		private Socket socket;
		//�ͻ��˵�ַ��Ϣ
		private String host;
		public ClientHandler(Socket socket) {
			this.socket = socket;
			InetAddress adderss = socket.getInetAddress();
			//��ȡIP��ַ
			host = adderss.getHostAddress();
		}
		
		public void run() {
			PrintWriter pw = null;
			String nickname = null;
			try {
				/*
				 * Socket�ṩ���� InputStream getInputStream()
				 * �÷����ɻ�ȡһ�����������Ӹ�����ȡ�����ݾ��Ǵӿͻ��˷��͹����ġ�
				 */
				InputStream is = socket.getInputStream();
				InputStreamReader isr=new InputStreamReader(is,"utf-8");
				BufferedReader br = new  BufferedReader(isr);
				nickname = br.readLine();
				System.out.println("����["+host+"]��"+nickname+"�����ˣ�");
				/*
				 * ͨ��Socket������������ڽ���Ϣ�����ͻ���
				 * 
				 */
				OutputStream os= socket.getOutputStream();
				OutputStreamWriter osw= new OutputStreamWriter(os, "utf-8");
				pw= new PrintWriter(osw,true);
				//���ÿͻ��˵���������빲����
				addOut(pw);
				
				String message = null;
				while((message=br.readLine())!=null) {
					//System.out.println(host+"���ͣ�"+message);
					//�㲥��Ϣ
					sendMessage("["+host+"]"+nickname+":"+message);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally {
				//�ͻ��˶Ͽ�����ʾ����
				System.out.println("["+host+"]"+nickname+"�����ˣ�");
				//���ÿͻ���������ӹ�������ɾ��
				removeOut(pw);
			}
			
		}
	}
}
