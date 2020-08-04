package modelo.servicos;

import java.util.List;

import model.dao.DaoFactory;
import model.dao.DepartamentoDao;
import modelo.entidades.Departamento;

public class DepartamentoServico {

	private DepartamentoDao dao = DaoFactory.criarDepartamentoDao();
	
	public List<Departamento> findAll(){	
		return dao.findAll();
	}
	
	public void salvarOuAtualizar(Departamento obj) {
		if(obj.getId() == null) 
			dao.insert(obj);
		else
			dao.update(obj);
	}
	
	public void remover(Departamento obj) {
		dao.deleteById(obj.getId());
	}
	
}
