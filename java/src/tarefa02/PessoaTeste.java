package tarefa02;

public class PessoaTeste {
    public static void main(String[] args) {
        Pessoa p1 = new Pessoa();
        p1.nome = "Alice";
        p1.idade = 25;
        p1.altura = 1.65;

        Pessoa p2 = new Pessoa();
        p2.nome = "Beto";
        p2.idade = 32;
        p2.altura = 1.80;

        Pessoa p3 = new Pessoa();
        p3.nome = "Camila";
        p3.idade = 28;
        p3.altura = 1.70;

        if (p1.idade > p2.idade && p1.idade > p3.idade) {
            System.out.println(p1.nome + " é a pessoa mais velha.");
        } else if (p2.idade > p1.idade && p2.idade > p3.idade) {
            System.out.println(p2.nome + " é a pessoa mais velha.");
        } else {
            System.out.println(p3.nome + " é a pessoa mais velha.");
        }
    }
}