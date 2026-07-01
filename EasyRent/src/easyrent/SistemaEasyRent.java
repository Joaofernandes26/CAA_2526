package easyrent;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;

public class SistemaEasyRent {

    private static final String PASTA_BD = "database";
    private static final String FICHEIRO_BD = PASTA_BD + File.separator + "easyrent.db";
    private static final String URL_BD = "jdbc:sqlite:" + FICHEIRO_BD;

    private final String FICHEIRO_VIATURAS = "viaturas.csv";
    private final String FICHEIRO_CLIENTES = "clientes.csv";
    private final String FICHEIRO_ALUGUERES = "alugueres.csv";

    public SistemaEasyRent() {
        iniciarBaseDados();
    }

    private Connection ligar() throws SQLException {
        return DriverManager.getConnection(URL_BD);
    }

    private void iniciarBaseDados() {
        try {
            new File(PASTA_BD).mkdirs();
            Class.forName("org.sqlite.JDBC");
            criarTabelas();
            criarUtilizadorInicial();
            importarCSVSeNecessario();
        } catch (ClassNotFoundException e) {
            mostrarErroBD("Driver SQLite não encontrado. Confirme se o ficheiro sqlite-jdbc.jar está na pasta lib do projeto.");
        } catch (SQLException e) {
            mostrarErroBD("Erro ao iniciar a base de dados: " + e.getMessage());
        }
    }

    private void mostrarErroBD(String mensagem) {
        System.out.println(mensagem);
    }

    private void criarTabelas() throws SQLException {
        try (Connection con = ligar(); Statement st = con.createStatement()) {
            st.execute("CREATE TABLE IF NOT EXISTS clientes (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "nome TEXT NOT NULL," +
                    "telefone TEXT NOT NULL," +
                    "nif TEXT," +
                    "morada TEXT" +
                    ")");

            st.execute("CREATE TABLE IF NOT EXISTS viaturas (" +
                    "matricula TEXT PRIMARY KEY," +
                    "marca TEXT NOT NULL," +
                    "modelo TEXT NOT NULL," +
                    "estado TEXT NOT NULL," +
                    "preco_diario REAL NOT NULL," +
                    "preco_semanal REAL NOT NULL," +
                    "preco_mensal REAL NOT NULL" +
                    ")");

            st.execute("CREATE TABLE IF NOT EXISTS alugueres (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "id_cliente INTEGER NOT NULL," +
                    "matricula_viatura TEXT NOT NULL," +
                    "pacote TEXT NOT NULL," +
                    "quantidade INTEGER NOT NULL," +
                    "valor_total REAL NOT NULL," +
                    "devolvido INTEGER NOT NULL DEFAULT 0," +
                    "FOREIGN KEY(id_cliente) REFERENCES clientes(id)," +
                    "FOREIGN KEY(matricula_viatura) REFERENCES viaturas(matricula)" +
                    ")");

            st.execute("CREATE TABLE IF NOT EXISTS utilizadores (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "utilizador TEXT UNIQUE NOT NULL," +
                    "password TEXT NOT NULL" +
                    ")");
        }
    }

    private void criarUtilizadorInicial() throws SQLException {
        String sql = "INSERT OR IGNORE INTO utilizadores (utilizador, password) VALUES (?, ?)";
        try (Connection con = ligar(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "admin");
            ps.setString(2, "1234");
            ps.executeUpdate();
        }
    }

    public boolean validarLogin(String utilizador, String password) {
        String sql = "SELECT id FROM utilizadores WHERE utilizador = ? AND password = ?";
        try (Connection con = ligar(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, utilizador);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            return false;
        }
    }

    private int contarRegistos(String tabela) throws SQLException {
        try (Connection con = ligar(); Statement st = con.createStatement(); ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM " + tabela)) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private void importarCSVSeNecessario() throws SQLException {
        if (contarRegistos("viaturas") == 0) importarViaturasCSV();
        if (contarRegistos("clientes") == 0) importarClientesCSV();
        if (contarRegistos("alugueres") == 0) importarAlugueresCSV();
    }

    private void importarViaturasCSV() {
        File ficheiro = new File(FICHEIRO_VIATURAS);
        if (!ficheiro.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(ficheiro))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                String[] p = linha.split(";", -1);
                if (p.length == 7) {
                    inserirViaturaDireto(p[0], p[1], p[2], p[3], Double.parseDouble(p[4]), Double.parseDouble(p[5]), Double.parseDouble(p[6]));
                }
            }
        } catch (Exception e) {
            System.out.println("Erro ao importar viaturas CSV: " + e.getMessage());
        }
    }

    private void importarClientesCSV() {
        File ficheiro = new File(FICHEIRO_CLIENTES);
        if (!ficheiro.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(ficheiro))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                String[] p = linha.split(";", -1);
                if (p.length >= 4) {
                    int id = Integer.parseInt(p[0]);
                    String morada = p.length >= 5 ? p[4] : "";
                    inserirClienteDireto(id, p[1], p[2], p[3], morada);
                }
            }
        } catch (Exception e) {
            System.out.println("Erro ao importar clientes CSV: " + e.getMessage());
        }
    }

    private void importarAlugueresCSV() {
        File ficheiro = new File(FICHEIRO_ALUGUERES);
        if (!ficheiro.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(ficheiro))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                String[] p = linha.split(";", -1);
                if (p.length == 7) {
                    inserirAluguerDireto(Integer.parseInt(p[0]), Integer.parseInt(p[1]), p[2], p[3], Integer.parseInt(p[4]), Double.parseDouble(p[5]), Boolean.parseBoolean(p[6]));
                }
            }
        } catch (Exception e) {
            System.out.println("Erro ao importar alugueres CSV: " + e.getMessage());
        }
    }

    private void inserirViaturaDireto(String matricula, String marca, String modelo, String estado, double pd, double ps, double pm) throws SQLException {
        String sql = "INSERT OR IGNORE INTO viaturas (matricula, marca, modelo, estado, preco_diario, preco_semanal, preco_mensal) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = ligar(); PreparedStatement psmt = con.prepareStatement(sql)) {
            psmt.setString(1, matricula); psmt.setString(2, marca); psmt.setString(3, modelo); psmt.setString(4, estado);
            psmt.setDouble(5, pd); psmt.setDouble(6, ps); psmt.setDouble(7, pm); psmt.executeUpdate();
        }
    }

    private void inserirClienteDireto(int id, String nome, String telefone, String nif, String morada) throws SQLException {
        String sql = "INSERT OR IGNORE INTO clientes (id, nome, telefone, nif, morada) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = ligar(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id); ps.setString(2, nome); ps.setString(3, telefone); ps.setString(4, nif); ps.setString(5, morada); ps.executeUpdate();
        }
    }

    private void inserirAluguerDireto(int id, int idCliente, String matricula, String pacote, int quantidade, double total, boolean devolvido) throws SQLException {
        String sql = "INSERT OR IGNORE INTO alugueres (id, id_cliente, matricula_viatura, pacote, quantidade, valor_total, devolvido) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = ligar(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id); ps.setInt(2, idCliente); ps.setString(3, matricula); ps.setString(4, pacote);
            ps.setInt(5, quantidade); ps.setDouble(6, total); ps.setInt(7, devolvido ? 1 : 0); ps.executeUpdate();
        }
    }

    // ================= VIATURAS =================

    public String adicionarViatura(String matricula, String marca, String modelo, double precoDiario, double precoSemanal, double precoMensal) {
        if (matricula.isEmpty() || marca.isEmpty() || modelo.isEmpty()) return "Preencha todos os campos obrigatórios.";
        if (precoDiario <= 0 || precoSemanal <= 0 || precoMensal <= 0) return "Os preços devem ser superiores a zero.";
        if (procurarViatura(matricula) != null) return "Já existe uma viatura com esta matrícula.";
        try {
            inserirViaturaDireto(matricula, marca, modelo, "Disponível", precoDiario, precoSemanal, precoMensal);
            return "Viatura registada com sucesso.";
        } catch (SQLException e) { return "Erro ao registar viatura: " + e.getMessage(); }
    }

    public String atualizarViatura(String matricula, String marca, String modelo, String estado, double precoDiario, double precoSemanal, double precoMensal) {
        if (procurarViatura(matricula) == null) return "Viatura não encontrada.";
        if (marca.isEmpty() || modelo.isEmpty()) return "Marca e modelo são obrigatórios.";
        if (precoDiario <= 0 || precoSemanal <= 0 || precoMensal <= 0) return "Os preços devem ser superiores a zero.";
        String sql = "UPDATE viaturas SET marca=?, modelo=?, estado=?, preco_diario=?, preco_semanal=?, preco_mensal=? WHERE matricula=?";
        try (Connection con = ligar(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, marca); ps.setString(2, modelo); ps.setString(3, estado); ps.setDouble(4, precoDiario);
            ps.setDouble(5, precoSemanal); ps.setDouble(6, precoMensal); ps.setString(7, matricula); ps.executeUpdate();
            return "Viatura atualizada com sucesso.";
        } catch (SQLException e) { return "Erro ao atualizar viatura: " + e.getMessage(); }
    }

    public String removerViatura(String matricula) {
        Viatura v = procurarViatura(matricula);
        if (v == null) return "Viatura não encontrada.";
        if (v.getEstado().equalsIgnoreCase("Alugada")) return "Não é possível remover uma viatura alugada.";
        try (Connection con = ligar(); PreparedStatement ps = con.prepareStatement("DELETE FROM viaturas WHERE matricula=?")) {
            ps.setString(1, matricula); ps.executeUpdate(); return "Viatura removida com sucesso.";
        } catch (SQLException e) { return "Erro ao remover viatura: " + e.getMessage(); }
    }

    public Viatura procurarViatura(String matricula) {
        String sql = "SELECT * FROM viaturas WHERE LOWER(matricula)=LOWER(?)";
        try (Connection con = ligar(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, matricula);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return viaturaDoRS(rs); }
        } catch (SQLException e) { }
        return null;
    }

    private Viatura viaturaDoRS(ResultSet rs) throws SQLException {
        return new Viatura(rs.getString("matricula"), rs.getString("marca"), rs.getString("modelo"), rs.getString("estado"), rs.getDouble("preco_diario"), rs.getDouble("preco_semanal"), rs.getDouble("preco_mensal"));
    }

    public ArrayList<Viatura> listarViaturas() {
        ArrayList<Viatura> lista = new ArrayList<>();
        try (Connection con = ligar(); Statement st = con.createStatement(); ResultSet rs = st.executeQuery("SELECT * FROM viaturas ORDER BY matricula")) {
            while (rs.next()) lista.add(viaturaDoRS(rs));
        } catch (SQLException e) { }
        return lista;
    }

    public ArrayList<Viatura> listarViaturasPorEstado(String estado) {
        ArrayList<Viatura> lista = new ArrayList<>();
        String sql = "SELECT * FROM viaturas WHERE LOWER(estado)=LOWER(?) ORDER BY matricula";
        try (Connection con = ligar(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, estado); try (ResultSet rs = ps.executeQuery()) { while (rs.next()) lista.add(viaturaDoRS(rs)); }
        } catch (SQLException e) { }
        return lista;
    }

    // ================= CLIENTES =================

    public String adicionarCliente(String nome, String telefone, String nif, String morada) {
        if (nome.isEmpty() || telefone.isEmpty()) return "Nome e telefone são obrigatórios.";
        String sql = "INSERT INTO clientes (nome, telefone, nif, morada) VALUES (?, ?, ?, ?)";
        try (Connection con = ligar(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nome); ps.setString(2, telefone); ps.setString(3, nif); ps.setString(4, morada); ps.executeUpdate();
            return "Cliente registado com sucesso.";
        } catch (SQLException e) { return "Erro ao registar cliente: " + e.getMessage(); }
    }

    public String atualizarCliente(int id, String nome, String telefone, String nif, String morada) {
        if (procurarCliente(id) == null) return "Cliente não encontrado.";
        if (nome.isEmpty() || telefone.isEmpty()) return "Nome e telefone são obrigatórios.";
        String sql = "UPDATE clientes SET nome=?, telefone=?, nif=?, morada=? WHERE id=?";
        try (Connection con = ligar(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nome); ps.setString(2, telefone); ps.setString(3, nif); ps.setString(4, morada); ps.setInt(5, id); ps.executeUpdate();
            return "Cliente atualizado com sucesso.";
        } catch (SQLException e) { return "Erro ao atualizar cliente: " + e.getMessage(); }
    }

    public String removerCliente(int id) {
        if (procurarCliente(id) == null) return "Cliente não encontrado.";
        String alugueresAtivos = "SELECT COUNT(*) FROM alugueres WHERE id_cliente=? AND devolvido=0";
        try (Connection con = ligar(); PreparedStatement ps = con.prepareStatement(alugueresAtivos)) {
            ps.setInt(1, id); try (ResultSet rs = ps.executeQuery()) { if (rs.next() && rs.getInt(1) > 0) return "Não é possível remover um cliente com aluguer ativo."; }
            try (PreparedStatement del = con.prepareStatement("DELETE FROM clientes WHERE id=?")) { del.setInt(1, id); del.executeUpdate(); }
            return "Cliente removido com sucesso.";
        } catch (SQLException e) { return "Erro ao remover cliente: " + e.getMessage(); }
    }

    public Cliente procurarCliente(int id) {
        String sql = "SELECT * FROM clientes WHERE id=?";
        try (Connection con = ligar(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id); try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return clienteDoRS(rs); }
        } catch (SQLException e) { }
        return null;
    }

    public Cliente procurarClientePorNome(String nome) {
        String sql = "SELECT * FROM clientes WHERE LOWER(nome) LIKE LOWER(?) ORDER BY id LIMIT 1";
        try (Connection con = ligar(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + nome + "%"); try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return clienteDoRS(rs); }
        } catch (SQLException e) { }
        return null;
    }

    private Cliente clienteDoRS(ResultSet rs) throws SQLException {
        return new Cliente(rs.getInt("id"), rs.getString("nome"), rs.getString("telefone"), rs.getString("nif"), rs.getString("morada"));
    }

    public ArrayList<Cliente> listarClientes() {
        ArrayList<Cliente> lista = new ArrayList<>();
        try (Connection con = ligar(); Statement st = con.createStatement(); ResultSet rs = st.executeQuery("SELECT * FROM clientes ORDER BY id")) {
            while (rs.next()) lista.add(clienteDoRS(rs));
        } catch (SQLException e) { }
        return lista;
    }

    // ================= ALUGUERES =================

    public String criarAluguer(int idCliente, String matricula, String pacote, int quantidade) {
        Cliente cliente = procurarCliente(idCliente);
        Viatura viatura = procurarViatura(matricula);
        if (cliente == null) return "Cliente não existe.";
        if (viatura == null) return "Viatura não existe.";
        if (!viatura.getEstado().equalsIgnoreCase("Disponível")) return "A viatura não está disponível.";
        if (quantidade <= 0) return "A quantidade deve ser superior a zero.";

        double total;
        if (pacote.equalsIgnoreCase("Diário")) total = viatura.getPrecoDiario() * quantidade;
        else if (pacote.equalsIgnoreCase("Semanal")) total = viatura.getPrecoSemanal() * quantidade;
        else if (pacote.equalsIgnoreCase("Mensal")) total = viatura.getPrecoMensal() * quantidade;
        else return "Pacote inválido.";

        String sql = "INSERT INTO alugueres (id_cliente, matricula_viatura, pacote, quantidade, valor_total, devolvido) VALUES (?, ?, ?, ?, ?, 0)";
        try (Connection con = ligar()) {
            con.setAutoCommit(false);
            try (PreparedStatement ps = con.prepareStatement(sql); PreparedStatement up = con.prepareStatement("UPDATE viaturas SET estado='Alugada' WHERE matricula=?")) {
                ps.setInt(1, idCliente); ps.setString(2, matricula); ps.setString(3, pacote); ps.setInt(4, quantidade); ps.setDouble(5, total); ps.executeUpdate();
                up.setString(1, matricula); up.executeUpdate(); con.commit();
                return "Aluguer registado com sucesso. Valor total: " + String.format("%.0f", total) + " ECV.";
            } catch (SQLException e) { con.rollback(); return "Erro ao registar aluguer: " + e.getMessage(); }
        } catch (SQLException e) { return "Erro ao registar aluguer: " + e.getMessage(); }
    }

    public String devolverViatura(int idAluguer) {
        Aluguer a = procurarAluguer(idAluguer);
        if (a == null || a.isDevolvido()) return "Aluguer não encontrado ou já devolvido.";
        try (Connection con = ligar()) {
            con.setAutoCommit(false);
            try (PreparedStatement ps = con.prepareStatement("UPDATE alugueres SET devolvido=1 WHERE id=?"); PreparedStatement up = con.prepareStatement("UPDATE viaturas SET estado='Disponível' WHERE matricula=?")) {
                ps.setInt(1, idAluguer); ps.executeUpdate(); up.setString(1, a.getMatriculaViatura()); up.executeUpdate(); con.commit();
                return "Devolução registada com sucesso.";
            } catch (SQLException e) { con.rollback(); return "Erro ao registar devolução: " + e.getMessage(); }
        } catch (SQLException e) { return "Erro ao registar devolução: " + e.getMessage(); }
    }

    private Aluguer procurarAluguer(int id) {
        String sql = "SELECT * FROM alugueres WHERE id=?";
        try (Connection con = ligar(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id); try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return aluguerDoRS(rs); }
        } catch (SQLException e) { }
        return null;
    }

    private Aluguer aluguerDoRS(ResultSet rs) throws SQLException {
        return new Aluguer(rs.getInt("id"), rs.getInt("id_cliente"), rs.getString("matricula_viatura"), rs.getString("pacote"), rs.getInt("quantidade"), rs.getDouble("valor_total"), rs.getInt("devolvido") == 1);
    }

    public ArrayList<Aluguer> listarAlugueres() {
        ArrayList<Aluguer> lista = new ArrayList<>();
        try (Connection con = ligar(); Statement st = con.createStatement(); ResultSet rs = st.executeQuery("SELECT * FROM alugueres ORDER BY id DESC")) {
            while (rs.next()) lista.add(aluguerDoRS(rs));
        } catch (SQLException e) { }
        return lista;
    }

    // ================= RELATÓRIOS =================

    public int totalViaturas() { return contar("viaturas", null, null); }
    public int totalClientes() { return contar("clientes", null, null); }
    public int totalAlugueres() { return contar("alugueres", null, null); }
    public int contarViaturasPorEstado(String estado) { return contar("viaturas", "estado", estado); }

    private int contar(String tabela, String campo, String valor) {
        String sql = campo == null ? "SELECT COUNT(*) FROM " + tabela : "SELECT COUNT(*) FROM " + tabela + " WHERE LOWER(" + campo + ")=LOWER(?)";
        try (Connection con = ligar(); PreparedStatement ps = con.prepareStatement(sql)) {
            if (campo != null) ps.setString(1, valor);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? rs.getInt(1) : 0; }
        } catch (SQLException e) { return 0; }
    }

    public double receitaTotal() {
        try (Connection con = ligar(); Statement st = con.createStatement(); ResultSet rs = st.executeQuery("SELECT SUM(valor_total) FROM alugueres")) {
            return rs.next() ? rs.getDouble(1) : 0;
        } catch (SQLException e) { return 0; }
    }

    public String guardarRelatorioCSV() {
        String nome = "relatorio_easyrent.csv";
        try (PrintWriter pw = new PrintWriter(new FileWriter(nome))) {
            pw.println("Indicador;Valor");
            pw.println("Total de viaturas;" + totalViaturas());
            pw.println("Viaturas disponíveis;" + contarViaturasPorEstado("Disponível"));
            pw.println("Viaturas alugadas;" + contarViaturasPorEstado("Alugada"));
            pw.println("Viaturas em manutenção;" + contarViaturasPorEstado("Manutenção"));
            pw.println("Total de clientes;" + totalClientes());
            pw.println("Total de alugueres;" + totalAlugueres());
            pw.println("Receita total;" + String.format("%.0f", receitaTotal()) + " ECV");
            return "Relatório guardado com sucesso em: " + new File(nome).getAbsolutePath();
        } catch (IOException e) { return "Erro ao guardar relatório: " + e.getMessage(); }
    }

    // Mantido para compatibilidade com telas antigas. Agora os dados são guardados automaticamente na BD.
    public void guardarDados() { }
}
