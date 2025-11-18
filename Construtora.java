import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class Funcionario {
    private int id;
    private String nome;
    private String cargo;
    private double salario;
    private List<Construcao> construcoes;

    public Funcionario(int id, String nome, String cargo, double salario) {
        this.id = id;
        this.nome = nome;
        this.cargo = cargo;
        this.salario = salario;
        this.construcoes = new ArrayList<>();
    }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getCargo() { return cargo; }
    public double getSalario() { return salario; }
    public List<Construcao> getConstrucoes() { return construcoes; }

    public void setNome(String nome) { this.nome = nome; }
    public void setCargo(String cargo) { this.cargo = cargo; }
    public void setSalario(double salario) { this.salario = salario; }

    public void adicionarConstrucao(Construcao construcao) {
        if (!construcoes.contains(construcao)) {
            construcoes.add(construcao);
        }
    }

    public void removerConstrucao(Construcao construcao) {
        construcoes.remove(construcao);
    }

    public String toString() {
        return "ID: " + id + " | Nome: " + nome + " | Cargo: " + cargo + " | Salario: R$ " + salario;
    }
}

class Construcao {
    private int id;
    private String nomeObra;
    private String endereco;
    private LocalDate dataInicio;
    private List<Funcionario> funcionarios;

    public Construcao(int id, String nomeObra, String endereco, LocalDate dataInicio) {
        this.id = id;
        this.nomeObra = nomeObra;
        this.endereco = endereco;
        this.dataInicio = dataInicio;
        this.funcionarios = new ArrayList<>();
    }

    public int getId() { return id; }
    public String getNomeObra() { return nomeObra; }
    public String getEndereco() { return endereco; }
    public LocalDate getDataInicio() { return dataInicio; }
    public List<Funcionario> getFuncionarios() { return funcionarios; }

    public void setNomeObra(String nomeObra) { this.nomeObra = nomeObra; }
    public void setEndereco(String endereco) { this.endereco = endereco; }
    public void setDataInicio(LocalDate dataInicio) { this.dataInicio = dataInicio; }

    public void adicionarFuncionario(Funcionario funcionario) {
        if (!funcionarios.contains(funcionario)) {
            funcionarios.add(funcionario);
            funcionario.adicionarConstrucao(this);
        }
    }

    public void removerFuncionario(Funcionario funcionario) {
        funcionarios.remove(funcionario);
        funcionario.removerConstrucao(this);
    }

    public String toString() {
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return "ID: " + id + " | Obra: " + nomeObra + " | Endereco: " + endereco + " | Inicio: " + dataInicio.format(formato);
    }
}

public class Construtora {
    private List<Funcionario> funcionarios;
    private List<Construcao> construcoes;
    private Scanner scanner;
    private int nextFuncionarioId;
    private int nextConstrucaoId;
    private DateTimeFormatter formatoData;

    public Construtora() {
        funcionarios = new ArrayList<>();
        construcoes = new ArrayList<>();
        scanner = new Scanner(System.in);
        nextFuncionarioId = 1;
        nextConstrucaoId = 1;
        formatoData = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    }

    private LocalDate lerData(String mensagem) {
        while (true) {
            System.out.print(mensagem);
            String dataStr = scanner.nextLine();
            
            try {
                // Tenta converter no formato brasileiro
                return LocalDate.parse(dataStr, formatoData);
            } catch (DateTimeParseException e) {
                System.out.println("Data invalida. Use o formato DD/MM/AAAA (ex: 20/12/2025)");
            }
        }
    }

    public void criarFuncionario() {
        System.out.println("Cadastrar Funcionario");
        System.out.print("Nome: ");
        String nome = scanner.nextLine();
        System.out.print("Cargo: ");
        String cargo = scanner.nextLine();
        System.out.print("Salario: ");
        double salario = scanner.nextDouble();
        scanner.nextLine();

        Funcionario func = new Funcionario(nextFuncionarioId++, nome, cargo, salario);
        funcionarios.add(func);
        System.out.println("Funcionario cadastrado com ID: " + func.getId());
    }

    public void listarFuncionarios() {
        System.out.println("Lista de Funcionarios:");
        if (funcionarios.isEmpty()) {
            System.out.println("Nenhum funcionario cadastrado");
            return;
        }
        for (Funcionario f : funcionarios) {
            System.out.println(f);
        }
    }

    public void atualizarFuncionario() {
        System.out.print("ID do funcionario para atualizar: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        Funcionario func = buscarFuncionario(id);
        if (func == null) {
            System.out.println("Funcionario nao encontrado");
            return;
        }

        System.out.print("Novo nome (" + func.getNome() + "): ");
        String nome = scanner.nextLine();
        if (!nome.isEmpty()) func.setNome(nome);

        System.out.print("Novo cargo (" + func.getCargo() + "): ");
        String cargo = scanner.nextLine();
        if (!cargo.isEmpty()) func.setCargo(cargo);

        System.out.print("Novo salario (" + func.getSalario() + "): ");
        String salarioStr = scanner.nextLine();
        if (!salarioStr.isEmpty()) func.setSalario(Double.parseDouble(salarioStr));

        System.out.println("Funcionario atualizado");
    }

    public void excluirFuncionario() {
        System.out.print("ID do funcionario para excluir: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        Funcionario func = buscarFuncionario(id);
        if (func == null) {
            System.out.println("Funcionario nao encontrado");
            return;
        }

        for (Construcao constr : new ArrayList<>(func.getConstrucoes())) {
            constr.removerFuncionario(func);
        }

        funcionarios.remove(func);
        System.out.println("Funcionario excluido");
    }

    public void criarConstrucao() {
        System.out.println("Cadastrar Construcao");
        System.out.print("Nome da obra: ");
        String nomeObra = scanner.nextLine();
        System.out.print("Endereco: ");
        String endereco = scanner.nextLine();
        
        LocalDate dataInicio = lerData("Data de inicio (DD/MM/AAAA): ");

        Construcao constr = new Construcao(nextConstrucaoId++, nomeObra, endereco, dataInicio);
        construcoes.add(constr);
        System.out.println("Construcao cadastrada com ID: " + constr.getId());
    }

    public void listarConstrucoes() {
        System.out.println("Lista de Construcoes:");
        if (construcoes.isEmpty()) {
            System.out.println("Nenhuma construcao cadastrada");
            return;
        }
        for (Construcao c : construcoes) {
            System.out.println(c);
        }
    }

    public void atualizarConstrucao() {
        System.out.print("ID da construcao para atualizar: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        Construcao constr = buscarConstrucao(id);
        if (constr == null) {
            System.out.println("Construcao nao encontrada");
            return;
        }

        System.out.print("Novo nome da obra (" + constr.getNomeObra() + "): ");
        String nomeObra = scanner.nextLine();
        if (!nomeObra.isEmpty()) constr.setNomeObra(nomeObra);

        System.out.print("Novo endereco (" + constr.getEndereco() + "): ");
        String endereco = scanner.nextLine();
        if (!endereco.isEmpty()) constr.setEndereco(endereco);

        System.out.println("Nova data de inicio (" + constr.getDataInicio().format(formatoData) + "): ");
        String dataStr = scanner.nextLine();
        if (!dataStr.isEmpty()) {
            try {
                constr.setDataInicio(LocalDate.parse(dataStr, formatoData));
            } catch (DateTimeParseException e) {
                System.out.println("Data invalida. Mantida a data anterior.");
            }
        }

        System.out.println("Construcao atualizada");
    }

    public void excluirConstrucao() {
        System.out.print("ID da construcao para excluir: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        Construcao constr = buscarConstrucao(id);
        if (constr == null) {
            System.out.println("Construcao nao encontrada");
            return;
        }

        for (Funcionario func : new ArrayList<>(constr.getFuncionarios())) {
            func.removerConstrucao(constr);
        }

        construcoes.remove(constr);
        System.out.println("Construcao excluida");
    }

    public void vincularFuncionarioConstrucao() {
        System.out.print("ID do funcionario: ");
        int idFunc = scanner.nextInt();
        System.out.print("ID da construcao: ");
        int idConstr = scanner.nextInt();
        scanner.nextLine();

        Funcionario func = buscarFuncionario(idFunc);
        Construcao constr = buscarConstrucao(idConstr);

        if (func == null || constr == null) {
            System.out.println("Funcionario ou construcao nao encontrado");
            return;
        }

        constr.adicionarFuncionario(func);
        System.out.println("Funcionario vinculado a construcao");
    }

    public void desvincularFuncionarioConstrucao() {
        System.out.print("ID do funcionario: ");
        int idFunc = scanner.nextInt();
        System.out.print("ID da construcao: ");
        int idConstr = scanner.nextInt();
        scanner.nextLine();

        Funcionario func = buscarFuncionario(idFunc);
        Construcao constr = buscarConstrucao(idConstr);

        if (func == null || constr == null) {
            System.out.println("Funcionario ou construcao nao encontrado");
            return;
        }

        constr.removerFuncionario(func);
        System.out.println("Funcionario desvinculado da construcao");
    }

    public void relatorioConstrucao() {
        System.out.print("ID da construcao: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        Construcao constr = buscarConstrucao(id);
        if (constr == null) {
            System.out.println("Construcao nao encontrada");
            return;
        }

        System.out.println("Construcao: " + constr.getNomeObra());
        System.out.println("Endereco: " + constr.getEndereco());
        System.out.println("Data de inicio: " + constr.getDataInicio().format(formatoData));
        System.out.println("Funcionarios alocados:");

        if (constr.getFuncionarios().isEmpty()) {
            System.out.println("Nenhum funcionario alocado");
        } else {
            for (Funcionario f : constr.getFuncionarios()) {
                System.out.println("- " + f.getNome() + " (" + f.getCargo() + ")");
            }
        }
    }

    public void relatorioFuncionario() {
        System.out.print("ID do funcionario: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        Funcionario func = buscarFuncionario(id);
        if (func == null) {
            System.out.println("Funcionario nao encontrado");
            return;
        }

        System.out.println("Funcionario: " + func.getNome());
        System.out.println("Cargo: " + func.getCargo());
        System.out.println("Salario: R$ " + func.getSalario());
        System.out.println("Construcoes vinculadas:");

        if (func.getConstrucoes().isEmpty()) {
            System.out.println("Nenhuma construcao vinculada");
        } else {
            for (Construcao c : func.getConstrucoes()) {
                System.out.println("- " + c.getNomeObra() + " (" + c.getEndereco() + ")");
            }
        }
    }

    private Funcionario buscarFuncionario(int id) {
        for (Funcionario f : funcionarios) {
            if (f.getId() == id) return f;
        }
        return null;
    }

    private Construcao buscarConstrucao(int id) {
        for (Construcao c : construcoes) {
            if (c.getId() == id) return c;
        }
        return null;
    }

    public void menu() {
        while (true) {
            System.out.println("\nSistema Construtora");
            System.out.println("1 - Cadastrar Funcionario");
            System.out.println("2 - Listar Funcionarios");
            System.out.println("3 - Atualizar Funcionario");
            System.out.println("4 - Excluir Funcionario");
            System.out.println("5 - Cadastrar Construcao");
            System.out.println("6 - Listar Construcoes");
            System.out.println("7 - Atualizar Construcao");
            System.out.println("8 - Excluir Construcao");
            System.out.println("9 - Vincular Funcionario a Construcao");
            System.out.println("10 - Desvincular Funcionario de Construcao");
            System.out.println("11 - Relatorio da Construcao");
            System.out.println("12 - Relatorio do Funcionario");
            System.out.println("0 - Sair");
            System.out.print("Opcao: ");

            int opcao = scanner.nextInt();
            scanner.nextLine();

            switch (opcao) {
                case 1: criarFuncionario(); break;
                case 2: listarFuncionarios(); break;
                case 3: atualizarFuncionario(); break;
                case 4: excluirFuncionario(); break;
                case 5: criarConstrucao(); break;
                case 6: listarConstrucoes(); break;
                case 7: atualizarConstrucao(); break;
                case 8: excluirConstrucao(); break;
                case 9: vincularFuncionarioConstrucao(); break;
                case 10: desvincularFuncionarioConstrucao(); break;
                case 11: relatorioConstrucao(); break;
                case 12: relatorioFuncionario(); break;
                case 0: return;
                default: System.out.println("Opcao invalida");
            }
        }
    }
    public static void main(String[] args) {
        Construtora sistema = new Construtora();
        sistema.menu();
    }
}