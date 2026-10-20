package com.example.solid.after.application;

import com.example.solid.after.domain.Cliente;
import com.example.solid.after.domain.Item;
import com.example.solid.after.domain.Pedido;
import com.example.solid.after.payment.Boleto;
import com.example.solid.after.payment.Cartao;
import com.example.solid.after.payment.Estornavel;
import com.example.solid.after.payment.MeioDePagamento;
import com.example.solid.after.payment.Pix;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CancelarPedidoTest {

    private static Cliente clienteVip() {
        return new Cliente("1", "João VIP", "joao@email.com", true);
    }

    private static List<Item> itensBasicos() {
        return Arrays.asList(
                new Item("Produto A", 100.0, 1),
                new Item("Produto B", 50.0, 2)
        );
    }

    @Test
    void cancelarPedido_comCartao_deveEstornar() {
        Cartao cartao = new Cartao("1234567890123456", "João");
        Pedido pedido = new Pedido(itensBasicos(), clienteVip());
        
        CancelarPedido cancelar = new CancelarPedido();
        cancelar.executar(pedido, cartao, cartao);
    }

    @Test
    void cancelarPedido_comPix_deveEstornar() {
        Pix pix = new Pix("joao@email.com");
        Pedido pedido = new Pedido(itensBasicos(), clienteVip());
        
        CancelarPedido cancelar = new CancelarPedido();
        cancelar.executar(pedido, pix, pix);
    }

    @Test
    void cancelarPedido_comBoleto_deveUsarFluxoAlternativo() {
        Boleto boleto = new Boleto("12345678901234567890123456789012345678901234");
        Pedido pedido = new Pedido(itensBasicos(), clienteVip());
        
        CancelarPedido cancelar = new CancelarPedido();
        cancelar.executar(pedido, boleto, null);
    }
}