package api_payments.service;

import api_payments.ENUMs.StatusPagamento;
import api_payments.model.TransacaoPagamento;
import api_payments.repository.TransacaoPagamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PagamentoService {

    @Autowired
    private TransacaoPagamentoRepository transacaoPagamentoRepository;

    public TransacaoPagamento criarTransacao(String walletAddress, BigDecimal amountBitcoin) {
        TransacaoPagamento transacao = new TransacaoPagamento();
        transacao.setwalletAddress(walletAddress);
        transacao.setValorBTC(amountBitcoin);
        transacao.setStatus(StatusPagamento.PENDENTE);
        return transacaoPagamentoRepository.save(transacao);
    }

    public TransacaoPagamento getTransacaoPorId(String id) {
        return transacaoPagamentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transação não encontrada"));
    }

    public void atualizarStatusTransacao(String id, StatusPagamento status) {
        TransacaoPagamento transacao = getTransacaoPorId(id);
        transacao.setStatus(status);
        transacaoPagamentoRepository.save(transacao);
    }
}
