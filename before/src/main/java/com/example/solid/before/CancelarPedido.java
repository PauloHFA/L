package com.example.solid.before;

public class CancelarPedido {
    public void executar(Pedido pedido) {
        double total = pedido.calcularTotal();
        
        // O cliente precisa conhecer o tipo concreto para não quebrar
        // Isso viola LSP e também OCP: cada novo meio de pagamento obriga a revisitar este código
        if (pedido.getPagamento() instanceof Boleto) {
            System.out.println("Boleto detectado: cancelamento sem estorno automático. Total: R$ " + total);
            System.out.println("Gerando crédito na loja para o cliente: " + pedido.getCliente().getNome());
        } else {
            pedido.getPagamento().estornar(total);
            System.out.println("Estorno processado com sucesso. Total: R$ " + total);
        }
    }
}