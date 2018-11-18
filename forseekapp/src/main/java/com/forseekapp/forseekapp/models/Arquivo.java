package com.forseekapp.forseekapp.models;

public class Arquivo {

    private String nome;
    private String caminho;

    public Arquivo(String nome, String caminho) {
        this.nome = nome;
        this.caminho = caminho;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCaminho() {
        return caminho;
    }

    public void setCaminho(String caminho) {
        this.caminho = caminho;
    }
}
