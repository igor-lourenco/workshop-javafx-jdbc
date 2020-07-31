package modelo.servicos;

import java.util.ArrayList;
import java.util.List;

import modelo.entidades.Departamento;

public class DepartamentoServico {

	public List<Departamento> findAll(){
		List<Departamento> lista = new ArrayList<Departamento>();
		lista.add(new Departamento(1, "Livros"));
		lista.add(new Departamento(2, "Computadores"));
		lista.add(new Departamento(3, "Eletronicos"));
		return lista;
		
	}
}
