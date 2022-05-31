package com.tienda.granatec.service;

import java.util.List;

import com.tienda.granatec.model.Orden;
import com.tienda.granatec.model.Usuario;

public interface IOrdenService {
	List<Orden> findAll();
	Orden save(Orden orden);
	String generarNumeroOrden();
	List<Orden> findByUsuario (Usuario usuario); 
}
