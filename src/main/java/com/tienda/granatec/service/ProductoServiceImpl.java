package com.tienda.granatec.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tienda.granatec.model.Producto;
import com.tienda.granatec.repository.ProductoRepository;

@Service
public class ProductoServiceImpl implements ProductoService{
	/*Autowired significa que le estamos inyectando a esta clase un objeto */
	@Autowired
	private ProductoRepository productoRepository;
	@Override
	public Producto save(Producto producto) {
		// TODO Auto-generated method stub
		return productoRepository.save(producto);
	}

	@Override
	public Optional<Producto> get(Integer id) {
		// TODO Auto-generated method stub
		return productoRepository.findById(id);
	}

	@Override
	public void update(Producto producto) {
		//si el metodo save encuentra un producto ya creado lo actualiza
		productoRepository.save(producto);
		
	}

	@Override
	public void delete(Integer id) {
		productoRepository.deleteById(id);
		
	}

	@Override
	public List<Producto> findAll() {
		
		return productoRepository.findAll();
	}

}
