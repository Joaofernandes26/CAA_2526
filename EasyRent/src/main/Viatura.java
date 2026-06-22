public class Viatura {

    private String matricula;
    private String marca;
    private String modelo;
    private String estado;
    private double precoDiario;
    private double precoSemanal;
    private double precoMensal;

    public Viatura(String matricula, String marca, String modelo,
                   double precoDiario, double precoSemanal, double precoMensal) {

        this.matricula = matricula;
        this.marca = marca;
        this.modelo = modelo;
        this.estado = "Disponível";
        this.precoDiario = precoDiario;
        this.precoSemanal = precoSemanal;
        this.precoMensal = precoMensal;
    }

    public Viatura(String matricula, String marca, String modelo, String estado,
                   double precoDiario, double precoSemanal, double precoMensal) {

        this.matricula = matricula;
        this.marca = marca;
        this.modelo = modelo;
        this.estado = estado;
        this.precoDiario = precoDiario;
        this.precoSemanal = precoSemanal;
        this.precoMensal = precoMensal;
    }

    public String getMatricula() {
        return matricula;
    }

    public String getMarca() {
        return marca;
    }

    public String getModelo() {
        return modelo;
    }

    public String getEstado() {
        return estado;
    }

    public double getPrecoDiario() {
        return precoDiario;
    }

    public double getPrecoSemanal() {
        return precoSemanal;
    }

    public double getPrecoMensal() {
        return precoMensal;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public void setPrecoDiario(double precoDiario) {
        this.precoDiario = precoDiario;
    }

    public void setPrecoSemanal(double precoSemanal) {
        this.precoSemanal = precoSemanal;
    }

    public void setPrecoMensal(double precoMensal) {
        this.precoMensal = precoMensal;
    }

    public String toCSV() {
        return matricula + ";" + marca + ";" + modelo + ";" + estado + ";" +
                precoDiario + ";" + precoSemanal + ";" + precoMensal;
    }

    @Override
    public String toString() {
        return matricula + " | " + marca + " " + modelo + " | " + estado;
    }
}