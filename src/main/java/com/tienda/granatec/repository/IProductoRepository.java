package com.tienda.granatec.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tienda.granatec.model.Producto;

/*interface para poder obtener todos los
 *  metodos crud y poder aplicar a producto */

/*JpaRepository<Producto, Integer>---> indicamos la clase que hara referencia
 * 				 y el tipo de dato que se le aplicara al crud ejemplo por ID  */

@Repository
public interface IProductoRepository extends JpaRepository<Producto, Integer> {
	
}
