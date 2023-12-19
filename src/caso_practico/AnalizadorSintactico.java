package caso_practico;

import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

public class AnalizadorSintactico {

	private Lexico lexico;
	private ComponenteLexico componenteLexico;
	private Hashtable<String,String> simbolos;
	private String tipo;
	private int tamano;

	public AnalizadorSintactico(Lexico lexico) {
		this.lexico = lexico;
		this.componenteLexico = this.lexico.getComponenteLexico();
		this.simbolos = new Hashtable<String,String>();
	}

	public void analisisSintactico() {
		compara("void");
		compara("main");
		compara("open_bracket");
		declaraciones();
		compara("closed_bracket");
	}
	
	
	public void declaraciones() {	
        if (componenteLexico.getEtiqueta().equals("int") || componenteLexico.getEtiqueta().equals("float")) {
            declaracion();
            declaraciones();
        }
	}

	public void declaracion() {
		tipo();
		if(componenteLexico.getEtiqueta().equals("open_square_bracket")) {
			vector();
	        simbolos.put(this.componenteLexico.getValor(), this.tipo);
	        compara("id");
		}
		else {
			identificadores();
		}
		compara("semicolon");
	}

	
	public void tipo() {
		if(this.componenteLexico.getEtiqueta().equals("int")) {
			this.tipo = "int";
			compara("int");
		}else if(this.componenteLexico.getEtiqueta().equals("float")){
			this.tipo = "float";
			compara("float");
		}

	}

	public void vector() {
		compara("open_square_bracket");
		tamano = Integer.parseInt(componenteLexico.getValor());
		tipo = "array (" + tipo + ", " + tamano + ")";
		compara("int");
		compara("closed_square_bracket");
	}

	public void identificadores() {
        if (componenteLexico.getEtiqueta().equals("id")) {
            simbolos.put(componenteLexico.getValor(), tipo);
            componenteLexico = lexico.getComponenteLexico();
            masIdentificadores();
        }
	}

	public void masIdentificadores() {
        if (componenteLexico.getEtiqueta().equals("comma")) {
            compara("comma");
            if (componenteLexico.getEtiqueta().equals("id")) {
                simbolos.put(componenteLexico.getValor(), tipo); 
                componenteLexico = lexico.getComponenteLexico();
                masIdentificadores();
            }
        }
	}

	public void compara(String token){
		if(this.componenteLexico.getEtiqueta().equals(token)) {
			this.componenteLexico = this.lexico.getComponenteLexico();
		}else {
			System.out.println("Expected: " + token);
		}
	}

	public String tablaSimbolos() {
		String simbolos = "";

		Set<Map.Entry<String, String>> s = this.simbolos.entrySet();
		if(s.isEmpty()) System.out.println("La tabla de simbolos esta vacia\n");
		for(Map.Entry<String, String> m : s) {
			simbolos = simbolos + "<'" + m.getKey() + "', " +
					m.getValue() + "> \n";
		}

		return simbolos;
	}
}