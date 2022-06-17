package com.tienda.granatec.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

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

import com.tienda.granatec.model.DetalleOrden;
import com.tienda.granatec.model.Orden;
import com.tienda.granatec.model.Producto;
import com.tienda.granatec.model.Usuario;
import com.tienda.granatec.service.IDetalleOrdenService;
import com.tienda.granatec.service.IOrdenService;
import com.tienda.granatec.service.IProductoService;
import com.tienda.granatec.service.IUsuarioService;
import com.tienda.granatec.service.UsuarioServiceImpl;

/*Esta clase tendra la logica para mostrar los productos al usuario*/
@Controller
@RequestMapping("/")
public class HomeController {

	private final Logger LOGGER = LoggerFactory.getLogger(HomeController.class);

	@Autowired
	private IProductoService productoService;// nos permitira obntener los productos

	@Autowired
	private IUsuarioService usuarioService;

	@Autowired
	private IOrdenService ordenService;

	@Autowired
	IDetalleOrdenService detalleOrdenService;

	// almacenaje detalles de pedido/orden
	List<DetalleOrden> detalles = new ArrayList<DetalleOrden>();

	// datos de la pedido/orden
	Orden orden = new Orden();

	// Método para redirigir a la home del usuario
	@GetMapping("")
	public String home(Model model, HttpSession sesion) {
		model.addAttribute("productos", productoService.findAll());// aqui trae todos los productos con findAll()
		LOGGER.info("sesion del usuario: {}", sesion.getAttribute("idUsuario"));
		// sesion
		model.addAttribute("sesion", sesion.getAttribute("idUsuario"));
		return "usuario/home";
	}

	// Método para redirigir a la vista de producto para añadir al carrito
	@GetMapping("productoHome/{id}")
	public String productoHome(@PathVariable Integer id, Model model,HttpSession sesion ) {
		model.addAttribute("sesion", sesion.getAttribute("idUsuario"));
		LOGGER.info("id producto enviado como parametro {}", id);
		Producto producto = new Producto();
		Optional<Producto> productoOptional = productoService.get(id); // Nos devuelve el producto segun su id
		producto = productoOptional.get();
		model.addAttribute("producto", producto);
		return "usuario/productoHome";
	}

	// Método que nos redirige al carrito y añade la compra
	@PostMapping("/cart")
	public String addCart(@RequestParam Integer id, @RequestParam Integer cantidad, Model model,HttpSession sesion ) {
		model.addAttribute("sesion", sesion.getAttribute("idUsuario"));
		DetalleOrden detalleOrden = new DetalleOrden();
		Producto producto = new Producto();
		double sumaTotal = 0;
		Optional<Producto> optionalProducto = productoService.get(id);
		LOGGER.info("Producto añadido: {}", optionalProducto.get());
		LOGGER.info("Cantidad: {}", cantidad);
		producto = optionalProducto.get();
		detalleOrden.setCantidad(cantidad);
		detalleOrden.setPrecio(producto.getCantidad());
		detalleOrden.setNombre(producto.getNombre());
		detalleOrden.setTotal(producto.getPrecio() * cantidad);
		detalleOrden.setProducto(producto);

		// validar que el producto no se añade dos veces en la misma lista
		Integer idProducto = producto.getId();
		boolean ingresado = detalles.stream().anyMatch(x -> x.getProducto().getId() == idProducto);
		if (!ingresado) {
			detalles.add(detalleOrden);
		}

		// mediante programacion funcional sumamos todos los totales de los productos
		sumaTotal = detalles.stream().mapToDouble(dt -> dt.getTotal()).sum();

		orden.setTotal(sumaTotal);
		model.addAttribute("cart", detalles);
		model.addAttribute("orden", orden);
		return "usuario/carrito";
	}

	// Método para quitar un producto del carrito.
	@GetMapping("/delete/cart/{id}")
	public String deleteProductoCart(@PathVariable Integer id, Model model, HttpSession sesion ) {
		model.addAttribute("sesion", sesion.getAttribute("idUsuario"));
		// lista nueva de productos
		List<DetalleOrden> ordenesNueva = new ArrayList<DetalleOrden>();

		// los añadira todos menos el id que se le pasa
		for (DetalleOrden detalleOrden : detalles) {
			if (detalleOrden.getProducto().getId() != id) {
				ordenesNueva.add(detalleOrden);
			}
		}

		// actualizar la lista con los productos restantes al seleccionado
		detalles = ordenesNueva;

		double sumaTotal = 0;
		sumaTotal = detalles.stream().mapToDouble(dt -> dt.getTotal()).sum();

		orden.setTotal(sumaTotal);
		model.addAttribute("cart", detalles);
		model.addAttribute("orden", orden);

		return "usuario/carrito";
	}

	// metodo para mapear hacia carrito
	@GetMapping("/getCart")
	public String getCart(Model model, HttpSession sesion) {

		// para que se quede siempre activo el carrito de esta manera sobreviven los
		// datos del crud de carrito
		model.addAttribute("cart", detalles);
		model.addAttribute("orden", orden);
		model.addAttribute("sesion", sesion.getAttribute("idUsuario"));
		return "/usuario/carrito";
	}

	// Método para ver el resumen del pedido
	@GetMapping("/order")
	public String order(Model model, HttpSession sesion) {
		// Obtenemos el usuario mediante la sesion
		Usuario usuario = usuarioService.findById(Integer.parseInt(sesion.getAttribute("idUsuario").toString())).get();

		model.addAttribute("cart", detalles);
		model.addAttribute("orden", orden);
		model.addAttribute("usuario", usuario);
		return "usuario/resumenorden";
	}

	// Método para guardar/generar el pedido
	@GetMapping("/saveOrder")
	public String saveOrder(HttpSession sesion) {
		Date fechaCreacion = new Date();
		orden.setFechaCreacion(fechaCreacion);
		orden.setNumero(ordenService.generarNumeroOrden());

		// usuario
		// Obtenemos el usuario mediante la sesion
		Usuario usuario = usuarioService.findById(Integer.parseInt(sesion.getAttribute("idUsuario").toString())).get();
		orden.setUsuario(usuario);
		// guardar datos del pedido / orden

		ordenService.save(orden);

		// guardar detalles del pedido

		for (DetalleOrden dt : detalles) {
			dt.setOrden(orden);
			detalleOrdenService.save(dt);
		}

		// si el usuario quiere seguir comprando se le hara un limpiado a la lista de
		// pedido
		orden = new Orden();
		detalles.clear();

		// nos redirigira al home
		return "redirect:/";
	}

	// Método para buscar productos.
	@PostMapping("/buscar")
	public String searchProducto(@RequestParam String nombre, Model model, HttpSession sesion) {
		model.addAttribute("sesion", sesion.getAttribute("idUsuario"));
		LOGGER.info("Nombre del producto {}", nombre);
		// Devuelve una lista con los productos que contengan los caracteres buscados
		List<Producto> productos = productoService.findAll().stream()
				.filter(x -> x.getNombre().toUpperCase().contains(nombre.toUpperCase())).collect(Collectors.toList());
		model.addAttribute("productos", productos);
		return "usuario/home";
	}

	// Método para visualizar neveras.
	@GetMapping("/buscarNevera")
	public String searchNevera(Model model, HttpSession sesion) {
		model.addAttribute("sesion", sesion.getAttribute("idUsuario"));
		String nombre = "Frigorifico";
		LOGGER.info("Nombre del producto {}", nombre);
		// Devuelve una lista con los productos que contengan los caracteres buscados
		List<Producto> productos = productoService.findAll().stream()
				.filter(x -> x.getNombre().toUpperCase().contains(nombre.toUpperCase())).collect(Collectors.toList());
		model.addAttribute("productos", productos);
		return "usuario/home";
	}

	// Método para visualizar secadoras.
	@GetMapping("/buscarSecadora")
	public String searchSecadora(Model model, HttpSession sesion) {
		model.addAttribute("sesion", sesion.getAttribute("idUsuario"));
		String nombre = "Secadora";
		LOGGER.info("Nombre del producto {}", nombre);
		// Devuelve una lista con los productos que contengan los caracteres buscados
		List<Producto> productos = productoService.findAll().stream()
				.filter(x -> x.getNombre().toUpperCase().contains(nombre.toUpperCase())).collect(Collectors.toList());
		model.addAttribute("productos", productos);
		return "usuario/home";
	}

	// Método para visualizar lavadoras.
	@GetMapping("/buscarLavadora")
	public String searchLavadora(Model model, HttpSession sesion) {
		model.addAttribute("sesion", sesion.getAttribute("idUsuario"));
		String nombre = "Lavadora";
		LOGGER.info("Nombre del producto {}", nombre);
		// Devuelve una lista con los productos que contengan los caracteres buscados
		List<Producto> productos = productoService.findAll().stream()
				.filter(x -> x.getNombre().toUpperCase().contains(nombre.toUpperCase())).collect(Collectors.toList());
		model.addAttribute("productos", productos);
		return "usuario/home";
	}

	// Método para visualizar Altavoces.
	@GetMapping("/buscarAltavoz")
	public String searchAltavoz(Model model, HttpSession sesion) {
		model.addAttribute("sesion", sesion.getAttribute("idUsuario"));
		String nombre = "Altavoz";
		LOGGER.info("Nombre del producto {}", nombre);
		// Devuelve una lista con los productos que contengan los caracteres buscados
		List<Producto> productos = productoService.findAll().stream()
				.filter(x -> x.getNombre().toUpperCase().contains(nombre.toUpperCase())).collect(Collectors.toList());
		model.addAttribute("productos", productos);
		
			return "usuario/home";
		
		
	}
}
