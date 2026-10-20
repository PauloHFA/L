package com.example.solid.before;

import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== SOLID #3 - LSP: Versão BEFORE (Viola LSP) ===\n");

        Cliente cliente = new Cliente("1", "João Silva", "joao@email.com", true);
        List<Item> itens = Arrays.asList(
                new Item("Notebook", 3000.0, 1),
                new Item("Mouse", 150.0, 2)
        );

        // Teste com Cartão
        System.out.println("--- Pagamento com Cartão ---");
        MeioDePagamento cartao = new Cartao("1234567890123456", "João Silva");
        Pedido pedidoCartao = new Pedido(itens, cliente, cartao);
        System.out.println("Total: R$ " + pedidoCartao.calcularTotal());
        cartao.pagar(pedidoCartao.calcularTotal());
        
        System.out.println("\n--- Cancelamento com Cartão ---");
        new CancelarPedido().executar(pedidoCartao);

        // Teste com Pix
        System.out.println("\n--- Pagamento com Pix ---");
        MeioDePagamento pix = new Pix("joao@email.com");
        Pedido pedidoPix = new Pedido(itens, cliente, pix);
        System.out.println("Total: R$ " + pedidoPix.calcularTotal());
        pix.pagar(pedidoPix.calcularTotal());
        
        System.out.println("\n--- Cancelamento com Pix ---");
        new CancelarPedido().executar(pedidoPix);

        // Teste com Boleto - VIOLA LSP
        System.out.println("\n--- Pagamento com Boleto ---");
        MeioDePagamento boleto = new Boleto("12345678901234567890123456789012345678901234");
        Pedido pedidoBoleto = new Pedido(itens, cliente, boleto);
        System.out.println("Total: R$ " + pedidoBoleto.calcularTotal());
        boleto.pagar(pedidoBoleto.calcularTotal());
        
        System.out.println("\n--- Cancelamento com Boleto (VIOLA LSP) ---");
        try {
            new CancelarPedido().executar(pedidoBoleto);
        } catch (UnsupportedOperationException e) {
            System.out.println("ERRO: " + e.getMessage());
            System.out.println("O código cliente precisou fazer instanceof Boleto para evitar este erro!");
        }

        System.out.println("\n=== FIM ===");
    }
}