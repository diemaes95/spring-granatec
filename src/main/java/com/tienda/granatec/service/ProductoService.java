package com.tienda.granatec.service;

import java.util.List;
import java.util.Optional;

import com.tienda.granatec.model.Producto;

public interface ProductoService {
	//guardar
	public Producto save(Producto producto);
	//Optional nos da la capacidad de valiar si el objeto que mandamos a la base de datos existe o no
	//comprobar
	public Optional<Producto> get(Integer id);
	
	//actualizar
	public void update(Producto producto );
	
	//borrar
	public void delete(Integer id);
	
	public List<Producto> findAll();
}
