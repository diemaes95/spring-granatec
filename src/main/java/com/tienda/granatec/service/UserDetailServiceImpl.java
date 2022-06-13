package com.tienda.granatec.service;

import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.tienda.granatec.model.Usuario;

@Service
public class UserDetailServiceImpl implements UserDetailsService {
	@Autowired
	private IUsuarioService usuarioService;
	
	//atributo para encriptar datos en la base de datos
	@Autowired
	private BCryptPasswordEncoder bCrypt;
	
	@Autowired
	HttpSession sesion;
	
	private Logger logg = LoggerFactory.getLogger(UserDetailsService.class);
	
	//este metodo carga el usuario a traves de userName
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<Usuario> optionalUser = usuarioService.findByEmail(username);
		if (optionalUser.isPresent()) {
			logg.info("Esta es la id de el usuario {}", optionalUser.get().getId());
			sesion.setAttribute("idUsuario", optionalUser.get().getId());
			Usuario usuario = optionalUser.get();
			//para que pueda ser un objeto Userdetails se retorna con build()
			return User.builder()
					.username(usuario.getNombre())
					.password(usuario.getPassword())  //para encriptar contraseña  añadir (bCrypt.encode(usuario.getPassword()), en lugar de usuario.getPassword()
					.roles(usuario.getTipo())
					.build();
		}else {
			 	throw new UsernameNotFoundException("Usuario no encontrado");
		}
		
		
	}

}
