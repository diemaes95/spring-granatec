package com.tienda.granatec.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tienda.granatec.model.Usuario;
import com.tienda.granatec.repository.UsuarioRepository;

@Service
public class IUsuarioServiceImpl implements IUsuarioService{

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Override
	public Optional<Usuario> findById(Integer Id) {
		// TODO Auto-generated method stub
		return usuarioRepository.findById(Id);
	}

	

	
	


}
