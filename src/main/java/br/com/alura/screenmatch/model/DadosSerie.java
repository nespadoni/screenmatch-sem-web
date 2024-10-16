package br.com.alura.screenmatch.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

// ele vai ignorar tudo o que não foi dito na linha 9
@JsonIgnoreProperties(ignoreUnknown = true)
public record DadosSerie(@JsonAlias({"Title", "Titulo"}) String titulo,
                         @JsonAlias("totalSeasons") Integer totalTemporadas,
                         @JsonAlias("imdbRating") String avaliacao,
                         @JsonProperty("imdbVotes") String votos ) {
}
