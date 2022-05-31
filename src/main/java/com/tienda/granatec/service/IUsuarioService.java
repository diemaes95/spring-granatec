package com.tienda.granatec.service;

import java.util.List;
import java.util.Optional;

import com.tienda.granatec.model.Usuario;

public interface IUsuarioService {
	List<Usuario> findAll();
	Optional <Usuario> findById(Integer Id);
	Usuario save (Usuario usuario);
	Optional<Usuario> findByEmail(String email);

}
