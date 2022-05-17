package com.tienda.granatec.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/*Es una clase enfocada para la carga y eliminacion de archivos como imagenes*/
@Service
public class UploadFileService {
	private String folder="images//";
	
	public String saveImage(MultipartFile file) throws IOException {
		if(!file.isEmpty()) {//si el usuario sube una imgen 
			//pasamos la imagen a bytes para poder enviar desde dcliente a servidor 
			byte [] bytes = file.getBytes();
			Path path = Paths.get(folder+file.getOriginalFilename());//pasamos la uri donde queremos que se almacene nuestra imagen
			Files.write(path, bytes);//envia al servidor 
			return file.getOriginalFilename();
		}
		//si el usuario no envia una imagen  cargará una  por defecto 
		return "default.jpg";
		
	}
	//el siguiente metodo elimina una imagen cuando eliminemos un producto
	public void delete(String nombre) {
		String ruta = "images//";
		File file = new File(ruta+nombre);
		file.delete();
	}
	
}
