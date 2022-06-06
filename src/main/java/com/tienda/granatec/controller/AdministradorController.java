package com.tienda.granatec.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.tienda.granatec.model.Orden;
import com.tienda.granatec.model.Producto;
import com.tienda.granatec.service.IOrdenService;
import com.tienda.granatec.service.IProductoService;
import com.tienda.granatec.service.IUsuarioService;

@Controller
@RequestMapping("/administrador")
public class AdministradorController {
	@Autowired
	private IProductoService productoService;
	
	@Autowired
	private IUsuarioService usuarioService;
	
	@Autowired
	private IOrdenService ordenService;
	
	private Logger logg=LoggerFactory.getLogger(AdministradorController.class);
	
	@GetMapping("")
	public String home(Model model) {//cuando se haga la peticion al metodo devuelve los productos
		List<Producto> productos = productoService.findAll();
		model.addAttribute("productos", productos);
		return "administrador/home";
		
	}
	
	@GetMapping("/usuarios")
	public String usuarios(Model model) {
		model.addAttribute("usuarios", usuarioService.findAll());
		return "administrador/usuarios";
	}
	
	@GetMapping("/pedidos")
	public String pedidos(Model model) {
		model.addAttribute("pedidos", ordenService.findAll());
		return "administrador/pedidos";
	}
	@GetMapping("/detalle/{id}")
	public String detalle(Model model, @PathVariable Integer id) {
		logg.info("id del pedido {} ", id);
		Orden orden = ordenService.findById(id).get();
		
		model.addAttribute("detalles", orden.getDetalle());
		return "administrador/detallepedido";
	}
	@PostMapping("/buscarAdmin")
	public String searchProducto(@RequestParam String nombre, Model model) {
		logg.info("Nombre del producto {}", nombre);
		//Devuelve una lista con los productos que contengan los caracteres buscados
		List<Producto> productos = productoService.findAll().stream()
				.filter(x -> x.getNombre().toUpperCase().contains(nombre.toUpperCase())).collect(Collectors.toList());
		model.addAttribute("productos", productos);
		return "administrador/home";
	}
}
