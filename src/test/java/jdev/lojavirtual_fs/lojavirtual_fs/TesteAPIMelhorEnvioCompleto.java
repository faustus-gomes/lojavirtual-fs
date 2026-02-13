package jdev.lojavirtual_fs.lojavirtual_fs;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jdev.lojavirtual_fs.lojavirtual_fs.enums.ApiTokenIntegracao;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class TesteAPIMelhorEnvioCompleto {
    private static final String URL_SANDBOX = "https://sandbox.melhorenvio.com.br/";
    private static final String TOKEN = ApiTokenIntegracao.TOKEN_MELHOR_ENVIO_SANDBOX;
    private static final String USER_AGENT = "faustus.gomes@gmail.com";

    private static final OkHttpClient client = new OkHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final MediaType JSON = MediaType.parse("application/json");

    public static void main(String[] args) throws Exception {

        System.out.println("=== DIAGNÓSTICO COMPLETO - MELHOR ENVIO ===");

        // PASSO 1: Listar TODOS os pedidos para encontrar a etiqueta correta
        listarPedidos();

        // PASSO 2: Se você tem um código de rastreio, busque por ele
        String codigoRastreio = "QJ3721031BR"; // Substitua pelo seu código
        buscarPorRastreio(codigoRastreio);
    }

    /**
     * Lista todos os pedidos e mostra os IDs e status
     */
    public static void listarPedidos() throws IOException {
        System.out.println("\n📋 1. LISTANDO TODOS OS PEDIDOS:");
        System.out.println("----------------------------------------");

        Request request = new Request.Builder()
                .url(URL_SANDBOX + "api/v2/me/orders")
                .get()
                .addHeader("Accept", "application/json")
                .addHeader("Authorization", "Bearer " + TOKEN)
                .addHeader("User-Agent", USER_AGENT)
                .build();

        try (Response response = client.newCall(request).execute()) {
            System.out.println("Status: " + response.code());

            if (!response.isSuccessful()) {
                System.out.println("❌ Erro: " + response.body().string());
                return;
            }

            String resposta = response.body().string();
            JsonNode root = mapper.readTree(resposta);

            if (root.isArray()) {
                System.out.println("Total de pedidos encontrados: " + root.size());

                for (int i = 0; i < root.size(); i++) {
                    JsonNode pedido = root.get(i);

                    String id = pedido.path("id").asText();
                    String status = pedido.path("status").asText();
                    String tracking = pedido.path("freight").path("tracking").asText();
                    String createdAt = pedido.path("created_at").asText();

                    System.out.println("\n--- Pedido " + (i+1) + " ---");
                    System.out.println("   ID (UUID): " + id);
                    System.out.println("   Status: " + status);
                    System.out.println("   Rastreio: " + (tracking.isEmpty() ? "❌ NÃO TEM" : "✅ " + tracking));
                    System.out.println("   Criado em: " + createdAt);

                    // IMPORTANTE: Este ID é candidato para impressão SOMENTE se:
                    // 1. Status for "paid", "approved", "generated" ou "printed"
                    // 2. JÁ PASSOU pelo POST /generate
                    if (status.equals("generated") || status.equals("paid") || status.equals("approved")) {
                        System.out.println("   ✅ PODE GERAR LINK DE IMPRESSÃO COM ESTE ID!");
                    } else {
                        System.out.println("   ❌ NÃO PODE IMPRIMIR - Status incorreto");
                    }
                }
            } else {
                System.out.println("Resposta não é array: " + resposta);
            }
        }
    }

    /**
     * Busca especificamente pelo código de rastreio
     */
    public static void buscarPorRastreio(String codigoRastreio) throws IOException {
        System.out.println("\n🔍 2. BUSCANDO ETIQUETA PELO RASTREIO: " + codigoRastreio);
        System.out.println("----------------------------------------");

        Request request = new Request.Builder()
                .url(URL_SANDBOX + "api/v2/me/orders")
                .get()
                .addHeader("Accept", "application/json")
                .addHeader("Authorization", "Bearer " + TOKEN)
                .addHeader("User-Agent", USER_AGENT)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String resposta = response.body().string();
            JsonNode root = mapper.readTree(resposta);

            if (root.isArray()) {
                for (JsonNode pedido : root) {
                    String tracking = pedido.path("freight").path("tracking").asText();

                    if (tracking.equals(codigoRastreio)) {
                        System.out.println("✅ ETIQUETA ENCONTRADA!");
                        System.out.println("   ID do Pedido: " + pedido.path("id").asText());
                        System.out.println("   Status: " + pedido.path("status").asText());
                        System.out.println("   Serviço: " + pedido.path("service").asText());
                        System.out.println("   Total: R$ " + pedido.path("total").asText());

                        // Agora testa se este ID funciona para impressão
                        String idEncontrado = pedido.path("id").asText();
                        testarLinkImpressao(idEncontrado);
                        return;
                    }
                }
                System.out.println("❌ Nenhuma etiqueta encontrada com o rastreio: " + codigoRastreio);
            }
        }
    }

    /**
     * Testa se um ID gera link de impressão
     */
    public static void testarLinkImpressao(String id) throws IOException {
        System.out.println("\n🖨️ 3. TESTANDO LINK DE IMPRESSÃO PARA ID: " + id);
        System.out.println("----------------------------------------");

        Request request = new Request.Builder()
                .url(URL_SANDBOX + "api/v2/me/imprimir/" + id + "/link")
                .get()
                .addHeader("Accept", "application/json")
                .addHeader("Authorization", "Bearer " + TOKEN)
                .addHeader("User-Agent", USER_AGENT)
                .build();

        try (Response response = client.newCall(request).execute()) {
            System.out.println("Status: " + response.code());
            String resposta = response.body().string();

            if (response.isSuccessful()) {
                JsonNode node = mapper.readTree(resposta);
                String url = node.path("url").asText();
                System.out.println("✅ SUCESSO! Link gerado:");
                System.out.println(url);
            } else {
                System.out.println("❌ Erro: " + resposta);

                if (response.code() == 404) {
                    System.out.println("\n💡 MOTIVO DO ERRO:");
                    System.out.println("   Este ID NÃO é uma etiqueta pronta para impressão.");
                    System.out.println("   Provavelmente você NÃO chamou o POST /generate para este pedido.");
                }
            }
        }
    }
}
