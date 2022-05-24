package com.tienda.granatec.service;

import org.springframework.beans.factory.annotation.Autowired;

import com.tienda.granatec.model.Orden;
import com.tienda.granatec.repository.IOrdenRepository;

public class OrdenServiceImpl implements IOrdenService{
	
	@Autowired
	private IOrdenRepository ordenRepository;
	
	//metodo para guardar CRUD
	@Override
	public Orden save(Orden orden) {
		// TODO Auto-generated method stub
		return ordenRepository.save(orden);
	}

}
