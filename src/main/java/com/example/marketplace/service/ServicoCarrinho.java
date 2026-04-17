package com.example.marketplace.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.marketplace.model.ItemCarrinho;
import com.example.marketplace.model.Produto;
import com.example.marketplace.model.ResumoCarrinho;
import com.example.marketplace.model.SelecaoCarrinho;
import com.example.marketplace.repository.ProdutoRepository;

@Service
public class ServicoCarrinho {

    private static final BigDecimal CEM = BigDecimal.valueOf(100);
    private static final BigDecimal DESCONTO_MAXIMO = BigDecimal.valueOf(25);

    private final ProdutoRepository repositorioProdutos;

    public ServicoCarrinho(ProdutoRepository repositorioProdutos) {
        this.repositorioProdutos = repositorioProdutos;
    }

    public ResumoCarrinho construirResumo(List<SelecaoCarrinho> selecoes) {

        List<ItemCarrinho> itens = new ArrayList<>();

        // =========================
        // Monta os itens do carrinho
        // =========================
        for (SelecaoCarrinho selecao : selecoes) {
            Produto produto = repositorioProdutos.buscarPorId(selecao.getProdutoId())
                    .orElseThrow(
                            () -> new IllegalArgumentException("Produto não encontrado: " + selecao.getProdutoId()));

            itens.add(new ItemCarrinho(produto, selecao.getQuantidade()));
        }

        // =========================
        // Calcula subtotal
        // =========================
        BigDecimal subtotal = itens.stream()
                .map(ItemCarrinho::calcularSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal[] descontoCalculado = calcularDesconto(subtotal, itens);
        BigDecimal percentualDesconto = descontoCalculado[0];
        BigDecimal valorDesconto = descontoCalculado[1];
        BigDecimal total = descontoCalculado[2];

        return new ResumoCarrinho(itens, subtotal, percentualDesconto, valorDesconto, total);
    }

    private BigDecimal[] calcularDesconto(BigDecimal subtotal, List<ItemCarrinho> itens) {
        int quantidadeTotalItens = itens.stream().mapToInt(ItemCarrinho::getQuantidade).sum();

        BigDecimal percentualPorQuantidade = calcularPercentualPorQuantidade(quantidadeTotalItens);
        BigDecimal percentualPorCategoria = itens.stream()
                .map(this::calcularPercentualPorCategoria)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal percentualDesconto = percentualPorQuantidade.add(percentualPorCategoria);
        if (percentualDesconto.compareTo(DESCONTO_MAXIMO) > 0) {
            percentualDesconto = DESCONTO_MAXIMO;
        }

        BigDecimal valorDesconto = subtotal
                .multiply(percentualDesconto)
                .divide(CEM, 2, RoundingMode.HALF_UP);
        BigDecimal total = subtotal.subtract(valorDesconto).setScale(2, RoundingMode.HALF_UP);

        return new BigDecimal[] { percentualDesconto, valorDesconto, total };
    }

    private BigDecimal calcularPercentualPorQuantidade(int quantidadeTotalItens) {
        if (quantidadeTotalItens <= 1) {
            return BigDecimal.ZERO;
        }
        if (quantidadeTotalItens == 2) {
            return BigDecimal.valueOf(5);
        }
        if (quantidadeTotalItens == 3) {
            return BigDecimal.valueOf(7);
        }
        return BigDecimal.valueOf(10);
    }

    private BigDecimal calcularPercentualPorCategoria(ItemCarrinho item) {
        BigDecimal percentualCategoria = switch (item.getProduto().getCategoria()) {
            case CAPINHA, FONE -> BigDecimal.valueOf(3);
            case CARREGADOR -> BigDecimal.valueOf(5);
            case PELICULA, SUPORTE -> BigDecimal.valueOf(2);
        };

        return percentualCategoria.multiply(BigDecimal.valueOf(item.getQuantidade()));
    }
}
