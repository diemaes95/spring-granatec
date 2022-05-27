package com.tienda.granatec.service;

import java.util.Optional;

import com.tienda.granatec.model.Usuario;

public interface IUsuarioService {
	Optional <Usuario> findById(Integer Id);
	Usuario save (Usuario usuario);

}
