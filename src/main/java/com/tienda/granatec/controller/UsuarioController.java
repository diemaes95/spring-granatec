package com.tienda.granatec.controller;

import java.util.Optional;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionIdListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.tienda.granatec.model.Usuario;
import com.tienda.granatec.service.IUsuarioService;

@Controller
@RequestMapping("/usuario")
public class UsuarioController {
	// se le apunta a la interfaz de IUsuarioService pero realmente se usa su
	// implementacion UsuarioServiceImpl

	private final Logger logger = LoggerFactory.getLogger(UsuarioController.class);

	@Autowired
	private IUsuarioService usuarioService;

	// usuario/registro
	@GetMapping("/registro")
	public String create() {
		return "/usuario/registro";
	}

	@PostMapping("/save")
	public String save(Usuario usuario) {
		logger.info("Usuario registro ", usuario);
		usuario.setTipo("USER");
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
}
