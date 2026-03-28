package com.agendador.usuario.business;

import com.agendador.usuario.business.converter.UsuarioConverter;
import com.agendador.usuario.business.dto.EnderecoDTO;
import com.agendador.usuario.business.dto.TelefoneDTO;
import com.agendador.usuario.business.dto.UsuarioDTO;
import com.agendador.usuario.infrastructure.entity.Endereco;
import com.agendador.usuario.infrastructure.entity.Telefone;
import com.agendador.usuario.infrastructure.entity.Usuario;
import com.agendador.usuario.infrastructure.exceptions.ConflictException;
import com.agendador.usuario.infrastructure.exceptions.ResourceNotFoundException;
import com.agendador.usuario.infrastructure.repository.EnderecoRepository;
import com.agendador.usuario.infrastructure.repository.TelefoneRepository;
import com.agendador.usuario.infrastructure.repository.UsuarioRepository;
import com.agendador.usuario.infrastructure.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor

public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final UsuarioConverter usuarioConverter;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EnderecoRepository enderecoRepository;
    private final TelefoneRepository telefoneRepository;

    public UsuarioDTO salvaUsuario(UsuarioDTO usuarioDTO){
        emailExiste(usuarioDTO.getEmail());
        usuarioDTO.setSenha(passwordEncoder.encode(usuarioDTO.getSenha()));
        Usuario usuario = usuarioConverter.paraUsuario(usuarioDTO);
        usuario = usuarioRepository.save(usuario);
        return  usuarioConverter.paraUsuarioDTO(usuario);
    }

    public void emailExiste(String email){
        try {
            boolean existe = verificaEmailExistente(email);
            if(existe){
                throw new ConflictException("email ja cadastrado " + email);
            }
        }catch (ConflictException e){
            throw new ConflictException("Email ja cadastrado ", e.getCause());
        }
    }

    public boolean verificaEmailExistente(String email){

        return usuarioRepository.existsByEmail(email);
    }

    public UsuarioDTO buscarUsuarioPorEmail(String email){
        try {
            return usuarioConverter.paraUsuarioDTO(
                    usuarioRepository.findByEmail(email)
                            .orElseThrow(
                        () -> new ResourceNotFoundException(
                             "Email nao encontrado " + email)));
        } catch (ResourceNotFoundException e){
            throw new ResourceNotFoundException("Email nao encontrado " + email);
        }
    }
    public void deletaUsuarioPorEmail(String email){
        usuarioRepository.deleteByEmail(email);
    }
    public UsuarioDTO atualizaDadosUsuario(String token, UsuarioDTO dto){
       // Aqui buscamos o email do usuario atraves do token
        String email = jwtUtil.extractUsername(token.substring(7));

        //criptografia na nova senha (caso alterada)
        dto.setSenha(dto.getSenha() != null ? passwordEncoder.encode(dto.getSenha()) : null);

        //Busca os dados do usuario no banco de dados
        Usuario usuarioEntity = usuarioRepository.findByEmail(email).orElseThrow(
                () -> new ResourceNotFoundException("Email nao encontrado " + email));

        //mesclou os dados que recebemos na requisicao DTO com os dados do banco de dados
        Usuario usuario = usuarioConverter.updateUsuario(dto, usuarioEntity);

        //salvou os dados do usuario convertido e depois pegou o retorno e converteu para usuarioDTO
        return usuarioConverter.paraUsuarioDTO(usuarioRepository.save(usuario));
    }

    public EnderecoDTO atualizaEndereco(Long idEndereco, EnderecoDTO enderecoDTO){

        Endereco entity = enderecoRepository.findById(idEndereco).orElseThrow(() ->
                new ResourceNotFoundException("Id nao encontrado " + idEndereco));

        Endereco endereco = usuarioConverter.updateEndereco(enderecoDTO, entity);
        return usuarioConverter.paraEnderecoDTO(enderecoRepository.save(endereco)
        );
    }

    public TelefoneDTO atualizaTelefone(Long idTelefone, TelefoneDTO telefoneDTO){

        Telefone entity = telefoneRepository.findById(idTelefone).orElseThrow(() ->
                new ResourceNotFoundException("Id nao encontrado " + idTelefone));

        Telefone telefone = usuarioConverter.updateTelefone(telefoneDTO, entity);
        return usuarioConverter.paraTelefoneDTO(telefoneRepository.save(telefone));
    }

}
