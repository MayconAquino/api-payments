package api_payments.repository;

import api_payments.ENUMs.StatusPagamento;
import api_payments.model.TransacaoPagamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransacaoPagamentoRepository extends JpaRepository<TransacaoPagamento, String> {
    List<TransacaoPagamento> findByStatus(StatusPagamento status);
}
