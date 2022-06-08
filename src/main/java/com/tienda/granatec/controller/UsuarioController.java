package com.tienda.granatec.controller;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.tienda.granatec.model.Orden;
import com.tienda.granatec.model.Usuario;
import com.tienda.granatec.service.IOrdenService;
import com.tienda.granatec.service.IUsuarioService;

@Controller
@RequestMapping("/usuario")
public class UsuarioController {
	// se le apunta a la interfaz de IUsuarioService pero realmente se usa su
	// implementacion UsuarioServiceImpl

	private final Logger logger = LoggerFactory.getLogger(UsuarioController.class);

	@Autowired
	private IUsuarioService usuarioService;
	
	@Autowired
	private IOrdenService ordenService;
	
	BCryptPasswordEncoder passEncode = new  BCryptPasswordEncoder();

	// usuario/registro
	@GetMapping("/registro")
	public String create() {
		return "/usuario/registro";
	}

	@PostMapping("/save")
	public String save(Usuario usuario) {
		logger.info("Usuario registro ", usuario);
		usuario.setTipo("USER");
		//encriptamos la clave del usuario
		usuario.setPassword(passEncode.encode(usuario.getPassword()));
		usuarioService.save(usuario);
		return "redirect:/";
	}

	@GetMapping("/login")
	public String login() {

		return "usuario/login";
	}

	@PostMapping("/acceder")
	public String acceder(Usuario usuario, HttpSession sesion) {
		logger.info("Accesos: {} ", usuario);
		Optional<Usuario> user = usuarioService.findByEmail(usuario.getEmail());
		//logger.info("Usuario de DB obtenido: {}", user.get());
		
		if(user.isPresent()) {
			sesion.setAttribute("idUsuario", user.get().getId());
			if(user.get().getTipo().equals("ADMIN")) {
				return "redirect:/administrador";
			}else {
				return "redirect:/";
			}
		}else {
			logger.info("El usuario no existe");
		}
		return "redirect:/";
	}
	
	@GetMapping("/compras")
	public String obtenerCompras(Model model, HttpSession sesion) {
		model.addAttribute("sesion", sesion.getAttribute("idUsuario"));
		//como es un optional hay que poner .get() al final para que traiga el usuario , ya que optional valida si es usuario o no 
		Usuario usuario = usuarioService.findById(Integer.parseInt(sesion.getAttribute("idUsuario").toString())).get();
		List<Orden> ordenes = ordenService.findByUsuario(usuario);
		
		model.addAttribute("ordenes", ordenes);
		return "usuario/compras";
	}
	
	@GetMapping("/detalle/{id}")
	public String detalleCompra(@PathVariable Integer id, HttpSession sesion, Model model) {
		logger.info("id del pedido {} ", id);
		Optional<Orden> orden = ordenService.findById(id);
		
		model.addAttribute("detalles", orden.get().getDetalle());
		//Le pasamos la sesion
		model.addAttribute("sesion",sesion.getAttribute("idUsuario"));
		return "usuario/detallecompra";
	}
	
	@GetMapping("/cerrar")
	public String cerrarSesion( HttpSession sesion) {
		sesion.removeAttribute("idUsuario");
		logger.info("se cerró la sesion de usuario");
		return "redirect:/";
	}
	
}
