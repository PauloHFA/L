package com.example.solid.after.payment;

public class Boleto implements MeioDePagamento {
    private final String codigoBarras;

    public Boleto(String codigoBarras) {
        this.codigoBarras = codigoBarras;
    }

    public String getCodigoBarras() {
        return codigoBarras;
    }

    @Override
    public void pagar(double valor) {
        System.out.println("Gerando boleto de R$ " + valor + " (código: " + codigoBarras + ")");
    }
}