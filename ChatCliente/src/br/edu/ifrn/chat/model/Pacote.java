package br.edu.ifrn.chat.model;

import br.edu.ifrn.chat.enumarator.Comando;
import java.io.*;
import java.util.*;

public class Pacote implements Serializable {
    
    private String usuario;
    private String usuarioReservado;
    private Set<String> usuarioOnLine;
    private String mensagem;    
    private Comando acao;    

    public Pacote() {
        this.usuarioOnLine = new HashSet<>();
    }    

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getUsuarioReservado() {
        return usuarioReservado;
    }

    public void setUsuarioReservado(String usuarioReservado) {
        this.usuarioReservado = usuarioReservado;
    }

    public Set<String> getUsuarioOnLine() {
        return usuarioOnLine;
    }

    public void setUsuarioOnLine(Set<String> usuarioOnLine) {
        this.usuarioOnLine = usuarioOnLine;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public Comando getAcao() {
        return acao;
    }

    public void setAcao(Comando acao) {
        this.acao = acao;
    }
    
}
