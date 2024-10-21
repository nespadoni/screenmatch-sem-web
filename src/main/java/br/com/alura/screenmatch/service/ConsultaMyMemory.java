package br.com.alura.screenmatch.service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import org.json.JSONObject;

public class ConsultaMyMemory {

    private static final String ENDPOINT = "https://api.mymemory.translated.net/get";

    public static String obterTraducao(String texto) {
        String idiomaOrigem = "en";  // Idioma do texto original
        String idiomaDestino = "pt";  // Idioma para traduzir

        try {
            String urlStr = String.format("%s?q=%s&langpair=%s|%s",
                    ENDPOINT, texto.replace(" ", "%20"), idiomaOrigem, idiomaDestino);
            URL url = new URL(urlStr);

            HttpURLConnection conexao = (HttpURLConnection) url.openConnection();
            conexao.setRequestMethod("GET");
            conexao.connect();

            int codigoResposta = conexao.getResponseCode();
            if (codigoResposta != 200) {
                throw new RuntimeException("Erro HTTP: " + codigoResposta);
            }

            Scanner scanner = new Scanner(url.openStream());
            StringBuilder respostaJson = new StringBuilder();
            while (scanner.hasNext()) {
                respostaJson.append(scanner.nextLine());
            }
            scanner.close();

            JSONObject json = new JSONObject(respostaJson.toString());
            return json.getJSONObject("responseData").getString("translatedText");

        } catch (IOException e) {
            e.printStackTrace();
            return "Erro na tradução";
        }
    }
}
