package caso_practico;

import java.nio.charset.StandardCharsets;

public class TestAnalizadorSintactico {

	public static void main(String[] args) {

		boolean mostrarComponentesLexicos = false; // poner a false y no se quieren mostrar los tokens <id, a> ...
		String expresion = "programa1.txt";
		
		ComponenteLexico etiquetaLexica;
		Lexico lexico = new Lexico(expresion,StandardCharsets.UTF_8);

		if (mostrarComponentesLexicos) {

			do {
				etiquetaLexica = lexico.getComponenteLexico();
				System.out.println("<" + etiquetaLexica.toString() + ">");
			} while (!etiquetaLexica.getEtiqueta().equals("end_program"));
			System.out.println("");
		}

		AnalizadorSintactico compilador = new AnalizadorSintactico(new Lexico(expresion,StandardCharsets.UTF_8));

		System.out.println(expresion + " compilado perfectamente\n");

		compilador.analisisSintactico();

		System.out.println("Tabla de simbolos \n\n");
		String simbolos = compilador.tablaSimbolos();
		System.out.println(simbolos);
		
		compilador.mostrarPila();
	}
}