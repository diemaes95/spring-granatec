package com.tienda.granatec.service;

import java.util.List;

import com.tienda.granatec.model.Orden;

public interface IOrdenService {

	List<Orden> findAll();

	Orden save(Orden orden);
}
