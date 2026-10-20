package com.example.solid.before;

public class Boleto extends MeioDePagamento {
    private final String codigoBarras;

    public Boleto(String codigoBarras) {
        this.codigoBarras = codigoBarras;
    }

    public String getCodigoBarras() {
        return codigoBarras;
    }

    @Override
    public void pagar(double valor) {
        // Simula geração de boleto
        System.out.println("Gerando boleto de R$ " + valor + " (código: " + codigoBarras + ")");
    }

    @Override
    public void estornar(double valor) {
        // VIOLA LSP: Boleto não suporta estorno automático
        // A abstração promete estornar, mas esta implementação falha
        throw new UnsupportedOperationException("Boleto não suporta estorno automático");
    }
}