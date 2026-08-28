package tarefa02;

public class CarroTeste {
    public static void main(String[] args) {
        Carro carro1 = new Carro();
        carro1.marca = "Toyota";
        carro1.modelo = "Corolla";
        carro1.ano = 2022;

        Carro carro2 = new Carro();
        carro2.marca = "Honda";
        carro2.modelo = "Civic";
        carro2.ano = 2020;

        carro1.ano = 2023;

        System.out.println("Carro 1: " + carro1.marca + " " + carro1.modelo + ", Ano: " + carro1.ano);
        System.out.println("Carro 2: " + carro2.marca + " " + carro2.modelo + ", Ano: " + carro2.ano);

        // A alteração em um carro não afetou o outro porque, ao utilizarmos a palavra "new", cada objeto instanciado é criado em um espaço de memória completamente diferente e independente. Assim, "carro1" e "carro2" possuem suas próprias cópias individuais dos atributos da classe.
    }
}