package jdev.lojavirtual_fs.lojavirtual_fs;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jdev.lojavirtual_fs.lojavirtual_fs.dto.EmpresaTransporteDTO;
import jdev.lojavirtual_fs.lojavirtual_fs.enums.ApiTokenIntegracao;
import okhttp3.*;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
/*
        OkHttpClient client = new OkHttpClient();

MediaType mediaType = MediaType.parse("application/json");
RequestBody body = RequestBody.create(mediaType, "{\"options\":{\"receipt\":true,\"own_hand\":true,\"reverse\":true,\"non_commercial\":true}}");
Request request = new Request.Builder()
  .url("https://sandbox.melhorenvio.com.br/api/v2/me/cart")
  .post(body)
  .addHeader("Accept", "application/json")
  .addHeader("Content-Type", "application/json")
  .addHeader("Authorization", "Bearer token")
  .addHeader("User-Agent", "Aplicação (email para contato técnico)")
  .build();

Response response = client.newCall(request).execute();

        */

/**
 AJUSTE CORRETO PARA USO DA API
 * */
public class TesteAPIMelhorEnvio {

    public static void main(String[] args) throws Exception {
        /* PASSO 1 - Insere as Etiquetas de frete

        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/json");

        // JSON completo para criação de carrinho
        String jsonBody = "{"
                + "\"service\": \"1\","
                + "\"agency\": 123,"
                + "\"from\": {"
                + "  \"name\": \"Remetente Teste\","
                + "  \"phone\": \"(11) 99999-9999\","
                + "  \"email\": \"remetente@teste.com\","
                + "  \"document\": \"264.923.478.46\","
                + "  \"company_document\": \"28.013.432/0001-56\","
                + "  \"state_register\": \"123.456.789\","
                + "  \"address\": \"Rua do Remetente\","
                + "  \"complement\": \"Sala 1\","
                + "  \"number\": \"123\","
                + "  \"district\": \"Centro\","
                + "  \"city\": \"Pelotas\","
                + "  \"state_abbr\": \"RS\","
                + "  \"country_id\": \"BR\","
                + "  \"postal_code\": \"96020360\","
                + "  \"note\": \"Observação do remetente\""
                + "},"
                + "\"to\": {"
                + "  \"name\": \"Destinatário Teste\","
                + "  \"phone\": \"(11) 88888-8888\","
                + "  \"email\": \"destinatario@teste.com\","
                + "  \"document\": \"987.654.321-00\","
                + "  \"company_document\": \"32.837.684/0001-77\","
                + "  \"state_register\": \"987.654.321\","
                + "  \"address\": \"Rua do Destinatário\","
                + "  \"complement\": \"Apto 101\","
                + "  \"number\": \"456\","
                + "  \"district\": \"Centro\","
                + "  \"city\": \"São Paulo\","
                + "  \"state_abbr\": \"SP\","
                + "  \"country_id\": \"BR\","
                + "  \"postal_code\": \"01018020\","
                + "  \"note\": \"Observação do destinatário\""
                + "},"
                + "\"products\": ["
                + "  {"
                + "    \"name\": \"Produto Teste 1\","
                + "    \"quantity\": 1,"
                + "    \"unitary_value\": 50.00,"
                + "    \"weight\": 0.3,"
                + "    \"width\": 11,"
                + "    \"height\": 17,"
                + "    \"length\": 11,"
                + "    \"unitary_price\": 50.00"
                + "  }"
                + "],"
                + "\"options\": {"
                + "  \"insurance_value\": 50.00,"
                + "  \"receipt\": false,"
                + "  \"own_hand\": false,"
                + "  \"reverse\": false,"
                + "  \"non_commercial\": false"
                + "},"
                + "\"volumes\": ["
                + "  {"
                + "    \"height\": 17,"
                + "    \"width\": 11,"
                + "    \"length\": 11,"
                + "    \"weight\": 0.3"
                + "  }"
                + "]"
                + "}";

        RequestBody body = RequestBody.create(mediaType, jsonBody);

        System.out.println("JSON sendo enviado:");
        System.out.println(jsonBody);
        System.out.println();

        Request request = new Request.Builder()
                .url(ApiTokenIntegracao.URL_MELHOR_ENVIO_SANDBOX + "api/v2/me/cart")
                .post(body)
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + ApiTokenIntegracao.TOKEN_MELHOR_ENVIO_SANDBOX)
                .addHeader("User-Agent", "faustus.gomes@gmail.com")
                .build();

        try (Response response = client.newCall(request).execute()) {
            System.out.println("Status Code: " + response.code());
            System.out.println("Mensagem: " + response.message());

            String responseBody = response.body().string();
            System.out.println("Resposta da API:");
            System.out.println(responseBody);

            if (!response.isSuccessful()) {
                System.err.println("Erro na requisição! Status: " + response.code());
                return;
            }

            // Processamento da resposta
            JsonNode jsonNode = new ObjectMapper().readTree(responseBody);

            // Se a resposta for um objeto único (não array)
            if (jsonNode.has("id")) {
                EmpresaTransporteDTO empresaTransporteDTO = new EmpresaTransporteDTO();

                empresaTransporteDTO.setId(jsonNode.get("id").asText());

                if (jsonNode.has("protocol")) {
                    empresaTransporteDTO.setNome(jsonNode.get("protocol").asText());
                }

                if (jsonNode.has("total")) {
                    empresaTransporteDTO.setValor(jsonNode.get("total").asText());
                }

                if (jsonNode.has("status")) {
                    empresaTransporteDTO.setEmpresa(jsonNode.get("status").asText());
                }

                System.out.println("Carrinho criado com sucesso:");
                System.out.println("ID: " + empresaTransporteDTO.getId());
                System.out.println("Protocolo: " + empresaTransporteDTO.getNome());
                System.out.println("Total: " + empresaTransporteDTO.getValor());
                System.out.println("Status: " + empresaTransporteDTO.getEmpresa());
            }
        }*/

        /**
         PASSO 2- Compra de fretes
         * */

        /*OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json");

        // Body MÍNIMO - apenas o ID do frete que você quer comprar
        // Substitua "123456" pelo ID real que você recebeu na criação do carrinho
        String jsonBody = "{\"orders\": [\"a10885de-f23d-471f-a17d-bf2e4fcaa5da\"]}";

        RequestBody body = RequestBody.create(mediaType, jsonBody);

        Request request = new Request.Builder() //.url(ApiTokenIntegracao.URL_MELHOR_ENVIO_SANDBOX +
                .url(ApiTokenIntegracao.URL_MELHOR_ENVIO_SANDBOX + "api/v2/me/shipment/checkout")
                .post(body)
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + ApiTokenIntegracao.TOKEN_MELHOR_ENVIO_SANDBOX)
                .addHeader("User-Agent", "faustus.gomes@gmail.com")
                .build();

        try (Response response = client.newCall(request).execute()) {
            System.out.println("Status Code: " + response.code());
            System.out.println("Resposta: " + response.body().string());
        }*/

        /**
         * PASSO 3 - GERA AS ETIQUETAS
         * */
        /*OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json");

        //Meu ID Carrinho
        String jsonBody = "{\"orders\": [\"a10885de-f23d-471f-a17d-bf2e4fcaa5da\"]}";

        //Criar o RequestBody a partir do JSON
        RequestBody body = RequestBody.create(mediaType, jsonBody);

        Request request = new Request.Builder()
                .url(ApiTokenIntegracao.URL_MELHOR_ENVIO_SANDBOX + "api/v2/me/shipment/generate")
                .post(body)
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + ApiTokenIntegracao.TOKEN_MELHOR_ENVIO_SANDBOX)

                .addHeader("User-Agent", "faustus.gomes@gmail.com")
                // Opcional: formato da etiqueta
                .addHeader("Accept", "application/pdf") // se quiser receber PDF direto
                .build();

        try (Response response = client.newCall(request).execute()) {
            System.out.println("Status Code: " + response.code());
            System.out.println("Resposta: " + response.body().string());
        }
        */

        /**
         * FAZ IMPRESSAO DAS ETIQUETAS - GERAR LINK
         */
        try {
            OkHttpClient client = new OkHttpClient();

            String idEtiqueta = "a11117b9-b8c7-49ac-bde1-4de685f81963";

            // ENDPOINT CORRETO: GET com /link no final
            Request request = new Request.Builder()
                    .url(ApiTokenIntegracao.URL_MELHOR_ENVIO_SANDBOX + "api/v2/me/imprimir/" + idEtiqueta + "/link")
                    .get()
                    .addHeader("Accept", "application/json")
                    .addHeader("Authorization", "Bearer " + ApiTokenIntegracao.TOKEN_MELHOR_ENVIO_SANDBOX)
                    .addHeader("User-Agent", "faustus.gomes@gmail.com")
                    .build();

            try (Response response = client.newCall(request).execute()) {
                System.out.println("Status: " + response.code());

                String resposta = response.body().string();
                System.out.println("Resposta bruta: " + resposta);

                if (response.isSuccessful()) {
                    // Parse do JSON para extrair a URL
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode jsonNode = mapper.readTree(resposta);

                    String urlEtiqueta = jsonNode.get("url").asText();

                    System.out.println("\n✅ LINK GERADO COM SUCESSO!");
                    System.out.println("🔗 URL da etiqueta: " + urlEtiqueta);
                    System.out.println("\n👉 Copie este link e cole no navegador para visualizar a etiqueta:");
                    System.out.println(urlEtiqueta);

                    // Opcional: Abrir automaticamente no navegador
                    try {
                        java.awt.Desktop.getDesktop().browse(new java.net.URI(urlEtiqueta));
                        System.out.println("✅ Navegador aberto automaticamente!");
                    } catch (Exception e) {
                        System.out.println("⚠️ Abra o link manualmente no navegador");
                    }

                } else {
                    System.err.println("❌ Erro ao gerar link!");

                    if (response.code() == 404) {
                        System.err.println("   Etiqueta não encontrada. Verifique se:");
                        System.err.println("   - O ID '" + idEtiqueta + "' está correto");
                        System.err.println("   - A etiqueta já foi gerada (POST /generate)");
                        System.err.println("   - O pedido está pago/comprado (checkout)");
                    } else if (response.code() == 403) {
                        System.err.println("   Erro de autorização. Verifique seu token.");
                    } else if (response.code() == 401) {
                        System.err.println("   Token inválido ou expirado.");
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("❌ Erro na requisição: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
