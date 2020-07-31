package modelo.exceptions;

import java.util.HashMap;
import java.util.Map;

public class ValidarException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	private Map<String, String> erros = new HashMap<>();
	
	public ValidarException(String msg) {
		super(msg);
	}
	
	public Map<String, String> getErros(){
		return erros;
	}
	
	public void addErro(String fieldNome, String messagemErro) {
		erros.put(fieldNome, messagemErro);
	}

}
