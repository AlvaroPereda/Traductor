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
		instrucciones();
		compara("closed_bracket");
	}
	
	
	public void declaraciones() {
		if (componenteLexico.getEtiqueta().equals("int") || componenteLexico.getEtiqueta().equals("float") || componenteLexico.getEtiqueta().equals("boolean")) {
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
		if (this.componenteLexico.getEtiqueta().equals("int")) {
			this.tipo = "int";
			compara("int");
		} else if (this.componenteLexico.getEtiqueta().equals("float")) {
			this.tipo = "float";
			compara("float");
		} else if (this.componenteLexico.getEtiqueta().equals("boolean")) {
			this.tipo = "boolean";
			compara("boolean");
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
        	asignacionDeclaracion();
            masIdentificadores();
        }	
	}

	public void masIdentificadores() {
        if (componenteLexico.getEtiqueta().equals("comma")) {
            compara("comma");
            if (componenteLexico.getEtiqueta().equals("id")) {
                simbolos.put(componenteLexico.getValor(), tipo); 
                componenteLexico = lexico.getComponenteLexico();
                asignacionDeclaracion();
                masIdentificadores();
            }
        }
	}
    public void instrucciones() {
        if(!componenteLexico.getEtiqueta().equals("closed_bracket") && !componenteLexico.getEtiqueta().equals("end_program")) {
            instruccion();
            instrucciones();
        }
    }
    public void instruccion() {
        if (componenteLexico.getEtiqueta().equals("int") || componenteLexico.getEtiqueta().equals("float") || componenteLexico.getEtiqueta().equals("boolean")) {
        	declaracion();
        } else if (componenteLexico.getEtiqueta().equals("id")) {
            variable();
            compara("assignment");
            expresionLogica();
            compara("semicolon");
        } else if (componenteLexico.getEtiqueta().equals("if")) {
            compara("if");
            compara("open_parenthesis");
            expresionLogica();
            compara("closed_parenthesis");
            instruccion();
            if (componenteLexico.getEtiqueta().equals("else")) {
                compara("else");
                instruccion();
            }
        } else if (componenteLexico.getEtiqueta().equals("while")) {
            compara("while");
            compara("open_parenthesis");
            expresionLogica();
            compara("closed_parenthesis");
            instruccion();
        } else if (componenteLexico.getEtiqueta().equals("do")) {
            compara("do");
            instruccion();
            compara("while");
            compara("open_parenthesis");
            expresionLogica();
            compara("closed_parenthesis");
            compara("semicolon");
        } else if (componenteLexico.getEtiqueta().equals("print")) {
            compara("print");
            compara("open_parenthesis");
            variable();
            compara("closed_parenthesis");
            compara("semicolon");
        } else if (componenteLexico.getEtiqueta().equals("open_bracket")) {
            compara("open_bracket");
            instrucciones();
            compara("closed_bracket");
        }
    }
    
    public void variable() {
        compara("id");
        if (componenteLexico.getEtiqueta().equals("open_square_bracket")) {
            compara("open_square_bracket");
            expresion();
            compara("closed_square_bracket");
        }
    }

	public void asignacionDeclaracion() {
        if (componenteLexico.getEtiqueta().equals("assignment")) {
            compara("assignment");
            expresionLogica();
        }
	}
	
	public void expresionLogica() {
	    terminoLogico();
	    if (componenteLexico.getEtiqueta().equals("or")) {
	        compara("or");
	        terminoLogico();
	    }
	}

	public void terminoLogico() {
	    factorLogico();
	    if (componenteLexico.getEtiqueta().equals("and")) {
	        compara("and");
	        factorLogico();
	    }
	}

	public void factorLogico() {
	    if (componenteLexico.getEtiqueta().equals("not")) {
	        compara("not");
	        factorLogico();
	    } else if (componenteLexico.getEtiqueta().equals("true") || componenteLexico.getEtiqueta().equals("false")) {
	        compara(componenteLexico.getEtiqueta());
	    } else {
	        expresionRelacional();
	    }
	}
    public void expresionRelacional() {
        expresion();
        if (componenteLexico.getEtiqueta().equals("greater_than") ||
            componenteLexico.getEtiqueta().equals("greater_equals") ||
            componenteLexico.getEtiqueta().equals("less_than") ||
            componenteLexico.getEtiqueta().equals("less_equals") ||
            componenteLexico.getEtiqueta().equals("equals") ||
            componenteLexico.getEtiqueta().equals("not_equals")) {
            
            compara(componenteLexico.getEtiqueta());
            expresion();
        }
    }
    
    public void expresion() {
        termino();
        if (componenteLexico.getEtiqueta().equals("add") || componenteLexico.getEtiqueta().equals("subtract")) {
            compara(componenteLexico.getEtiqueta());
            termino();
            expresion();
        }
    }
    public void termino() {
        factor();
        if(componenteLexico.getEtiqueta().equals("multiply") || componenteLexico.getEtiqueta().equals("divide") || componenteLexico.getEtiqueta().equals("remainder")) {
            compara(componenteLexico.getEtiqueta());
            factor();
            termino();
        }
    }
    public void factor() {
        if (componenteLexico.getEtiqueta().equals("open_parenthesis")) {
            compara("open_parenthesis");
            expresion();
            compara("closed_parenthesis");
        } else if (componenteLexico.getEtiqueta().equals("int") || componenteLexico.getEtiqueta().equals("float")) {
            compara(componenteLexico.getEtiqueta());
        } else if (componenteLexico.getEtiqueta().equals("id")) {
        	variable();
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