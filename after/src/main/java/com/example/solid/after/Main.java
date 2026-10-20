package com.example.solid.after;

import com.example.solid.after.application.CancelarPedido;
import com.example.solid.after.domain.Cliente;
import com.example.solid.after.domain.Item;
import com.example.solid.after.domain.Pedido;
import com.example.solid.after.payment.Boleto;
import com.example.solid.after.payment.Cartao;
import com.example.solid.after.payment.Estornavel;
import com.example.solid.after.payment.MeioDePagamento;
import com.example.solid.after.payment.Pix;

import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== SOLID #3 - LSP: Versão AFTER (Respeita LSP) ===\n");

        Cliente cliente = new Cliente("1", "João Silva", "joao@email.com", true);
        List<Item> itens = Arrays.asList(
                new Item("Notebook", 3000.0, 1),
                new Item("Mouse", 150.0, 2)
        );

        // Teste com Cartão
        System.out.println("--- Pagamento com Cartão ---");
        MeioDePagamento cartao = new Cartao("1234567890123456", "João Silva");
        Pedido pedidoCartao = new Pedido(itens, cliente);
        System.out.println("Total: R$ " + pedidoCartao.calcularTotal());
        cartao.pagar(pedidoCartao.calcularTotal());
        
        System.out.println("\n--- Cancelamento com Cartão ---");
        new CancelarPedido().executar(pedidoCartao, cartao, (Estornavel) cartao);

        // Teste com Pix
        System.out.println("\n--- Pagamento com Pix ---");
        MeioDePagamento pix = new Pix("joao@email.com");
        Pedido pedidoPix = new Pedido(itens, cliente);
        System.out.println("Total: R$ " + pedidoPix.calcularTotal());
        pix.pagar(pedidoPix.calcularTotal());
        
        System.out.println("\n--- Cancelamento com Pix ---");
        new CancelarPedido().executar(pedidoPix, pix, (Estornavel) pix);

        // Teste com Boleto - RESPEITA LSP
        System.out.println("\n--- Pagamento com Boleto ---");
        MeioDePagamento boleto = new Boleto("12345678901234567890123456789012345678901234");
        Pedido pedidoBoleto = new Pedido(itens, cliente);
        System.out.println("Total: R$ " + pedidoBoleto.calcularTotal());
        boleto.pagar(pedidoBoleto.calcularTotal());
        
        System.out.println("\n--- Cancelamento com Boleto (RESPEITA LSP) ---");
        // Boleto não implementa Estornavel, então passa null
        new CancelarPedido().executar(pedidoBoleto, boleto, null);

        System.out.println("\n=== FIM ===");
    }
}