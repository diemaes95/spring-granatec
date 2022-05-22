package com.tienda.granatec.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.tienda.granatec.service.ProductoService;

/*Esta clase tendra la logica para mostrar los productos al usuario*/
@Controller
@RequestMapping("/")
public class HomeController {
	
	private final Logger LOGGER = LoggerFactory.getLogger(HomeController.class);
	
	@Autowired
	private ProductoService productoService;//nos permitira obntener los productos
	
	@GetMapping("")
	public String home(Model model) {
		model.addAttribute("productos", productoService.findAll());//aqui trae todos los productos con findAll()
		return "usuario/home";
	}
	
	@GetMapping("productoHome/{id}")
	public String productoHome(@PathVariable Integer id, Model model ) {
		LOGGER.info("id producto enviado como parametro {}",id);
		return "usuario/productoHome";
	}
	
}
