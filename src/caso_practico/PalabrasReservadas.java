package caso_practico;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class PalabrasReservadas {
	/*
	 * PalabrasReservadas es una tabla Hash (String, String) que almacena los
	 * componentes léxicos del lenguaje, definidos por parejas <lexema,
	 * etiquetaLexica> donde el lexema es la clave de la tabla y la etiqueta léxica
	 * el valor
	 */
	private Hashtable<String, String> PalabrasReservadas;

	public PalabrasReservadas(String ficheroPalabrasReservadas) {
		this.PalabrasReservadas = new Hashtable<String, String>();
		leePalabrasReservadas(this.PalabrasReservadas, ficheroPalabrasReservadas);
		//System.out.println(this.PalabrasReservadas.toString());

	}

	public String getEtiqueta(String lexema) {
		return this.PalabrasReservadas.get(lexema);
	}

	public String getLexema(String etiquetaLexica) {
		String lexema = null;
		Set<Map.Entry<String, String>> s = this.PalabrasReservadas.entrySet();
		for (Map.Entry<String, String> m : s)
			if (m.getValue().equals(etiquetaLexica)) {
				lexema = m.getKey();
				break;
			}
		return lexema;
	}

	private static boolean existeFichero(String fichero) {
		File ficheroEntrada = new File(fichero);
		return ficheroEntrada.exists();
	}

	private static String etiqueta(String s) {

		return s.substring(0, s.indexOf(" ")).trim();
	}

	private static String lexema(String s) {
		return s.substring(s.lastIndexOf(" ") + 1).trim();
	}

	private static void leePalabrasReservadas(Hashtable<String, String> PalabrasReservadas,String ficheroPalabrasReservadas) {
		if (existeFichero(ficheroPalabrasReservadas)) {
			try {
				Scanner fichero = new Scanner(new File(ficheroPalabrasReservadas), "UTF-8");
				String componenteLexico, lexema, etiquetaLexica;
				while (fichero.hasNext()) {
					componenteLexico = fichero.nextLine();
					if (componenteLexico.length() > 0 && componenteLexico.charAt(0) != '/') {
						lexema = lexema(componenteLexico);
						etiquetaLexica = etiqueta(componenteLexico);
						PalabrasReservadas.put(lexema, etiquetaLexica);	
					}
				}
				
				fichero.close();
			} catch (IOException e) {
			}
		}
	}
}
