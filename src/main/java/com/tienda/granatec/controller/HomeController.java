package com.tienda.granatec.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
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
import com.tienda.granatec.repository.IDetalleOrdenRepository;
import com.tienda.granatec.service.IUsuarioService;
import com.tienda.granatec.service.IDetalleOrdenService;
import com.tienda.granatec.service.IOrdenService;
import com.tienda.granatec.service.IProductoService;

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

	@GetMapping("")
	public String home(Model model, HttpSession sesion) {
		model.addAttribute("productos", productoService.findAll());// aqui trae todos los productos con findAll()
		LOGGER.info("sesion del usuario: {}", sesion.getAttribute("idUsuario"));
		return "usuario/home";
	}

	@GetMapping("productoHome/{id}")
	public String productoHome(@PathVariable Integer id, Model model) {
		LOGGER.info("id producto enviado como parametro {}", id);
		Producto producto = new Producto();
		Optional<Producto> productoOptional = productoService.get(id); // Nos devuelve el producto segun su id
		producto = productoOptional.get();
		model.addAttribute("producto", producto);
		return "usuario/productoHome";
	}

	@PostMapping("/cart")
	public String addCart(@RequestParam Integer id, @RequestParam Integer cantidad, Model model) {
		DetalleOrden detalleOrden = new DetalleOrden();
		Producto producto = new Producto();
		double sumaTotal = 0; // inicializamos a cero para que no se quede con el ultimo valor despues de una
								// ejecucion
		Optional<Producto> optionalProducto = productoService.get(id);
		// se comprueba mediante logs que se recibe el producto y su dicha cantidad
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

	// quitar un producto del carrito
	@GetMapping("/delete/cart/{id}")
	public String deleteProductoCart(@PathVariable Integer id, Model model) {
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
	public String getCart(Model model) {

		// para que se quede siempre activo el carrito de esta manera sobreviven los
		// datos del crud de carrito
		model.addAttribute("cart", detalles);
		model.addAttribute("orden", orden);
		return "/usuario/carrito";
	}

	@GetMapping("/order")
	public String order(Model model,HttpSession sesion) {
		// Obtenemos el usuario mediante la sesion
		Usuario usuario = usuarioService.findById(Integer.parseInt(sesion.getAttribute("idUsuario").toString())).get();

		model.addAttribute("cart", detalles);
		model.addAttribute("orden", orden);
		model.addAttribute("usuario", usuario);
		return "usuario/resumenorden";
	}

	// Método para guardar el pedido
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

	@PostMapping("/buscar")
	public String searchProducto(@RequestParam String nombre, Model model) {
		LOGGER.info("Nombre del producto {}", nombre);
		//Devuelve una lista con los productos que contengan los caracteres buscados
		List<Producto> productos = productoService.findAll().stream()
				.filter(x -> x.getNombre().toUpperCase().contains(nombre.toUpperCase())).collect(Collectors.toList());
		model.addAttribute("productos", productos);
		return "usuario/home";
	}

}
