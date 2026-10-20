package com.example.solid.acceptance;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Testes de aceitação que verificam se ambas as versões (before e after)
 * produzem o mesmo comportamento externo para operações que funcionam.
 * 
 * Para a versão BEFORE, o teste de cancelamento com Boleto espera exceção.
 * Para a versão AFTER, o cancelamento com Boleto funciona sem exceção (fluxo alternativo).
 */
class PedidoAcceptanceTest {

    enum Versao {
        BEFORE,
        AFTER
    }

    // ==================== VERSÃO BEFORE ====================

    private static com.example.solid.before.Cliente clienteVipBefore() {
        return new com.example.solid.before.Cliente("1", "João VIP", "joao@email.com", true);
    }

    private static com.example.solid.before.Cliente clienteComumBefore() {
        return new com.example.solid.before.Cliente("2", "Maria Comum", "maria@email.com", false);
    }

    private static List<com.example.solid.before.Item> itensBasicosBefore() {
        return Arrays.asList(
                new com.example.solid.before.Item("Produto A", 100.0, 1),
                new com.example.solid.before.Item("Produto B", 50.0, 2)
        );
    }

    // ==================== VERSÃO AFTER ====================

    private static com.example.solid.after.domain.Cliente clienteVipAfter() {
        return new com.example.solid.after.domain.Cliente("1", "João VIP", "joao@email.com", true);
    }

    private static com.example.solid.after.domain.Cliente clienteComumAfter() {
        return new com.example.solid.after.domain.Cliente("2", "Maria Comum", "maria@email.com", false);
    }

    private static List<com.example.solid.after.domain.Item> itensBasicosAfter() {
        return Arrays.asList(
                new com.example.solid.after.domain.Item("Produto A", 100.0, 1),
                new com.example.solid.after.domain.Item("Produto B", 50.0, 2)
        );
    }

    // ==================== TESTES COMUNS (FUNCIONAM EM AMBAS) ====================

    @ParameterizedTest(name = "Versão {0}: Calcular total com cliente VIP deve aplicar desconto 10%")
    @EnumSource(Versao.class)
    void calcularTotal_comClienteVip_deveAplicarDesconto10Porcento(Versao versao) {
        if (versao == Versao.BEFORE) {
            com.example.solid.before.Cliente cliente = clienteVipBefore();
            List<com.example.solid.before.Item> itens = itensBasicosBefore();
            com.example.solid.before.MeioDePagamento pagamento = new com.example.solid.before.Cartao("1234567890123456", "João");
            com.example.solid.before.Pedido pedido = new com.example.solid.before.Pedido(itens, cliente, pagamento);
            
            double total = pedido.calcularTotal();
            assertThat(total).isEqualTo(180.0);
        } else {
            com.example.solid.after.domain.Cliente cliente = clienteVipAfter();
            List<com.example.solid.after.domain.Item> itens = itensBasicosAfter();
            com.example.solid.after.domain.Pedido pedido = new com.example.solid.after.domain.Pedido(itens, cliente);
            
            double total = pedido.calcularTotal();
            assertThat(total).isEqualTo(180.0);
        }
    }

    @ParameterizedTest(name = "Versão {0}: Calcular total com cliente não-VIP não deve aplicar desconto")
    @EnumSource(Versao.class)
    void calcularTotal_comClienteNaoVip_deveNaoAplicarDesconto(Versao versao) {
        if (versao == Versao.BEFORE) {
            com.example.solid.before.Cliente cliente = clienteComumBefore();
            List<com.example.solid.before.Item> itens = itensBasicosBefore();
            com.example.solid.before.MeioDePagamento pagamento = new com.example.solid.before.Cartao("1234567890123456", "Maria");
            com.example.solid.before.Pedido pedido = new com.example.solid.before.Pedido(itens, cliente, pagamento);
            
            double total = pedido.calcularTotal();
            assertThat(total).isEqualTo(200.0);
        } else {
            com.example.solid.after.domain.Cliente cliente = clienteComumAfter();
            List<com.example.solid.after.domain.Item> itens = itensBasicosAfter();
            com.example.solid.after.domain.Pedido pedido = new com.example.solid.after.domain.Pedido(itens, cliente);
            
            double total = pedido.calcularTotal();
            assertThat(total).isEqualTo(200.0);
        }
    }

    @ParameterizedTest(name = "Versão {0}: Calcular total com lista vazia deve retornar zero")
    @EnumSource(Versao.class)
    void calcularTotal_comListaVazia_deveRetornarZero(Versao versao) {
        if (versao == Versao.BEFORE) {
            com.example.solid.before.Cliente cliente = clienteComumBefore();
            com.example.solid.before.MeioDePagamento pagamento = new com.example.solid.before.Cartao("1234567890123456", "Teste");
            com.example.solid.before.Pedido pedido = new com.example.solid.before.Pedido(Arrays.asList(), cliente, pagamento);
            
            double total = pedido.calcularTotal();
            assertThat(total).isEqualTo(0.0);
        } else {
            com.example.solid.after.domain.Cliente cliente = clienteComumAfter();
            com.example.solid.after.domain.Pedido pedido = new com.example.solid.after.domain.Pedido(Arrays.asList(), cliente);
            
            double total = pedido.calcularTotal();
            assertThat(total).isEqualTo(0.0);
        }
    }

    // ==================== TESTES DE PAGAMENTO (FUNCIONAM EM AMBAS) ====================

    @ParameterizedTest(name = "Versão {0}: Cartão deve processar pagamento")
    @EnumSource(Versao.class)
    void cartao_deveProcessarPagamento(Versao versao) {
        if (versao == Versao.BEFORE) {
            com.example.solid.before.Cartao cartao = new com.example.solid.before.Cartao("1234567890123456", "João");
            cartao.pagar(100.0);
        } else {
            com.example.solid.after.payment.Cartao cartao = new com.example.solid.after.payment.Cartao("1234567890123456", "João");
            cartao.pagar(100.0);
        }
    }

    @ParameterizedTest(name = "Versão {0}: Pix deve processar pagamento")
    @EnumSource(Versao.class)
    void pix_deveProcessarPagamento(Versao versao) {
        if (versao == Versao.BEFORE) {
            com.example.solid.before.Pix pix = new com.example.solid.before.Pix("joao@email.com");
            pix.pagar(100.0);
        } else {
            com.example.solid.after.payment.Pix pix = new com.example.solid.after.payment.Pix("joao@email.com");
            pix.pagar(100.0);
        }
    }

    @ParameterizedTest(name = "Versão {0}: Boleto deve processar pagamento")
    @EnumSource(Versao.class)
    void boleto_deveProcessarPagamento(Versao versao) {
        if (versao == Versao.BEFORE) {
            com.example.solid.before.Boleto boleto = new com.example.solid.before.Boleto("12345678901234567890123456789012345678901234");
            boleto.pagar(100.0);
        } else {
            com.example.solid.after.payment.Boleto boleto = new com.example.solid.after.payment.Boleto("12345678901234567890123456789012345678901234");
            boleto.pagar(100.0);
        }
    }

    // ==================== TESTES DE ESTORNO (DIFERENÇA DE COMPORTAMENTO) ====================

    @Test
    void before_cartao_devePermitirEstorno() {
        com.example.solid.before.Cartao cartao = new com.example.solid.before.Cartao("1234567890123456", "João");
        cartao.estornar(100.0);
    }

    @Test
    void after_cartao_devePermitirEstorno() {
        com.example.solid.after.payment.Cartao cartao = new com.example.solid.after.payment.Cartao("1234567890123456", "João");
        cartao.estornar(100.0);
    }

    @Test
    void before_pix_devePermitirEstorno() {
        com.example.solid.before.Pix pix = new com.example.solid.before.Pix("joao@email.com");
        pix.estornar(100.0);
    }

    @Test
    void after_pix_devePermitirEstorno() {
        com.example.solid.after.payment.Pix pix = new com.example.solid.after.payment.Pix("joao@email.com");
        pix.estornar(100.0);
    }

    @Test
    void before_boleto_deveLancarExcecaoNoEstorno() {
        com.example.solid.before.Boleto boleto = new com.example.solid.before.Boleto("12345678901234567890123456789012345678901234");
        assertThatThrownBy(() -> boleto.estornar(100.0))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessageContaining("não suporta estorno");
    }

    @Test
    void after_boleto_naoImplementaEstornavel() {
        com.example.solid.after.payment.Boleto boleto = new com.example.solid.after.payment.Boleto("12345678901234567890123456789012345678901234");
        assertThat(boleto).isInstanceOf(com.example.solid.after.payment.MeioDePagamento.class);
        assertThat(boleto).isNotInstanceOf(com.example.solid.after.payment.Estornavel.class);
    }

    // ==================== TESTES DE CANCELAMENTO ====================

    @Test
    void before_cancelarPedido_comCartao_deveEstornar() {
        com.example.solid.before.Cliente cliente = clienteVipBefore();
        List<com.example.solid.before.Item> itens = itensBasicosBefore();
        com.example.solid.before.Cartao cartao = new com.example.solid.before.Cartao("1234567890123456", "João");
        com.example.solid.before.Pedido pedido = new com.example.solid.before.Pedido(itens, cliente, cartao);
        
        com.example.solid.before.CancelarPedido cancelar = new com.example.solid.before.CancelarPedido();
        cancelar.executar(pedido);
    }

    @Test
    void after_cancelarPedido_comCartao_deveEstornar() {
        com.example.solid.after.domain.Cliente cliente = clienteVipAfter();
        List<com.example.solid.after.domain.Item> itens = itensBasicosAfter();
        com.example.solid.after.payment.Cartao cartao = new com.example.solid.after.payment.Cartao("1234567890123456", "João");
        com.example.solid.after.domain.Pedido pedido = new com.example.solid.after.domain.Pedido(itens, cliente);
        
        com.example.solid.after.application.CancelarPedido cancelar = new com.example.solid.after.application.CancelarPedido();
        cancelar.executar(pedido, cartao, cartao);
    }

    @Test
    void before_cancelarPedido_comBoleto_deveTratarEspecialmente() {
        com.example.solid.before.Cliente cliente = clienteVipBefore();
        List<com.example.solid.before.Item> itens = itensBasicosBefore();
        com.example.solid.before.Boleto boleto = new com.example.solid.before.Boleto("12345678901234567890123456789012345678901234");
        com.example.solid.before.Pedido pedido = new com.example.solid.before.Pedido(itens, cliente, boleto);
        
        com.example.solid.before.CancelarPedido cancelar = new com.example.solid.before.CancelarPedido();
        cancelar.executar(pedido);
    }

    @Test
    void after_cancelarPedido_comBoleto_deveUsarFluxoAlternativo() {
        com.example.solid.after.domain.Cliente cliente = clienteVipAfter();
        List<com.example.solid.after.domain.Item> itens = itensBasicosAfter();
        com.example.solid.after.payment.Boleto boleto = new com.example.solid.after.payment.Boleto("12345678901234567890123456789012345678901234");
        com.example.solid.after.domain.Pedido pedido = new com.example.solid.after.domain.Pedido(itens, cliente);
        
        com.example.solid.after.application.CancelarPedido cancelar = new com.example.solid.after.application.CancelarPedido();
        cancelar.executar(pedido, boleto, null);
    }

    // ==================== TESTES DE SUBSTITUIÇÃO (LSP) ====================

    @Test
    void before_violaLSP_boletoNaoESubstituivelPorMeioDePagamento() {
        // Este teste demonstra a violação: Boleto não pode substituir MeioDePagamento
        // porque quebra o contrato (lança exceção em estornar)
        com.example.solid.before.MeioDePagamento boleto = new com.example.solid.before.Boleto("12345678901234567890123456789012345678901234");
        
        // O contrato diz que estornar deve funcionar, mas falha
        assertThatThrownBy(() -> boleto.estornar(100.0))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void after_respeitaLSP_boletoImplementaApenasContratoQueCumpre() {
        // Boleto implementa apenas MeioDePagamento (pagar), não Estornavel
        // Isso respeita LSP: cada tipo promete apenas o que cumpre
        com.example.solid.after.payment.Boleto boleto = new com.example.solid.after.payment.Boleto("12345678901234567890123456789012345678901234");
        
        // Pode ser usado como MeioDePagamento
        com.example.solid.after.payment.MeioDePagamento pagamento = boleto;
        pagamento.pagar(100.0); // Funciona
        
        // NÃO pode ser usado como Estornavel (não implementa)
        assertThat(boleto).isNotInstanceOf(com.example.solid.after.payment.Estornavel.class);
    }

    @Test
    void after_cartaoEPixSaoSubstituiveisPorAmbosContratos() {
        com.example.solid.after.payment.Cartao cartao = new com.example.solid.after.payment.Cartao("1234567890123456", "João");
        com.example.solid.after.payment.Pix pix = new com.example.solid.after.payment.Pix("joao@email.com");
        
        // Ambos implementam MeioDePagamento E Estornavel
        assertThat(cartao).isInstanceOf(com.example.solid.after.payment.MeioDePagamento.class);
        assertThat(cartao).isInstanceOf(com.example.solid.after.payment.Estornavel.class);
        assertThat(pix).isInstanceOf(com.example.solid.after.payment.MeioDePagamento.class);
        assertThat(pix).isInstanceOf(com.example.solid.after.payment.Estornavel.class);
        
        // Podem ser usados polimorficamente em ambos os contratos
        com.example.solid.after.payment.MeioDePagamento pagamento1 = cartao;
        com.example.solid.after.payment.Estornavel estorno1 = cartao;
        pagamento1.pagar(100.0);
        estorno1.estornar(100.0);
        
        com.example.solid.after.payment.MeioDePagamento pagamento2 = pix;
        com.example.solid.after.payment.Estornavel estorno2 = pix;
        pagamento2.pagar(100.0);
        estorno2.estornar(100.0);
    }
}