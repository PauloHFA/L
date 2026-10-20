package com.example.solid.before;

import java.util.List;

public class Pedido {
    private final List<Item> itens;
    private final Cliente cliente;
    private final MeioDePagamento pagamento;

    public Pedido(List<Item> itens, Cliente cliente, MeioDePagamento pagamento) {
        this.itens = itens;
        this.cliente = cliente;
        this.pagamento = pagamento;
    }

    public List<Item> getItens() {
        return itens;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public MeioDePagamento getPagamento() {
        return pagamento;
    }

    public double calcularSubtotal() {
        return itens.stream()
                .mapToDouble(Item::getSubtotal)
                .sum();
    }

    public double calcularTotal() {
        double subtotal = calcularSubtotal();
        return cliente.isVip() ? subtotal * 0.9 : subtotal;
    }
}