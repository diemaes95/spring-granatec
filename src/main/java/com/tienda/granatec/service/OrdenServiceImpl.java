package com.tienda.granatec.service;

import java.util.ArrayList;
import java.util.List;

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

	@Override
	public List<Orden> findAll() {
		// TODO Auto-generated method stub
		return ordenRepository.findAll();
	}
	//metodo para obtener el secuencial  del pedido
	public String generarNumeroOrden() {
		int numero = 0;
		String numeroConcatenado = "";
		//usamos el mismo metodo de la clase findAll()
		List<Orden> ordenes = findAll();
		
		List<Integer> numeros = new ArrayList<Integer>();
		
		//funcion de java8
		ordenes.stream().forEach(x -> numeros.add(Integer.parseInt(x.getNumero())));
		if(ordenes.isEmpty()) {
			//en caso de estar vacio lo ponemos a 1 
			numero = 1;
		}else {
			//obtenemos el mayor numero e incrementamos en 1 
			numero = numeros.stream().max(Integer::compare).get();
			numero++;
		}
		//validacion para obtener el numero de pedido  en cadena con los siguientes ceros delante segun el numero que sea 
		if(numero<10) { //000000009 / 000000010 / 0000000100 / 00000001000 / 00000010000
			numeroConcatenado = "000000000" + String.valueOf(numero);
		}else if(numero<100){
			numeroConcatenado = "00000000" + String.valueOf(numero);
		}else if(numero<1000){
			numeroConcatenado = "0000000" + String.valueOf(numero);
		}else if(numero<10000){
			numeroConcatenado = "000000" + String.valueOf(numero);
		}
		return numeroConcatenado;
		
	}
	
}
