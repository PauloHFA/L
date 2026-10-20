package com.example.solid.after.payment;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MeioDePagamentoTest {

    @Test
    void cartao_deveProcessarPagamento() {
        Cartao cartao = new Cartao("1234567890123456", "João");
        cartao.pagar(100.0);
    }

    @Test
    void cartao_devePermitirEstorno() {
        Cartao cartao = new Cartao("1234567890123456", "João");
        cartao.estornar(100.0);
    }

    @Test
    void cartao_implementaMeioDePagamento() {
        Cartao cartao = new Cartao("1234567890123456", "João");
        assertThat(cartao).isInstanceOf(MeioDePagamento.class);
    }

    @Test
    void cartao_implementaEstornavel() {
        Cartao cartao = new Cartao("1234567890123456", "João");
        assertThat(cartao).isInstanceOf(Estornavel.class);
    }

    @Test
    void pix_deveProcessarPagamento() {
        Pix pix = new Pix("joao@email.com");
        pix.pagar(100.0);
    }

    @Test
    void pix_devePermitirEstorno() {
        Pix pix = new Pix("joao@email.com");
        pix.estornar(100.0);
    }

    @Test
    void pix_implementaMeioDePagamento() {
        Pix pix = new Pix("joao@email.com");
        assertThat(pix).isInstanceOf(MeioDePagamento.class);
    }

    @Test
    void pix_implementaEstornavel() {
        Pix pix = new Pix("joao@email.com");
        assertThat(pix).isInstanceOf(Estornavel.class);
    }

    @Test
    void boleto_deveProcessarPagamento() {
        Boleto boleto = new Boleto("12345678901234567890123456789012345678901234");
        boleto.pagar(100.0);
    }

    @Test
    void boleto_implementaMeioDePagamento() {
        Boleto boleto = new Boleto("12345678901234567890123456789012345678901234");
        assertThat(boleto).isInstanceOf(MeioDePagamento.class);
    }

    @Test
    void boleto_naoImplementaEstornavel() {
        Boleto boleto = new Boleto("12345678901234567890123456789012345678901234");
        assertThat(boleto).isNotInstanceOf(Estornavel.class);
    }
}