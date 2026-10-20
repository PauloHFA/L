package com.example.solid.after.payment;

public class Cartao implements MeioDePagamento, Estornavel {
    private final String numero;
    private final String titular;

    public Cartao(String numero, String titular) {
        this.numero = numero;
        this.titular = titular;
    }

    public String getNumero() {
        return numero;
    }

    public String getTitular() {
        return titular;
    }

    @Override
    public void pagar(double valor) {
        System.out.println("Cobrando R$ " + valor + " no cartão " + numero.substring(numero.length() - 4) + " (" + titular + ")");
    }

    @Override
    public void estornar(double valor) {
        System.out.println("Estornando R$ " + valor + " no cartão " + numero.substring(numero.length() - 4));
    }
}