package modelo.servicos;

import java.util.List;

import model.dao.DaoFactory;
import model.dao.VendedorDao;
import modelo.entidades.Vendedor;

public class VendedorServico {

	private VendedorDao dao = DaoFactory.criarVendedorDao();
	
	public List<Vendedor> findAll(){	
		return dao.findAll();
	}
	
	public void salvarOuAtualizar(Vendedor obj) {
		if(obj.getId() == null) 
			dao.insert(obj);
		else
			dao.update(obj);
	}
	
	public void remover(Vendedor obj) {
		dao.deleteById(obj.getId());
	}
	
}
