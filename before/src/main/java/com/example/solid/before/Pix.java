package com.example.solid.before;

public class Pix extends MeioDePagamento {
    private final String chave;

    public Pix(String chave) {
        this.chave = chave;
    }

    public String getChave() {
        return chave;
    }

    @Override
    public void pagar(double valor) {
        // Simula pagamento via Pix
        System.out.println("Pagando R$ " + valor + " via Pix (chave: " + chave + ")");
    }

    @Override
    public void estornar(double valor) {
        // Simula devolução via Pix
        System.out.println("Devolvendo R$ " + valor + " via Pix (chave: " + chave + ")");
    }
}