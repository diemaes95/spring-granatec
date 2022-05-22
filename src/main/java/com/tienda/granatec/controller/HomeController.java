package com.tienda.granatec.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.tienda.granatec.service.ProductoService;

/*Esta clase tendra la logica para mostrar los productos al usuario*/
@Controller
@RequestMapping("/")
public class HomeController {
	@Autowired
	private ProductoService productoService;//nos permitira obntener los productos
	
	@GetMapping("")
	public String home(Model model) {
		model.addAttribute("productos", productoService.findAll());//aqui trae todos los productos con findAll()
		return "usuario/home";
	}
	
}
