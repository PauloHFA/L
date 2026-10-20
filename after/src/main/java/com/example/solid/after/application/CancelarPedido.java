package com.example.solid.after.application;

import com.example.solid.after.domain.Pedido;
import com.example.solid.after.payment.Estornavel;
import com.example.solid.after.payment.MeioDePagamento;

public class CancelarPedido {
    public void executar(Pedido pedido, MeioDePagamento pagamento, Estornavel estorno) {
        double total = pedido.calcularTotal();
        
        // O cliente decide pelo contrato (Estornavel), não pela classe concreta
        // Não há instanceof, não há if (tipo === ...)
        if (estorno != null) {
            estorno.estornar(total);
            System.out.println("Estorno processado com sucesso. Total: R$ " + total);
        } else {
            // Fluxo de cancelamento sem estorno automático (ex.: crédito na loja)
            System.out.println("Pagamento não estornável detectado: cancelamento sem estorno automático. Total: R$ " + total);
            System.out.println("Gerando crédito na loja para o cliente: " + pedido.getCliente().getNome());
        }
    }
}