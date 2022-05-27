package com.tienda.granatec.controller;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.tienda.granatec.model.Producto;
import com.tienda.granatec.model.Usuario;
import com.tienda.granatec.service.IProductoService;
import com.tienda.granatec.service.IUsuarioService;
import com.tienda.granatec.service.UploadFileService;
import com.tienda.granatec.service.UsuarioServiceImpl;

@Controller
@RequestMapping("/productos")
public class ProductoController {

	private final Logger LOGGER = LoggerFactory.getLogger(ProductoController.class);

	@Autowired
	private IProductoService productoService;

	@Autowired
	private UploadFileService upload;
	
	@Autowired
	private IUsuarioService usuarioService;

	@GetMapping("")
	public String show(Model model) {
		model.addAttribute("productos", productoService.findAll());// lleva datos desde el backend a frontend
		return "productos/show";
	}

	@GetMapping("/create")
	public String create() {
		return "productos/create";
	}

	@PostMapping("/save")
	public String save(Producto producto, @RequestParam("img") MultipartFile file ,HttpSession sesion) throws IOException {// requestParam
																										// trae del
																										// campo img del
																										// formulario la
		// imagen para la variable vile
		LOGGER.info("Este es el objeto producto {}", producto);
		
		Usuario usuario = usuarioService.findById(Integer.parseInt(sesion.getAttribute("idUsuario").toString())).get();
		producto.setUsuario(usuario);

		// imagen
		if (producto.getId() == null) {// esta validacion es para cuando se crea un producto(el id del producto sera
										// null)
			String nombreImagen = upload.saveImage(file);
			producto.setImagen(nombreImagen);// guardamos la imagen en producto
		} 

		productoService.save(producto);
		return "redirect:/productos";
	}

	@GetMapping("/edit/{id}")
	public String edit(@PathVariable Integer id, Model model) {
		Producto producto = new Producto();
		Optional<Producto> optionalProducto = productoService.get(id);// con Optional comprueba is es null
		producto = optionalProducto.get();// le pasamos a producto el id de optional producto
		LOGGER.info("Producto buscado: {}", producto);
		model.addAttribute("producto", producto); // lleva datos desde el backend a frontend
		return "productos/edit";
	}

	@PostMapping("/update")
	public String update(Producto producto , @RequestParam("img") MultipartFile file) throws IOException {
		Producto p = new Producto();
		p = productoService.get(producto.getId()).get();// obtenemos un producto segun por id
		
		
		if (file.isEmpty()) {// cuando se modifica un producto y se carga la misma imagen
		
			producto.setImagen(p.getImagen());
		} else {// cuando se modifica un producto y su imagen también
			 p = new Producto();
			p=productoService.get(producto.getId()).get();
			
			/*Eliminar cuando la imagen no es por defecto */
			if(!p.getImagen().equals("default.jpg")) {
				upload.deleteImage(p.getImagen());
			}
			String nombreImagen = upload.saveImage(file);
			producto.setImagen(nombreImagen);
		}
		producto.setUsuario(p.getUsuario());
		productoService.update(producto);
		return "redirect:/productos";
	}

	@GetMapping("/delete/{id}")
	public String delete(@PathVariable Integer id) {
		
		Producto p = new Producto();
		p=productoService.get(id).get();
		/*Eliminar cuando la imagen no es por defecto */
		if(!p.getImagen().equals("default.jpg")) {
			upload.deleteImage(p.getImagen());
		}
		
		productoService.delete(id);
		return "redirect:/productos";
	}
}
