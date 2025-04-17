package api_payments.model;

import api_payments.ENUMs.StatusPagamento;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transacao_pagamento")
public class TransacaoPagamento {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(name = "wallet_address")
    private String walletAddress ;

    @Column(name = "valor_btc", precision = 18, scale = 8)
    private BigDecimal valorBTC;

    @Enumerated(EnumType.STRING)
    private StatusPagamento status;

    @Column(name = "data_criacao")
    private LocalDateTime dataCriacao = LocalDateTime.now();

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public Long getId() {
        return id;
    }

    public String getwalletAddress () {
        return walletAddress ;
    }

    public BigDecimal getValorBTC() {
        return valorBTC;
    }

    public StatusPagamento getStatus() {
        return status;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setwalletAddress (String wallet) {
        this.walletAddress  = wallet;
    }

    public void setValorBTC(BigDecimal valorBTC) {
        this.valorBTC = valorBTC;
    }

    public void setStatus(StatusPagamento status) {
        this.status = status;
    }
}