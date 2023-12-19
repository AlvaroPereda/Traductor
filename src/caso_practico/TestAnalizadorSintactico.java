package caso_practico;

public class TestAnalizadorSintactico {

	public static void main(String[] args) {

		boolean mostrarComponentesLexicos = false; // poner a false y no se quieren mostrar los tokens <id, a> ...

		String expresion = "void main {int a, b, c, d; float x; float [10] v;}";

		ComponenteLexico etiquetaLexica;
		Lexico lexico = new Lexico(expresion);

		if (mostrarComponentesLexicos) {

			do {
				etiquetaLexica = lexico.getComponenteLexico();
				System.out.println("<" + etiquetaLexica.toString() + ">");

			} while (!etiquetaLexica.getEtiqueta().equals("end_program"));

			System.out.println("");
		}

		AnalizadorSintactico compilador = new AnalizadorSintactico(new Lexico(expresion));

		System.out.println("Compilacion de sentencia de declaraciones de variables");
		System.out.println(expresion + "\n");

		compilador.analisisSintactico();

		System.out.println("Tabla de simbolos \n\n");
		String simbolos = compilador.tablaSimbolos();
		System.out.println(simbolos);
	}
}