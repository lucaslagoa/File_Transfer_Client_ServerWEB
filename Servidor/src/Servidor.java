import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Servidor implements Runnable {

	static ObjectOutputStream saida;
	static OutputStream os;
	static int porta = 8080;
	static String navegador;
	static String url;
	static String door;
	static PrintStream mensagem;
	static Socket servidor;
	static ServerSocket sservidor;
	
	Servidor(Socket servidor) {
	      this.servidor = servidor;
	}
	
	public static void main(String[] args) throws IOException, ClassNotFoundException {

		sservidor = new ServerSocket(porta);
		System.out.println("Servidor está rodando na porta - " + porta + "\n");
		
		 while (true) {
	         Socket servidor = sservidor.accept();
	         new Thread(new Servidor(servidor)).start();
	      }
		 
	}
	
	public void run() {
		System.out.println("Nova conexão com o servidor " + servidor.getInetAddress().getHostAddress() + "\n");

		try {
			saida = new ObjectOutputStream(servidor.getOutputStream());
			os = servidor.getOutputStream();
			mensagem = new PrintStream(os);
			Scanner s = new Scanner(servidor.getInputStream());

			while (s.hasNextLine()) {
				String requisicao = s.nextLine();
				System.out.println(requisicao);
				String[] parts = requisicao.split(" ");
				if (parts.length == 3) {
					navegador = parts[0];
					url = parts[1];
					door = parts[2];
				} else if (parts.length == 2) {
					navegador = parts[0];
					url = parts[1];
					door = "8080";
				} else {
					System.out.println("O ESPACO EM BRANCO");
				}
				GET();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	static public void GET() throws IOException, ClassNotFoundException {

		String nomeArquivo = "/home/lucas/workspace/Servidor/src/" + url;
		

		File file = new File(nomeArquivo);

		if (file.exists()) {
			String msg = "OK";
			saida.writeObject(msg);
			saida.flush();

			FileInputStream fis = new FileInputStream(file);
			BufferedInputStream bis = new BufferedInputStream(fis);

			saida.writeLong((long) file.length());
			saida.flush();

			// long i = 0;
			
			
			long tam = file.length();
			int valor = 0;
			byte[] contents;
			int i = 0;
			while (tam > 0) {

				if (tam >= 1) {
					tam = tam - 1;
					valor = 1;
				} else if (tam < 1) {
					valor = (int) tam;
					tam = 0;
				}

				contents = new byte[valor];
				bis.read(contents, 0, valor);
				os.write(contents);
				os.flush();

				//System.out.println("Enviando arquivo ... " + (i * 100) /
				//file.length() + "% completo!");
				//i += 1;

			}
			fis.close();
			bis.close();

		} else {
			System.out.println("Error 404 - Page not found!\n");
			String msg = "NOT OK";
			saida.writeObject(msg);

		}

	}
}