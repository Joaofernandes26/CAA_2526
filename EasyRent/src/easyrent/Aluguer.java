package easyrent;

public class Aluguer {
    //Atributos
    private int id;
    private int idCliente;
    private String matriculaViatura;
    private String pacote;
    private int quantidade;
    private double valorTotal;
    private boolean devolvido;

    //Contrutor
    public Aluguer(int id, int idCliente, String matriculaViatura,String pacote, int quantidade, double valorTotal, boolean devolvido) {

        this.id = id;
        this.idCliente = idCliente;
        this.matriculaViatura = matriculaViatura;
        this.pacote = pacote;
        this.quantidade = quantidade;
        this.valorTotal = valorTotal;
        this.devolvido = devolvido;
    }

    //Metudos Get
    public int getId() {
        return id;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public String getMatriculaViatura() {
        return matriculaViatura;
    }

    public String getPacote() {
        return pacote;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public double getValorTotal() {
        return valorTotal;
    }

    public boolean isDevolvido() {
        return devolvido;
    }

    public void devolver() {
        this.devolvido = true;
    }

    public String toCSV() {
        return id + ";" + idCliente + ";" + matriculaViatura + ";" +
                pacote + ";" + quantidade + ";" + valorTotal + ";" + devolvido;
    }

    @Override
    public String toString() {
        return "Aluguer " + id + " | Cliente " + idCliente + " | " +
                matriculaViatura + " | " + valorTotal + " ECV | " +
                (devolvido ? "Devolvido" : "Activo");
    }
}