package com.example.solid.after.payment;

public class Pix implements MeioDePagamento, Estornavel {
    private final String chave;

    public Pix(String chave) {
        this.chave = chave;
    }

    public String getChave() {
        return chave;
    }

    @Override
    public void pagar(double valor) {
        System.out.println("Pagando R$ " + valor + " via Pix (chave: " + chave + ")");
    }

    @Override
    public void estornar(double valor) {
        System.out.println("Devolvendo R$ " + valor + " via Pix (chave: " + chave + ")");
    }
}