package com.example.solid.before;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PedidoTest {

    private static Cliente clienteVip() {
        return new Cliente("1", "João VIP", "joao@email.com", true);
    }

    private static Cliente clienteComum() {
        return new Cliente("2", "Maria Comum", "maria@email.com", false);
    }

    private static List<Item> itensBasicos() {
        return Arrays.asList(
                new Item("Produto A", 100.0, 1),
                new Item("Produto B", 50.0, 2)
        );
    }

    @Test
    void calcularTotal_comClienteVip_deveAplicarDesconto10Porcento() {
        MeioDePagamento pagamento = new Cartao("1234567890123456", "João");
        Pedido pedido = new Pedido(itensBasicos(), clienteVip(), pagamento);

        double total = pedido.calcularTotal();

        assertThat(total).isEqualTo(180.0);
    }

    @Test
    void calcularTotal_comClienteNaoVip_deveNaoAplicarDesconto() {
        MeioDePagamento pagamento = new Cartao("1234567890123456", "Maria");
        Pedido pedido = new Pedido(itensBasicos(), clienteComum(), pagamento);

        double total = pedido.calcularTotal();

        assertThat(total).isEqualTo(200.0);
    }

    @Test
    void calcularTotal_comListaVazia_deveRetornarZero() {
        MeioDePagamento pagamento = new Cartao("1234567890123456", "Teste");
        Pedido pedido = new Pedido(Arrays.asList(), clienteComum(), pagamento);

        double total = pedido.calcularTotal();

        assertThat(total).isEqualTo(0.0);
    }
}