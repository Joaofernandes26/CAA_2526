package easyrent;

public class Cliente {

    //atributos
    private int id;
    private String nome;
    private String telefone;
    private String nif;
    private String morada;

    //Construtor
    public Cliente(int id, String nome, String telefone, String nif, String morada) {
        this.id = id;
        this.nome = nome;
        this.telefone = telefone;
        this.nif = nif;
        this.morada = morada;
    }

    //Metudo Get
    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public String getNif() {
        return nif;
    }

    public String getMorada() {
        return morada;
    }

    //Metudo Set
    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public void setNif(String nif) {
        this.nif = nif;
    }

    public void setMorada(String morada) {
        this.morada = morada;
    }

    public String toCSV() {
        return id + ";" + nome + ";" + telefone + ";" + nif + ";" + morada;
    }

    @Override
    public String toString() {
        return id + " | " + nome + " | Tel: " + telefone + " | NIF: " + nif + " | Morada: " + morada;
    }
}
