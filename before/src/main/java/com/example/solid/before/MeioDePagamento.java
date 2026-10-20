package com.example.solid.before;

public abstract class MeioDePagamento {
    public abstract void pagar(double valor);
    public abstract void estornar(double valor);
}