public class Cliente {

    private int id;
    private String nome;
    private String telefone;
    private String nif;

    public Cliente(int id, String nome, String telefone, String nif) {
        this.id = id;
        this.nome = nome;
        this.telefone = telefone;
        this.nif = nif;
    }

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

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public void setNif(String nif) {
        this.nif = nif;
    }

    public String toCSV() {
        return id + ";" + nome + ";" + telefone + ";" + nif;
    }

    @Override
    public String toString() {
        return id + " | " + nome + " | Tel: " + telefone;
    }
}