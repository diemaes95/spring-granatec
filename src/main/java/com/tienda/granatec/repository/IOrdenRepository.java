package com.tienda.granatec.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tienda.granatec.model.Orden;
import com.tienda.granatec.model.Producto;

@Repository
public interface IOrdenRepository extends JpaRepository<Orden, Integer> {
	
}
