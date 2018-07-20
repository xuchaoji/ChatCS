package chat;
/**
 * �����ҿͻ���
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
	 * ��װ��TCPЭ�飬ʹ�����Ϳ��Ի���TcpЭ���������ͨѶ
	 * Socket�������ڿͻ��˵�
	 */
	private Socket socket;
	/*
	 * ���췽������ʼ���ͻ���
	 * ʵ����Socket��ʱ����Ҫ����������������IP���������˿�
	 * ʵ�����Ĺ��̾������ӵĹ��̣���������û����Ӧ�����׳��쳣
	 */
	public Client() throws Exception{
		System.out.println("�������ӷ����...");
		socket = new Socket("localhost", 8089);
		System.out.println("���ӷ���˳ɹ�!");
	}
	/**
	 * �����ͻ��˵ķ���
	 */
	public void start() {
		try {
			/*
			 * socket�ķ���
			 * getOutputStream��ȡһ���ֽ��������
			 * ͨ������д�������ݻᱻ���͵�����ˡ�
			 */
			OutputStream out= socket.getOutputStream();
			//ת���ַ�����ָ���ַ���Ϊutf-8,ʹ�ô��Զ���ˢ�µ�pritwriter��
			OutputStreamWriter osw=new OutputStreamWriter(out,"utf-8");
			PrintWriter pw = new PrintWriter(osw,true);
			Scanner scanner= new Scanner(System.in);
			//Ҫ���û��������ǳ�
			String nickname = null;
			while(true) {
				System.out.println("�������ǳƣ�");
				nickname = scanner.nextLine();
				if(nickname.length()>0) {
					break;
				}
				System.out.println("��������");
			}
			System.out.println("Welcome~ "+nickname);
			//�ȷ����ǳ�
			pw.println(nickname);
			/*
			 * ��������ʾ����˷�������Ϣ
			 */
			ServerHandler serverHandler= new ServerHandler();
			Thread thread = new Thread(serverHandler);
			thread.start();
			/*
			 * ���ַ������͵������
			 */
			System.out.println("��������Ϣ��");
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
			System.out.println("�ͻ�������ʧ�ܣ�");
		}
	}
	/**
	 * ���߳�������ȡ����˷�������Ϣ
	 * ��������ͻ��˿���̨��ʾ
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
