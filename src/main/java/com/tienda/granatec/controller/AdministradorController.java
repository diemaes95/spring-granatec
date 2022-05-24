package com.tienda.granatec.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.tienda.granatec.model.Producto;
import com.tienda.granatec.service.IProductoService;

@Controller
@RequestMapping("/administrador")
public class AdministradorController {
	@Autowired
	private IProductoService productoService;
	
	@GetMapping("")
	public String home(Model model) {//cuando se haga la peticion al metodo devuelve los productos
		List<Producto> productos = productoService.findAll();
		model.addAttribute("productos", productos);
		return "administrador/home";
		
	}
}
