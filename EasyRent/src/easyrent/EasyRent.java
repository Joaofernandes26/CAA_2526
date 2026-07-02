package easyrent;

import java.sql.*;
import java.util.Scanner;

public class EasyRent {

    private static final String URL = "jdbc:sqlite:database/easyrent.db";
    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        int opcao;

        do {
            System.out.println("\n===== EASYRENT - MODO TERMINAL =====");
            System.out.println("1 - Listar clientes");
            System.out.println("2 - Adicionar cliente");
            System.out.println("3 - Listar viaturas");
            System.out.println("4 - Adicionar viatura");
            System.out.println("5 - Listar alugueres");
            System.out.println("0 - Sair");
            System.out.print("Escolha uma opção: ");

            opcao = sc.nextInt();
            sc.nextLine();

            switch (opcao) {
                case 1 -> listarClientes();
                case 2 -> adicionarCliente();
                case 3 -> listarViaturas();
                case 4 -> adicionarViatura();
                case 5 -> listarAlugueres();
                case 0 -> System.out.println("A sair do sistema...");
                default -> System.out.println("Opção inválida.");
            }

        } while (opcao != 0);
    }

    private static Connection ligar() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    private static void listarClientes() {
        String sql = "SELECT * FROM clientes";

        try (Connection con = ligar();
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql)) {

            System.out.println("\n--- LISTA DE CLIENTES ---");

            while (rs.next()) {
                System.out.println(
                        rs.getInt("id") + " | " +
                        rs.getString("nome") + " | " +
                        rs.getString("telefone") + " | " +
                        rs.getString("nif") + " | " +
                        rs.getString("morada")
                );
            }

        } catch (SQLException e) {
            System.out.println("Erro ao listar clientes: " + e.getMessage());
        }
    }

    private static void adicionarCliente() {
        try {
            System.out.print("Nome: ");
            String nome = sc.nextLine();

            System.out.print("Telefone: ");
            String telefone = sc.nextLine();

            System.out.print("NIF: ");
            String nif = sc.nextLine();

            System.out.print("Morada: ");
            String morada = sc.nextLine();

            String sql = "INSERT INTO clientes (nome, telefone, nif, morada) VALUES (?, ?, ?, ?)";

            try (Connection con = ligar();
                PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setString(1, nome);
                ps.setString(2, telefone);
                ps.setString(3, nif);
                ps.setString(4, morada);

                ps.executeUpdate();
                System.out.println("Cliente adicionado com sucesso.");
            }

        } catch (SQLException e) {
            System.out.println("Erro ao adicionar cliente: " + e.getMessage());
        }
    }

    private static void listarViaturas() {
        String sql = "SELECT * FROM viaturas";

        try (Connection con = ligar();
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql)) {

            System.out.println("\n--- LISTA DE VIATURAS ---");

            while (rs.next()) {
                System.out.println(
                        rs.getString("matricula") + " | " +
                        rs.getString("marca") + " | " +
                        rs.getString("modelo") + " | " +
                        rs.getString("estado") + " | " +
                        rs.getDouble("preco_diario") + " ECV/dia"
                );
            }

        } catch (SQLException e) {
            System.out.println("Erro ao listar viaturas: " + e.getMessage());
        }
    }

    private static void adicionarViatura() {
        try {
            System.out.print("Matrícula: ");
            String matricula = sc.nextLine();

            System.out.print("Marca: ");
            String marca = sc.nextLine();

            System.out.print("Modelo: ");
            String modelo = sc.nextLine();

            System.out.print("Preço diário: ");
            double precoDiario = sc.nextDouble();

            System.out.print("Preço semanal: ");
            double precoSemanal = sc.nextDouble();

            System.out.print("Preço mensal: ");
            double precoMensal = sc.nextDouble();
            sc.nextLine();

            String sql = "INSERT INTO viaturas (matricula, marca, modelo, estado, preco_diario, preco_semanal, preco_mensal) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?)";

            try (Connection con = ligar();
                PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setString(1, matricula);
                ps.setString(2, marca);
                ps.setString(3, modelo);
                ps.setString(4, "Disponível");
                ps.setDouble(5, precoDiario);
                ps.setDouble(6, precoSemanal);
                ps.setDouble(7, precoMensal);

                ps.executeUpdate();
                System.out.println("Viatura adicionada com sucesso.");
            }

        } catch (SQLException e) {
            System.out.println("Erro ao adicionar viatura: " + e.getMessage());
        }
    }

    private static void listarAlugueres() {
        String sql = "SELECT * FROM alugueres";

        try (Connection con = ligar();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            System.out.println("\n--- LISTA DE ALUGUERES ---");

            while (rs.next()) {
                System.out.println(
                        "ID: " + rs.getInt("id") +
                        " | Cliente: " + rs.getInt("id_cliente") +
                        " | Viatura: " + rs.getString("matricula_viatura") +
                        " | Pacote: " + rs.getString("pacote") +
                        " | Quantidade: " + rs.getInt("quantidade") +
                        " | Total: " + rs.getDouble("valor_total") + " ECV"
                );
            }

        } catch (SQLException e) {
            System.out.println("Erro ao listar alugueres: " + e.getMessage());
        }
    }
}