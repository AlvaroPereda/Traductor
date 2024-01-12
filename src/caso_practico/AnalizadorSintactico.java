package caso_practico;

import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.Stack;


public class AnalizadorSintactico {

	private Lexico lexico;
	private ComponenteLexico componenteLexico;
	private Hashtable<String,String> simbolos;
	private String tipo;
	private int tamano;
	private String valor;
    private Stack<String> pila;

	public AnalizadorSintactico(Lexico lexico) {
		this.lexico = lexico;
		this.componenteLexico = this.lexico.getComponenteLexico();
		this.simbolos = new Hashtable<String,String>();
		this.pila = new Stack<String>();	
	}

	public void analisisSintactico() {
		compara("void");
		compara("main");
		compara("open_bracket");
		declaraciones();
		instrucciones();
		compara("closed_bracket");
		pila.push("halt");
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
			if(variableDeclarada(componenteLexico.getValor()))
				System.out.println("Error en la linea " + lexico.getLineas() + ", identificador '" + componenteLexico.getValor() + "' ya declarado");

	        simbolos.put(this.componenteLexico.getValor(), this.tipo);
	        compara("id");
		}
		else {
			if(variableDeclarada(componenteLexico.getValor()))
				System.out.println("Error en la linea " + lexico.getLineas() + ", identificador '" + componenteLexico.getValor() + "' ya declarado");
			
			pila.push("lvalue " + componenteLexico.getValor());
			
			valor = componenteLexico.getValor();
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
		try {
			tamano = Integer.parseInt(componenteLexico.getValor());
		} catch(NumberFormatException e) {
			System.out.println("El tamano del array debe ser un int");
			throw e;
		} catch(Exception e) {
			System.out.println(e);
			throw e;
		}
		valor = tipo;
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
                pila.push("lvalue " + componenteLexico.getValor());
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
			if (!valor.equals("int") && !valor.equals("float") && !valor.equals("boolean")) {
				valor = componenteLexico.getValor();
			}
			if (!variableDeclarada(componenteLexico.getValor()))
				System.out.println("Error en la linea " + lexico.getLineas() + ", identificador '" + componenteLexico.getValor() + "' no declarado");
			pila.push("lvalue " + componenteLexico.getValor());
			variable();
			asignacionDeclaracion();
			compara("semicolon");
		} else if (componenteLexico.getEtiqueta().equals("if")) {
			compara("if");
			compara("open_parenthesis");
			expresionLogica();
			compara("closed_parenthesis");
			instruccion();
			if (componenteLexico.getEtiqueta().equals("else")) {
				pila.push("goto label_1");
				pila.push("label_0:");
				compara("else");
				instruccion();
			}
		} else if (componenteLexico.getEtiqueta().equals("while")) {;
			pila.push("label_0:");
			compara("while");
			compara("open_parenthesis");
			expresionLogica();
			compara("closed_parenthesis");
			instruccion();
			pila.push("goto label_0");
		} else if (componenteLexico.getEtiqueta().equals("do")) {
			pila.push("label_0:");
			compara("do");
			instruccion();
			compara("while");
			compara("open_parenthesis");
			expresionLogica();
			compara("closed_parenthesis");
			compara("semicolon");
			pila.push("goto label_0");
		} else if (componenteLexico.getEtiqueta().equals("print")) {
			compara("print");
			compara("open_parenthesis");
			pila.push("label_1:");
			pila.push("print " + componenteLexico.getValor());
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
        	String aux = componenteLexico.getEtiqueta();
            compara("assignment");
        	variablesIncompatibles(valor);
            expresionLogica();
            variablesPila(aux);
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
        if (operadorRelacional()) {            
        	String aux = componenteLexico.getEtiqueta();
            compara(componenteLexico.getEtiqueta());
            expresion();
            variablesPila(aux);
        }
    }
    public boolean operadorRelacional() {
        if (componenteLexico.getEtiqueta().equals("greater_than") ||
                componenteLexico.getEtiqueta().equals("greater_equals") ||
                componenteLexico.getEtiqueta().equals("less_than") ||
                componenteLexico.getEtiqueta().equals("less_equals") ||
                componenteLexico.getEtiqueta().equals("equals") ||
                componenteLexico.getEtiqueta().equals("not_equals")) {
        	return true;
        }
        return false;
    }
    
    public void expresion() {
        termino();
        if (componenteLexico.getEtiqueta().equals("add") || componenteLexico.getEtiqueta().equals("subtract")) {
        	String aux = componenteLexico.getEtiqueta();
            compara(componenteLexico.getEtiqueta());
            termino();
            variablesPila(aux);
            expresion();
            
        }
    }
    public void termino() {
        factor();
        if(componenteLexico.getEtiqueta().equals("multiply") || componenteLexico.getEtiqueta().equals("divide") || componenteLexico.getEtiqueta().equals("remainder")) {
           
        	String aux = componenteLexico.getEtiqueta();
        	
        	compara(componenteLexico.getEtiqueta());
            factor();
            termino();
            variablesPila(aux);
        }
    }
    public void factor() {
        if (componenteLexico.getEtiqueta().equals("open_parenthesis")) {
            compara("open_parenthesis");
            expresion();
            compara("closed_parenthesis");
        } else if (componenteLexico.getEtiqueta().equals("int") || componenteLexico.getEtiqueta().equals("float")) {
        	
        	pila.push("push " + componenteLexico.getValor());
        	
            compara(componenteLexico.getEtiqueta());
        } else if (componenteLexico.getEtiqueta().equals("id")) {
        	
            pila.push("rvalue " + componenteLexico.getValor());
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
	
	public boolean variableDeclarada(String variable) {
		return simbolos.containsKey(variable);
	}
	public void variablesIncompatibles(String variable) {
		if (!variable.equals("int") && !variable.equals("float") && !variable.equals("boolean")) {
			String aux = recorrerHastable(variable);
			if(componenteLexico.getEtiqueta().equals("int") || componenteLexico.getEtiqueta().equals("float") || componenteLexico.getEtiqueta().equals("boolean") ) {
				if(!componenteLexico.getEtiqueta().equals(aux)) {
					System.out.println("Error en la linea " + lexico.getLineas() +", incompatibilidad de tipos en la instrucción de asignación");
				}
			} else if(componenteLexico.getEtiqueta().equals("id")) {
				String aux2 = recorrerHastable(componenteLexico.getValor());
				if(aux != aux2) {
					System.out.println("Error en la linea " + lexico.getLineas() +", incompatibilidad de tipos en la instrucción de asignación");
				}
			}
		} else {
			if(!componenteLexico.getEtiqueta().equals(variable)) {
				System.out.println("Error en la linea " + lexico.getLineas() +", incompatibilidad de tipos en la instrucción de asignación");
			}
		}
	}
	public String recorrerHastable(String valor) {
	    for (Map.Entry<String, String> aux: simbolos.entrySet()) {
	        if(aux.getKey().equals(valor)) {
	        	return aux.getValue();
	        }
	    }
	    return null;
	}
	

    public void mostrarPila() {
        System.out.println("Codigo de la maquina de pila\n");

        if (pila.isEmpty()) {
            System.out.println("La pila está vacía.");
        } else {
            for (String aux : pila) {
                System.out.println(aux);
            }
        }
    }
    
    private void variablesPila(String token) {
    	switch (token) {
    	case "assignment":
    		pila.push("=");
    		break;
    	case "add":
    		pila.push("+");
    		break;
    	case "subtract":
    		pila.push("-");
    		break;
    	case "multiply":
    		pila.push("*");
    		break;
    	case "divide":
    		pila.push("/");
    		break;
    	case "remainder":
    		pila.push("%");
    		break;
    	case "greater_than":
            pila.push(">");
            break;
        case "greater_equals":
            pila.push(">=");
            break;
        case "less_than":
            pila.push("<");
            break;
        case "less_equals":
            pila.push("<=");
            pila.push("gofalse label_1");
            break;
        case "equals":
            pila.push("==");
            pila.push("gofalse label_0");
            break;
        case "not_equals":
            pila.push("!=");
            break;
		default:
			pila.push("Invalid_character");
    	}
    }
}
