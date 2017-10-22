import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Servidor {
	
	static ObjectOutputStream saida;
	static OutputStream os;
	static int porta = 8080;
	static String navegador;
	static String url;
	static String door;
	
	public static void main(String[] args) throws IOException {
		
		ServerSocket servidor = new ServerSocket(porta);
		System.out.println("Servidor está rodando na porta -" + porta);

		Socket cliente = servidor.accept();
		System.out.println("Nova conexão com o cliente " + cliente.getInetAddress().getHostAddress());

		Scanner s = new Scanner(cliente.getInputStream());
		
		while (s.hasNextLine()) {
			String requisicao = s.nextLine();
			System.out.println(requisicao);
			String[] parts = requisicao.split(" ");
			if (parts.length == 3) {
				navegador = parts[0];
				url = parts[1];
				door = parts[2];
			}
			if (parts.length == 2){
				navegador = parts[0];
				url = parts[1];
				door = "8080";
				
			}
			
		}
		s.close();
		servidor.close();
		cliente.close();
	}
		
	static public void GET() throws IOException, ClassNotFoundException {
			
		String nomeArquivo = url;
		
		File file = new File(nomeArquivo);
//			File file = null;
//			while(true){
//				String nomeArquivo = (String) entrada.readObject();
//
//				file = new File(nomeArquivo);
//				if(!file.exists()){
//					System.out.println("Arquivo nao existe");
//					saida.flush();
//					saida.writeObject("Nao existe");
//				}else 
//					break;
//			}
		
		FileInputStream fis = new FileInputStream(file);
		BufferedInputStream bis = new BufferedInputStream(fis); 
		
		saida.flush();
		saida.writeObject(file.length());
		
		long i = 0;
		long tam = file.length();
		int valor = 0;
		byte[] contents;
		while(tam > 0){
			
			if (tam >= 1024){
				tam = tam - 1024;
				valor = 1024;
			}else if (tam < 1024){
				valor = (int) tam;
				tam = 0;
			}
			
			contents = new byte[valor]; 
			bis.read(contents, 0, valor); 
			os.write(contents);
		
			//System.out.println("Enviando arquivo ... "+(i*100)/file.length()+"% completo!");
			i += 1024;
		}	
		
	}
}