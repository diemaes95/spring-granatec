package com.tienda.granatec.service;

import java.util.List;
import java.util.Optional;

import com.tienda.granatec.model.Orden;
import com.tienda.granatec.model.Usuario;

public interface IOrdenService {
	List<Orden> findAll();
	Optional<Orden> findById(Integer id);
	Orden save(Orden orden);
	String generarNumeroOrden();
	List<Orden> findByUsuario (Usuario usuario); 
}
