package jdev.lojavirtual_fs.lojavirtual_fs;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jdev.lojavirtual_fs.lojavirtual_fs.dto.EmpresaTransporteDTO;
import jdev.lojavirtual_fs.lojavirtual_fs.enums.ApiTokenIntegracao;
import okhttp3.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TesteAPIMelhorEnvio {

    public static void main(String[] args) throws Exception {

        /*OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\"from\":{\"postal_code\":\"96020360\"},\"to\":{\"postal_code\":\"01018020\"},\"products\":[{\"id\":\"x\",\"width\":11,\"height\":17,\"length\":11,\"weight\":0.3,\"insurance_value\":10.1,\"quantity\":1},{\"id\":\"y\",\"width\":16,\"height\":25,\"length\":11,\"weight\":0.3,\"insurance_value\":55.05,\"quantity\":2},{\"id\":\"z\",\"width\":22,\"height\":30,\"length\":11,\"weight\":1,\"insurance_value\":30,\"quantity\":1}]}");
        Request request = new Request.Builder()
                .url(ApiTokenIntegracao.URL_MELHOR_ENVIO_SANDBOX +"api/v2/me/shipment/calculate")
                .post(body)
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + ApiTokenIntegracao.TOKEN_MELHOR_ENVIO_SANDBOX)
                .addHeader("User-Agent", "faustus.gomes@gmail.com") //e-mail de suporte
                .build();

        Response response = client.newCall(request).execute();
        System.out.println(response.body().toString());*/

        /**
         AJUSTE CORRETO PARA USO DA API
         * */
        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/json");
        String jsonBody = "{"
                + "\"from\": {\"postal_code\": \"96020360\"},"
                + "\"to\": {\"postal_code\": \"01018020\"},"
                + "\"products\": ["
                + "  {"
                + "    \"id\": \"x\","
                + "    \"width\": 11,"
                + "    \"height\": 17,"
                + "    \"length\": 11,"
                + "    \"weight\": 0.3,"
                + "    \"insurance_value\": 10.1,"
                + "    \"quantity\": 1"
                + "  },"
                + "  {"
                + "    \"id\": \"y\","
                + "    \"width\": 16,"
                + "    \"height\": 25,"
                + "    \"length\": 11,"
                + "    \"weight\": 0.3,"
                + "    \"insurance_value\": 55.05,"
                + "    \"quantity\": 2"
                + "  },"
                + "  {"
                + "    \"id\": \"z\","
                + "    \"width\": 22,"
                + "    \"height\": 30,"
                + "    \"length\": 11,"
                + "    \"weight\": 1,"
                + "    \"insurance_value\": 30,"
                + "    \"quantity\": 1"
                + "  }"
                + "]"
                + "}";

        RequestBody body = RequestBody.create(mediaType, jsonBody);

// Para debug: imprimir o JSON que está sendo enviado
        System.out.println("JSON sendo enviado:");
        System.out.println(jsonBody);
        System.out.println();

        Request request = new Request.Builder()
                .url(ApiTokenIntegracao.URL_MELHOR_ENVIO_SANDBOX + "api/v2/me/shipment/calculate")
                .post(body)
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + ApiTokenIntegracao.TOKEN_MELHOR_ENVIO_SANDBOX) // Note o espaço após "Bearer"
                .addHeader("User-Agent", "faustus.gomes@gmail.com")
                .build();

        try (Response response = client.newCall(request).execute()) {
            System.out.println("Status Code: " + response.code());
            System.out.println("Mensagem: " + response.message());

            String responseBody = response.body().string();
            System.out.println("Resposta da API:");
            System.out.println(responseBody);

            // Verificar se há erro
            if (!response.isSuccessful()) {
                System.err.println("Erro na requisição! Status: " + response.code());
            }

            JsonNode jsonNode = new ObjectMapper().readTree(responseBody);
            Iterator<JsonNode> iterator = jsonNode.iterator();

            List<EmpresaTransporteDTO> empresaTransporteDTOS = new ArrayList<EmpresaTransporteDTO>();

            while (iterator.hasNext()) {
                JsonNode node = iterator.next();

                EmpresaTransporteDTO empresaTransporteDTO = new EmpresaTransporteDTO();
                //System.out.println(node.get("name"));

                if (node.get("id") != null) {
                    empresaTransporteDTO.setId(node.get("id").asText());
                }

                if (node.get("name") != null) {
                    empresaTransporteDTO.setNome(node.get("name").asText());
                }

                if (node.get("price") != null) {
                    empresaTransporteDTO.setValor(node.get("price").asText());
                }

                if (node.get("company") != null) {
                    empresaTransporteDTO.setEmpresa(node.get("company").get("name").asText());
                    empresaTransporteDTO.setPicture(node.get("company").get("picture").asText());
                }

                if (empresaTransporteDTO.dadosOK()) {
                    empresaTransporteDTOS.add(empresaTransporteDTO);
                }

            }

            System.out.println(empresaTransporteDTOS);
        }

    }
}
