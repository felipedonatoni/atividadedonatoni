import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class loja {

    static class Produto {
        private String nome;
        private double preco;
        private int estoque;

        public Produto(String nome, double preco, int estoque) {
            this.nome = nome;
            this.preco = preco;
            this.estoque = estoque;
        }

        public String getNome() { return nome; }
        public double getPreco() { return preco; }
        public int getEstoque() { return estoque; }
        public void setEstoque(int estoque) { this.estoque = estoque; }

        @Override
        public String toString() {
            return String.format("%s [R$ %.2f | Estoque: %d]", nome, preco, estoque);
        }
    }

    static class Caixa {
        private Map<Integer, Integer> notas;
        private final int[] VALORES_NOTAS = {50, 20, 10}; 

        public Caixa() {
            notas = new HashMap<>();
            notas.put(50, 5);
            notas.put(20, 5);
            notas.put(10, 5);
        }

        public double getSaldoTotal() {
            double total = 0;
            for (Map.Entry<Integer, Integer> entry : notas.entrySet()) {
                total += entry.getKey() * entry.getValue();
            }
            return total;
        }

        public void exibirCaixa() {
            System.out.printf("Saldo Total: R$ %.2f%n", getSaldoTotal());
            System.out.println("Notas Disponíveis");
            for (int valor : VALORES_NOTAS) {
                System.out.printf("R$ %d,00: %d notas%n", valor, notas.getOrDefault(valor, 0));
            }
            System.out.println("--");
        }

        public Map<Integer, Integer> calcularTroco(double valorTroco) {
            if (valorTroco < 0.01) return new HashMap<>();
            
            Map<Integer, Integer> trocoNecessario = new HashMap<>();
            double restante = valorTroco;
            
            for (int valorNota : VALORES_NOTAS) {
                int qtdDisponivel = notas.getOrDefault(valorNota, 0);
                int qtdParaUsar = (int) Math.floor(restante / valorNota);
                int qtdRealmenteUsada = Math.min(qtdParaUsar, qtdDisponivel);
                
                if (qtdRealmenteUsada > 0) {
                    trocoNecessario.put(valorNota, qtdRealmenteUsada);
                    restante -= qtdRealmenteUsada * valorNota;
                }
            }
            
            if (restante > 0.01) { 
                return null;
            }
            
            return trocoNecessario;
        }

        public void efetivarTransacao(Map<Integer, Integer> troco) {
            if (troco != null) {
                for (Map.Entry<Integer, Integer> entry : troco.entrySet()) {
                    int valorNota = entry.getKey();
                    int qtdTroco = entry.getValue();
                    
                    int qtdAtual = notas.getOrDefault(valorNota, 0);
                    notas.put(valorNota, qtdAtual - qtdTroco);
                }
            }
        }
    }

    static class Loja {
        private List<Produto> produtos;
        private Caixa caixa;
        private Scanner scanner;

        public Loja() {
            produtos = new ArrayList<>();
            caixa = new Caixa();
            scanner = new Scanner(System.in);
            produtos.add(new Produto("Caderno", 15.50, 10));
            produtos.add(new Produto("Caneta Azul", 2.00, 50));
        }
        
        private boolean existeProduto(String nome) {
            for (Produto p : produtos) {
                if (p.getNome().equalsIgnoreCase(nome.trim())) {
                    return true;
                }
            }
            return false;
        }

        public void cadastrarProduto() {
            System.out.println("\nCadastro de Produto");
            
            String nome = "";
            do {
                System.out.print("Nome do Produto: ");
                nome = scanner.nextLine().trim();
                if (nome.isEmpty()) {
                    System.out.println("Nome não pode ser vazio.");
                } else if (existeProduto(nome)) {
                    System.out.println("Já existe um produto com este nome.");
                    nome = ""; 
                }
            } while (nome.isEmpty());

            double preco = 0;
            while (preco <= 0) {
                System.out.print("Preço (R$): ");
                if (scanner.hasNextDouble()) {
                    preco = scanner.nextDouble();
                    if (preco <= 0) System.out.println("Preço deve ser maior que zero.");
                } else {
                    System.out.println("Entrada inválida.");
                    scanner.next(); 
                }
            }

            int estoque = -1;
            while (estoque < 0) {
                System.out.print("Estoque Inicial: ");
                if (scanner.hasNextInt()) {
                    estoque = scanner.nextInt();
                    if (estoque < 0) System.out.println("Estoque não pode ser negativo.");
                } else {
                    System.out.println("Entrada inválida.");
                    scanner.next(); 
                }
            }
            scanner.nextLine(); 
            
            produtos.add(new Produto(nome, preco, estoque));
            System.out.println("\nProduto '" + nome + "' cadastrado com sucesso.");
        }

        public void listarProdutos() {
            if (produtos.isEmpty()) {
                System.out.println("Nenhum produto cadastrado.");
                return;
            }
            
            System.out.println("\nLista de Produtos");
            for (int i = 0; i < produtos.size(); i++) {
                System.out.printf("[%d] %s%n", i + 1, produtos.get(i));
            }
            System.out.println("--");
        }

        public void realizarVenda() {
            listarProdutos();
            if (produtos.isEmpty()) return;

            System.out.println("\nIniciar Venda");
            System.out.print("Digite o NÚMERO do produto para vender (ou 0 para cancelar): ");
            
            if (!scanner.hasNextInt()) {
                System.out.println("Entrada inválida.");
                scanner.nextLine();
                return;
            }
            int indice = scanner.nextInt() - 1;
            scanner.nextLine(); 

            if (indice < 0 || indice >= produtos.size()) {
                if (indice != -1) System.out.println("Produto inválido.");
                return;
            }

            Produto produtoVenda = produtos.get(indice);
            
            int quantidade = -1;
            while (quantidade <= 0) {
                System.out.printf("Quantas unidades de '%s' (Estoque: %d): ", 
                                  produtoVenda.getNome(), produtoVenda.getEstoque());
                if (scanner.hasNextInt()) {
                    quantidade = scanner.nextInt();
                    if (quantidade <= 0) {
                        System.out.println("A quantidade deve ser maior que zero.");
                    } else if (quantidade > produtoVenda.getEstoque()) {
                        System.out.println("Estoque insuficiente! Máximo disponível: " + produtoVenda.getEstoque());
                        quantidade = -1; 
                    }
                } else {
                    System.out.println("Entrada inválida.");
                    scanner.next();
                }
            }
            scanner.nextLine(); 
            
            double valorTotal = produtoVenda.getPreco() * quantidade;
            System.out.printf("\nValor total da venda: R$ %.2f%n", valorTotal);
            
            double valorPago = -1;
            while (valorPago < valorTotal) {
                System.out.print("Informe o valor pago pelo cliente (R$): ");
                if (scanner.hasNextDouble()) {
                    valorPago = scanner.nextDouble();
                    if (valorPago < valorTotal) {
                        System.out.println("O valor pago é insuficiente.");
                    }
                } else {
                    System.out.println("Entrada inválida.");
                    scanner.next();
                }
            }
            scanner.nextLine(); 

            double troco = valorPago - valorTotal;
            System.out.printf("Troco necessário: R$ %.2f%n", troco);
            
            Map<Integer, Integer> notasTroco = caixa.calcularTroco(troco);
            
            if (notasTroco == null) {
                System.out.println("\nVENDA CANCELADA: Caixa sem notas suficientes para o troco de R$ " + String.format("%.2f", troco) + ".");
                return;
            }

            produtoVenda.setEstoque(produtoVenda.getEstoque() - quantidade);
            caixa.efetivarTransacao(notasTroco);

            System.out.println("\nVENDA CONCLUÍDA!");
            System.out.printf("Total: R$ %.2f | Pago: R$ %.2f | Troco: R$ %.2f%n", valorTotal, valorPago, troco);

            if (troco > 0) {
                System.out.println("Notas de Troco:");
                for (Map.Entry<Integer, Integer> entry : notasTroco.entrySet()) {
                    if (entry.getValue() > 0) {
                         System.out.printf("   - %d nota(s) de R$ %d,00%n", entry.getValue(), entry.getKey());
                    }
                }
            }
        }

        public void exibirCaixa() {
            caixa.exibirCaixa();
        }
    }

    public static void main(String[] args) {
        Loja loja = new Loja();
        Scanner scanner = new Scanner(System.in);
        int opcao = -1;

        System.out.println("Sistema de Loja");
        loja.exibirCaixa();

        while (opcao != 5) {
            System.out.println("\nMENU");
            System.out.println("[1] Cadastrar Produto");
            System.out.println("[2] Listar Produtos");
            System.out.println("[3] Realizar Venda");
            System.out.println("[4] Exibir Caixa");
            System.out.println("[5] Sair");
            System.out.print("Escolha uma opção: ");

            if (scanner.hasNextInt()) {
                opcao = scanner.nextInt();
                scanner.nextLine(); 

                switch (opcao) {
                    case 1:
                        loja.cadastrarProduto();
                        break;
                    case 2:
                        loja.listarProdutos();
                        break;
                    case 3:
                        loja.realizarVenda();
                        break;
                    case 4:
                        loja.exibirCaixa();
                        break;
                    case 5:
                        System.out.println("Encerrando sistema.");
                        break;
                    default:
                        System.out.println("Opção inválida.");
                }
            } else {
                System.out.println("Entrada inválida.");
                scanner.nextLine(); 
                opcao = -1; 
            }
        }
        scanner.close();
    }
}