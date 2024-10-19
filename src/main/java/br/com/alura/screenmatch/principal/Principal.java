package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.DadosEpisodio;
import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class Principal {

    private Scanner leitura = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();

    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=7978cf30";

    public void exibeMenu() {
        System.out.println("Digite o nome da série para a busca");
        var nomeSerie = leitura.nextLine();
        var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        ;
        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
//        System.out.println(dados);

        List<DadosTemporada> temporadas = new ArrayList<>();

        // i = numero das Temporadas, enquanto o i for menor que os dados, ele vai ir somando.
        for (int i = 1; i <= dados.totalTemporadas(); i++) {
            json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + "&season=" + i + API_KEY);
            DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
            temporadas.add(dadosTemporada);
        }

//        temporadas.forEach(System.out::println);

        //
//        for (int i = 0; i < dados.totalTemporadas(); i++) {
//            List<DadosEpisodio> episodiosTemporada = temporadas.get(i).episodios();
//            for (int j = 0; j < episodiosTemporada.size(); j++) {
//                System.out.println(episodiosTemporada.get(j).titulo());
//            }
//        }

//        temporadas.forEach(t -> t.episodios().forEach(e -> System.out.println(e.titulo())));

        List<DadosEpisodio> dadosEpisodios = temporadas.stream()
                // usar uma lista dentro de outra lista
                .flatMap(t -> t.episodios().stream())
                // Pega todos os episodios de todas as temporadas e joga dentro da lista de dados episodios
                // se for modificar a lista, usa o collect
                .collect(Collectors.toList());
                // se nao for modificar, usa o toList
//                .toList();

        System.out.println("\nTop 10 Episódios!");
        dadosEpisodios.stream()
                // vai tirar todos que sejam "N/A
                .filter(e -> !e.avaliacao().equalsIgnoreCase("N/A"))
                .peek(e -> System.out.println("Primeiro filtro (N/A) " + e))
                // compara todos os episodios com base na avaliação, e o reversed vai reverter a ordem da lista
                .sorted(Comparator.comparing(DadosEpisodio::avaliacao).reversed())
                .peek(e -> System.out.println("Ordenação " + e))
                // limita em 5 episodios
                .limit(10)
                .peek(e -> System.out.println("Limite " + e))
                .map(e -> e.titulo().toUpperCase())
                .peek(e -> System.out.println("Mapeamento " + e))
                // printa
                .forEach(System.out::println);

        List<Episodio> episodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream()
                        .map(d -> new Episodio(t.numero(), d)))
                .toList();

//        episodios.forEach(System.out::println);
//
//        System.out.println("A partir de que ano você deseja ver os episodios? ");
//        var ano = leitura.nextInt();
//        leitura.nextLine();
//
//        LocalDate dataBusca = LocalDate.of(ano, 1, 1);

        // formata o tipo de visualizacao da data = DIA / MES / ANO
//        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//
//        episodios.stream()
//                .filter(e -> e.getDataLancamento() != null && e.getDataLancamento().isAfter(dataBusca))
//                .forEach(e -> System.out.println(
//                        "Temporada : " + e.getTemporada() +
//                                "Episodio: " + e.getTitulo() +
//                                "Data de lançamento: " + e.getDataLancamento().format(formatador)
//                ));

        System.out.println("Digite um trecho do título do episódio: ");

        var trechoTitulo = leitura.nextLine();
        Optional<Episodio> episodioBuscado = episodios.stream()
                .filter(e -> e.getTitulo().toUpperCase().contains(trechoTitulo.toUpperCase()))
                .findFirst();

        if (episodioBuscado.isPresent()) {
            System.out.println("Episódio encontrado!");
            System.out.println("Temporada: " + episodioBuscado.get().getTemporada());
        } else {
            System.out.println("Episódio não encontrado!");
        }

        Map<Integer, Double> avaliacoesPorTemporada = episodios.stream()
                .filter(e -> e.getAvaliacao() > 0.0)
                .collect(Collectors.groupingBy(Episodio::getTemporada,
                        Collectors.averagingDouble(Episodio::getAvaliacao)));

        avaliacoesPorTemporada.forEach((temporada, media) ->
                System.out.printf("Temporada %d: %.2f%n", temporada, media));

        DoubleSummaryStatistics est = episodios.stream()
                .filter(e -> e.getAvaliacao() > 0.0)
                .collect(Collectors.summarizingDouble(Episodio::getAvaliacao));

        System.out.println("Média: " + est.getAverage());
        System.out.println("Melhor episódio: " + est.getMax());
        System.out.println("Pior episódio: " + est.getMin());
        System.out.println("Quantidade: " + est.getCount());
    }
}

//        List<String> nomes = Arrays.asList("Jacque", "Iasmin", "Paulo", "Rodrigo", "Nico");

//        nomes.stream()
//                // ordernar a lista
//                .sorted()
//                // limitar a 3 nomes
//                .limit(3)
//                // filtrar nomes que começam com N
//                .filter(n -> n.startsWith("N"))
//                // transformar o nome em letra maiuscula
//                .map(n -> n.toUpperCase())
//                // vai printar o "nomes"
//                .forEach(System.out::println);



//public class Principal {
//
//    private Scanner leitura = new Scanner(System.in);
//    private ConsumoApi consumo = new ConsumoApi();
//    private ConverteDados conversor = new ConverteDados();
//
//    private final String ENDERECO = "https://www.omdbapi.com/?t=";
//    private final String API_KEY = "&apikey=7978cf30";
//
//    public void exibeMenu() {
//        System.out.println("Digite o nome da série para busca: ");
//        var nomeSerie = leitura.nextLine();
//        var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ","+") + API_KEY);
//        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
//        System.out.println(json);
//    }
//}


