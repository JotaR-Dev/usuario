package com.agendador.usuario.business;

import com.agendador.usuario.business.converter.UsuarioConverter;
import com.agendador.usuario.business.dto.UsuarioDTO;
import com.agendador.usuario.infrastructure.entity.Usuario;
import com.agendador.usuario.infrastructure.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor

public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final UsuarioConverter usuarioConverter;

    public UsuarioDTO salvaUsuario(UsuarioDTO usuarioDTO){
        Usuario usuario = usuarioConverter.paraUsuario(usuarioDTO);
        usuario = usuarioRepository.save(usuario);
        return  usuarioConverter.paraUsuarioDTO(usuario);
    }

}
