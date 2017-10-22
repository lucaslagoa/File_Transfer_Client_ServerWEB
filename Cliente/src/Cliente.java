import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Cliente {
	static ObjectInputStream entrada;
	static InputStream is;
	static String url;
	static Scanner teclado;

	public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException {

		System.out.println("O cliente se conectou ao servidor!");
		System.out.println("Digite o IP do servidor!");
		teclado = new Scanner(System.in);
		String ip = teclado.nextLine();
		System.out.println(
				"Bem-vindo ao navegador! \n Para acessar as páginas, digite 'navegador http://urldesejada Porta'");
		String comando = teclado.nextLine();
		String[] parts = comando.split(" ");
		String door = null;

		if (parts.length == 3) {
			url = parts[1];
			door = parts[2];
		} else if (parts.length == 2) {
			url = parts[1];
			door = "8082";
		} else {
			System.out.println("Digitou errado, tchau!");
			System.exit(0);
		}

		int porta = Integer.parseInt(door);
		Socket cliente = new Socket(ip, porta);

		entrada = new ObjectInputStream(cliente.getInputStream());
		is = cliente.getInputStream();

		PrintStream saida = new PrintStream(cliente.getOutputStream());

		saida.println(comando);
		saida.flush();
		GET();

		while (teclado.hasNextLine()) {
			
			//System.out.println("Para acessar as páginas, digite 'navegador http://urldesejada Porta'");


			comando = teclado.nextLine();
			parts = comando.split(" ");
			door = null;

			if (parts.length == 3) {
				url = parts[1];
				door = parts[2];
			} else if (parts.length == 2) {
				url = parts[1];
				door = "8082";
			} else {
				System.out.println("Digitou errado, tchau!");
				System.exit(0);
			}

			saida.println(comando);

			saida.flush();
			GET();

		}
		saida.close();
		teclado.close();
		cliente.close();
	}

	static public void GET() throws IOException, ClassNotFoundException {

		String nomeArquivo = url;
		System.out.println(nomeArquivo);
		String msg = (String) entrada.readObject();

		if (msg.equals("OK")) {

			FileOutputStream fos = new FileOutputStream(nomeArquivo);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			
			byte[] contents = new byte[1];
			int bytesRead = 0;
			long tam = (long) entrada.readLong();
			int valor = 0;

			System.out.println("tamanho: " + tam);
	
			while (tam > 0) {

				if (tam >= 1) {
					tam = tam - 1;
					valor = 1;
				} else if (tam < 1) {
					valor = (int) tam;
					tam = 0;
				}

				contents = new byte[valor];

				bytesRead = is.read(contents, 0, valor);
				bos.write(contents, 0, bytesRead);
				bos.flush();
			}

			System.out.println("Arquivo salvo com sucesso");

			bos.close();
			fos.close();
			
		} else {
			System.out.println("Error 404 - Page not found!");

		}

		// is.close();
	}

}