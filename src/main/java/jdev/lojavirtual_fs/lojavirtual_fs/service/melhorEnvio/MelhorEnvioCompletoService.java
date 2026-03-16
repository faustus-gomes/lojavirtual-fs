package jdev.lojavirtual_fs.lojavirtual_fs.service.melhorEnvio;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import jdev.lojavirtual_fs.lojavirtual_fs.dto.*;
import jdev.lojavirtual_fs.lojavirtual_fs.enums.ApiTokenIntegracao;
import jdev.lojavirtual_fs.lojavirtual_fs.model.ItemVendaLoja;
import jdev.lojavirtual_fs.lojavirtual_fs.model.VendaCompraLojaVirtual;
import jdev.lojavirtual_fs.lojavirtual_fs.repository.VdCpLojaVirtRepository;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class MelhorEnvioCompletoService {

    @Autowired
    private VdCpLojaVirtRepository vdCpLojaVirtRepository;

    private EnvioEtiquetaDTO envioEtiquetaDTO;

    private ProductsEnvioEtiquetaDTO productsEnvioEtiquetaDTO;
    // 1. Adicionar serviço ao carrinho
    public String adicionarServicoAoCarrinho(String cartId, String serviceId) throws IOException {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType mediaType = MediaType.parse("application/json");
        String json = String.format("{\"service\": %s, \"agency\": 123}", serviceId);

        okhttp3.RequestBody body = okhttp3.RequestBody.create(mediaType, json);
        Request request = new Request.Builder()
                .url(ApiTokenIntegracao.URL_MELHOR_ENVIO_SANDBOX + "api/v2/me/cart/" + cartId + "/shipment")
                .post(body)
                .addHeader("Authorization", "Bearer " + ApiTokenIntegracao.TOKEN_MELHOR_ENVIO_SANDBOX)
                .addHeader("User-Agent", "faustus.gomes@gmail.com")
                .build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();
            if (!response.isSuccessful()) {
                throw new RuntimeException("Erro ao adicionar serviço: " + responseBody);
            }

            JsonNode jsonNode = new ObjectMapper().readTree(responseBody);
            return jsonNode.get("id").asText();
        }
    }

    // 2. Comprar etiqueta (checkout)
    public String comprarEtiqueta(String cartId) throws IOException {
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json");

        String json = String.format("{\"orders\": [\"%s\"]}", cartId);
        okhttp3.RequestBody body = okhttp3.RequestBody.create(mediaType, json);

        Request request = new Request.Builder()
                .url(ApiTokenIntegracao.URL_MELHOR_ENVIO_SANDBOX + "api/v2/me/shipment/checkout")
                .post(body)
                .addHeader("Authorization", "Bearer " + ApiTokenIntegracao.TOKEN_MELHOR_ENVIO_SANDBOX)
                .addHeader("User-Agent", "faustus.gomes@gmail.com")
                .build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();
            if (!response.isSuccessful()) {
                throw new RuntimeException("Erro no checkout: " + responseBody);
            }

            JsonNode jsonNode = new ObjectMapper().readTree(responseBody);
            // Retorna o primeiro order ID
            return jsonNode.get("purchases").get(0).get("id").asText();
        }
    }

    // 3. Gerar PDF da etiqueta
    public byte[] gerarEtiquetaPDF(String orderId) throws IOException {
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json");

        String json = String.format("{\"orders\": [\"%s\"], \"mode\": \"private\"}", orderId);
        okhttp3.RequestBody body = RequestBody.create(mediaType, json);

        Request request = new Request.Builder()
                .url(ApiTokenIntegracao.URL_MELHOR_ENVIO_SANDBOX + "api/v2/me/shipment/print")
                .post(body)
                .addHeader("Authorization", "Bearer " + ApiTokenIntegracao.TOKEN_MELHOR_ENVIO_SANDBOX)
                .addHeader("User-Agent", "faustus.gomes@gmail.com")
                .build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();
            if (!response.isSuccessful()) {
                throw new RuntimeException("Erro ao gerar PDF: " + responseBody);
            }

            JsonNode jsonNode = new ObjectMapper().readTree(responseBody);
            // O PDF vem em base64
            String base64Pdf = jsonNode.get("url").asText(); // ou "base64" dependendo da resposta

            // Se for URL, faz o download
            if (base64Pdf.startsWith("http")) {
                return downloadPDF(base64Pdf);
            }

            // Se for base64, decodifica
            return Base64.getDecoder().decode(base64Pdf);
        }
    }

    private byte[] downloadPDF(String url) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().bytes();
        }
    }

    // 4. Método completo que integra tudo
    @Transactional
    public String processoCompletoEtiqueta(Long idVenda, String servicoEscolhido) throws Exception {
        VendaCompraLojaVirtual venda = vdCpLojaVirtRepository.findById(idVenda)
                .orElseThrow(() -> new Exception("Venda não encontrada"));

        // Passo 1: Criar carrinho (seu método existente)
        String cartId = criarCarrinho(venda); // Adapte seu método existente

        // Passo 2: Adicionar serviço
        String shipmentId = adicionarServicoAoCarrinho(cartId, servicoEscolhido);

        // Passo 3: Comprar etiqueta
        String orderId = comprarEtiqueta(cartId);

        // Passo 4: Gerar PDF
        byte[] pdf = gerarEtiquetaPDF(orderId);

        // Passo 5: Salvar tudo
        venda.setCodigoEtiqueta(orderId);
        //venda.setPdfEtiqueta(pdf); // Se tiver campo BLOB
        //venda.setStatusEtiqueta("GERADA");
        vdCpLojaVirtRepository.save(venda);

        // Passo 6: Salvar PDF em disco se necessário
        String caminhoPdf = salvarPDFEmDisco(pdf, orderId);

        return orderId;
    }

    public String criarCarrinho(VendaCompraLojaVirtual compraLojaVirtual) throws Exception{
        try {
            EnvioEtiquetaDTO envioEtiquetaDTO = new EnvioEtiquetaDTO();

            // Configuração do remetente (from)
            envioEtiquetaDTO.setService(compraLojaVirtual.getServicoTransportadora());
            envioEtiquetaDTO.setAgency("123");

            // Validação dos Endereços
            if(compraLojaVirtual.getEmpresa().getEnderecos() == null ||
                    compraLojaVirtual.getEmpresa().getEnderecos().isEmpty()) {
                throw new Exception("Endereço da empresa não cadastrada");
            }

            // Configuração do remetente
            envioEtiquetaDTO.getFrom().setName(compraLojaVirtual.getEmpresa().getNome());
            envioEtiquetaDTO.getFrom().setPhone(compraLojaVirtual.getEmpresa().getTelefone());
            envioEtiquetaDTO.getFrom().setEmail(compraLojaVirtual.getEmpresa().getEmail());
            envioEtiquetaDTO.getFrom().setDocument(compraLojaVirtual.getPessoa().getCpf());
            envioEtiquetaDTO.getFrom().setCompany_document(compraLojaVirtual.getEmpresa().getCnpj());
            envioEtiquetaDTO.getFrom().setState_register(compraLojaVirtual.getEmpresa().getInscEstadual());
            envioEtiquetaDTO.getFrom().setAddress(compraLojaVirtual.getEmpresa().getEnderecos().get(0).getRuaLogra());
            envioEtiquetaDTO.getFrom().setComplement(compraLojaVirtual.getEmpresa().getEnderecos().get(0).getComplemento());
            envioEtiquetaDTO.getFrom().setNumber(compraLojaVirtual.getEmpresa().getEnderecos().get(0).getNumero());
            envioEtiquetaDTO.getFrom().setDistrict(compraLojaVirtual.getEmpresa().getEnderecos().get(0).getBairro());
            envioEtiquetaDTO.getFrom().setCity(compraLojaVirtual.getEmpresa().getEnderecos().get(0).getCidade());
            envioEtiquetaDTO.getFrom().setState_abbr(compraLojaVirtual.getEmpresa().getEnderecos().get(0).getUf());
            envioEtiquetaDTO.getFrom().setCountry_id("BR");
            envioEtiquetaDTO.getFrom().setPostal_code(compraLojaVirtual.getEmpresa().getEnderecos().get(0).getCep());
            envioEtiquetaDTO.getFrom().setNote("Sem informação");

            // Configuração do destinatário (to)
            if (compraLojaVirtual.getPessoa().enderecoEntrega() == null) {
                throw new Exception("Endereço de entrega não cadastrado");
            }

            envioEtiquetaDTO.getTo().setName(compraLojaVirtual.getPessoa().getNome());
            envioEtiquetaDTO.getTo().setPhone(compraLojaVirtual.getPessoa().getTelefone());
            envioEtiquetaDTO.getTo().setEmail(compraLojaVirtual.getPessoa().getEmail());
            envioEtiquetaDTO.getTo().setDocument(compraLojaVirtual.getPessoa().getCpf());
            envioEtiquetaDTO.getTo().setCompany_document("000000");
            envioEtiquetaDTO.getTo().setState_register("000000");
            envioEtiquetaDTO.getTo().setAddress(compraLojaVirtual.getPessoa().enderecoEntrega().getRuaLogra());
            envioEtiquetaDTO.getTo().setComplement(compraLojaVirtual.getPessoa().enderecoEntrega().getComplemento());
            envioEtiquetaDTO.getTo().setNumber(compraLojaVirtual.getPessoa().enderecoEntrega().getNumero());
            envioEtiquetaDTO.getTo().setDistrict(compraLojaVirtual.getPessoa().enderecoEntrega().getBairro());
            envioEtiquetaDTO.getTo().setCity(compraLojaVirtual.getPessoa().enderecoEntrega().getCidade());
            envioEtiquetaDTO.getTo().setState_abbr(compraLojaVirtual.getPessoa().enderecoEntrega().getUf());
            envioEtiquetaDTO.getTo().setCountry_id("BR");
            envioEtiquetaDTO.getTo().setPostal_code(compraLojaVirtual.getPessoa().enderecoEntrega().getCep());
            envioEtiquetaDTO.getTo().setNote("Sem Informação");

            // Configuração dos produtos
            List<ProductsEnvioEtiquetaDTO> products = new ArrayList<>();
            for (ItemVendaLoja itemVendaLoja : compraLojaVirtual.getItemVendaLojas()) {
                ProductsEnvioEtiquetaDTO dto = new ProductsEnvioEtiquetaDTO();
                dto.setName(itemVendaLoja.getProduto().getNome());
                dto.setQuantity(itemVendaLoja.getQuantidade().toString());
                dto.setUnitary_value(String.format("%.2f", itemVendaLoja.getProduto().getValorVenda()));
                products.add(dto);
            }
            envioEtiquetaDTO.setProducts(products);

            // Configuração das opções
            envioEtiquetaDTO.getOptions().setInsurance_value(String.format("%.2f", compraLojaVirtual.getValorTotal()));
            envioEtiquetaDTO.getOptions().setReceipt("false");
            envioEtiquetaDTO.getOptions().setOwn_hand("false");
            envioEtiquetaDTO.getOptions().setReverse("false");
            envioEtiquetaDTO.getOptions().setNon_commercial("false");

            // Configuração Volumes
            List<VolumesEnvioEtiquetaDTO> volumes = new ArrayList<>();
            for (ItemVendaLoja itemVendaLoja : compraLojaVirtual.getItemVendaLojas()) {
                VolumesEnvioEtiquetaDTO dto = new VolumesEnvioEtiquetaDTO();
                dto.setHeight(itemVendaLoja.getProduto().getAltura().toString());
                dto.setLength(itemVendaLoja.getProduto().getProfundidade().toString());
                dto.setWeight(itemVendaLoja.getProduto().getPeso().toString());
                dto.setWidth(itemVendaLoja.getProduto().getLargura().toString());
                volumes.add(dto);
            }
            envioEtiquetaDTO.setVolumes(volumes);

            // ================== CHAMADA API MELHOR ENVIO ===================
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();

            MediaType mediaType = MediaType.parse("application/json");
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonEnvio = objectMapper.writeValueAsString(envioEtiquetaDTO);

            System.out.println("JSON enviado para criar carrinho: " + jsonEnvio);

            okhttp3.RequestBody body = okhttp3.RequestBody.create(mediaType, jsonEnvio);

            Request request = new Request.Builder()
                    .url(ApiTokenIntegracao.URL_MELHOR_ENVIO_SANDBOX + "api/v2/me/cart")
                    .post(body)
                    .addHeader("Accept", "application/json")
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "Bearer " + ApiTokenIntegracao.TOKEN_MELHOR_ENVIO_SANDBOX)
                    .addHeader("User-Agent", "faustus.gomes@gmail.com")
                    .build();

            try (Response response = client.newCall(request).execute()) {
                String responseBody = response.body() != null ? response.body().string() : "";

                System.out.println("Status Code: " + response.code());
                System.out.println("Resposta da API: " + responseBody);

                if (!response.isSuccessful()) {
                    throw new Exception("Erro ao criar carrinho: " + response.code() + " - " + responseBody);
                }

                // Processamento da resposta para extrair o cartId
                JsonNode jsonNode = objectMapper.readTree(responseBody);
                String cartId = "";

                if (jsonNode.isArray() && jsonNode.size() > 0) {
                    cartId = jsonNode.get(0).get("id").asText();
                } else if (jsonNode.has("id")) {
                    cartId = jsonNode.get("id").asText();
                }

                if (cartId.isEmpty()) {
                    throw new Exception("ID do carrinho não encontrado na resposta: " + responseBody);
                }

                System.out.println("Carrinho criado com sucesso! ID: " + cartId);
                return cartId;
            }

        } catch (Exception e) {
            System.err.println("Erro ao criar carrinho: " + e.getMessage());
            e.printStackTrace();
            throw new Exception("Falha ao criar carrinho no Melhor Envio: " + e.getMessage(), e);
        }
    }

    private String salvarPDFEmDisco(byte[] pdf, String orderId) throws IOException {

        // Define o diretório base para salvar as etiquetas
        String diretorioBase = "/Users/faustusgomes/Downloads/etiquetas/"; // Linux/Mac
        // String diretorioBase = "C:\\etiquetas\\"; // Windows

        // Cria o diretório se não existir
        File diretorio = new File(diretorioBase);
        if (!diretorio.exists()) {
            diretorio.mkdirs();
        }

        // Gera um nome único para o arquivo
        String dataAtual = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String nomeArquivo = String.format("etiqueta_%s_%s.pdf", orderId, dataAtual);

        // Caminho completo do arquivo
        String caminhoCompleto = diretorioBase + nomeArquivo;

        // Salva o arquivo
        try (FileOutputStream fos = new FileOutputStream(caminhoCompleto)) {
            fos.write(pdf);
            fos.flush();
        }

        System.out.println("PDF salvo em: " + caminhoCompleto);

        // Retorna o caminho relativo ou absoluto conforme sua necessidade
        return caminhoCompleto; // ou retorne apenas "etiquetas/" + nomeArquivo se preferir caminho relativo
    }

    // MÉTODO CONSULTAR FRETE ***************************************
    public List<EmpresaTransporteDTO> consultarFrete(ConsultaFreteDTO consultaFreteDTO) throws Exception{
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonBody = objectMapper.writeValueAsString(consultaFreteDTO);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType mediaType = MediaType.parse("application/json");
        okhttp3.RequestBody body = okhttp3.RequestBody.create(mediaType, jsonBody);

        System.out.println("JSON enviado consulta frete: " + jsonBody);

        Request request = new Request.Builder()
                .url(ApiTokenIntegracao.URL_MELHOR_ENVIO_SANDBOX + "api/v2/me/shipment/calculate")
                .post(body)
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + ApiTokenIntegracao.TOKEN_MELHOR_ENVIO_SANDBOX)
                .addHeader("User-Agent", "faustus.gomes@gmail.com")
                .build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body() != null ? response.body().string() : "";

            System.out.println("Status Code consulta frete: " + response.code());
            System.out.println("Resposta consulta frete: " + responseBody);

            if (!response.isSuccessful()) {
                throw new Exception("Erro na consulta de frete: " + response.code() + " - " + responseBody);
            }

            JsonNode jsonNode = objectMapper.readTree(responseBody);
            List<EmpresaTransporteDTO> empresaTransporteDTOS = new ArrayList<>();

            if (jsonNode.isArray()) {
                Iterator<JsonNode> iterator = jsonNode.iterator();

                while (iterator.hasNext()) {
                    JsonNode node = iterator.next();
                    EmpresaTransporteDTO dto = new EmpresaTransporteDTO();

                    // Mapeamento dos campos
                    if (node.has("id") && !node.get("id").isNull()) {
                        dto.setId(node.get("id").asText());
                    }

                    if (node.has("name") && !node.get("name").isNull()) {
                        dto.setNome(node.get("name").asText());
                    }

                    if (node.has("price") && !node.get("price").isNull()) {
                        dto.setValor(node.get("price").asText());
                    }

                    if (node.has("company") && !node.get("company").isNull()) {
                        JsonNode company = node.get("company");
                        if (company.has("name") && !company.get("name").isNull()) {
                            dto.setEmpresa(company.get("name").asText());
                        }
                        if (company.has("picture") && !company.get("picture").isNull()) {
                            dto.setPicture(company.get("picture").asText());
                        }
                    }

                    // Campos adicionais úteis
                    if (node.has("delivery_time") && !node.get("delivery_time").isNull()) {
                        dto.setPrazoEntrega(node.get("delivery_time").asText() + " dias úteis");
                    }

                    if (node.has("delivery_time") && !node.get("delivery_time").isNull()) {
                        JsonNode deliveryTime = node.get("delivery_time");
                        if (deliveryTime.has("days") && !deliveryTime.get("days").isNull()) {
                            dto.setPrazoEntrega(deliveryTime.get("days").asText() + " dias úteis");
                        }
                    }

                    if (node.has("error") && !node.get("error").isNull()) {
                        dto.setErro(node.get("error").asText());
                    }

                    // Só adiciona se tiver os dados mínimos necessários
                    if (dto.dadosOK()) {
                        empresaTransporteDTOS.add(dto);
                    }
                }
            }

            System.out.println("Transportadoras encontradas: " + empresaTransporteDTOS.size());
            return empresaTransporteDTOS;
        }
    }
    public boolean cancelarEtiqueta(String orderId) throws Exception{

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType mediaType = MediaType.parse("application/json");
        String json = String.format("{\"order_id\": \"%s\"}", orderId);

        RequestBody body = RequestBody.create(mediaType, json);
        Request request = new Request.Builder()
                .url(ApiTokenIntegracao.URL_MELHOR_ENVIO_SANDBOX + "api/v2/me/shipment/cancel")
                .post(body)
                .addHeader("Authorization", "Bearer " + ApiTokenIntegracao.TOKEN_MELHOR_ENVIO_SANDBOX)
                .addHeader("User-Agent", "faustus.gomes@gmail.com")
                .build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body() != null ? response.body().string() : "";

            if (!response.isSuccessful()) {
                throw new Exception("Erro ao cancelar etiqueta: " + response.code() + " - " + responseBody);
            }

            // MESMO PADRÃO - USANDO findByCodigoEtiqueta
            VendaCompraLojaVirtual venda = vdCpLojaVirtRepository.findByCodigoEtiqueta(orderId);
            if (venda != null) {
                venda.setStatusEtiqueta("CANCELADA");
                vdCpLojaVirtRepository.save(venda);
                System.out.println("Etiqueta " + orderId + " cancelada para venda " + venda.getId());
            }

            return true;
        }
    }

    //  MÉTODO RASTREAR ENVIO ****************************
    public Map<String, Object> rastrearEnvio(String orderId) throws Exception {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url(ApiTokenIntegracao.URL_MELHOR_ENVIO_SANDBOX + "api/v2/me/shipment/tracking/" + orderId)
                .get()
                .addHeader("Authorization", "Bearer " + ApiTokenIntegracao.TOKEN_MELHOR_ENVIO_SANDBOX)
                .addHeader("User-Agent", "faustus.gomes@gmail.com")
                .build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body() != null ? response.body().string() : "";

            if (!response.isSuccessful()) {
                throw new Exception("Erro ao rastrear envio: " + response.code() + " - " + responseBody);
            }

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(responseBody);

            Map<String, Object> trackingInfo = new HashMap<>();

            // Extrair informações de rastreamento
            if (jsonNode.has("tracking") && !jsonNode.get("tracking").isNull()) {
                trackingInfo.put("codigoRastreio", jsonNode.get("tracking").asText());
            }

            if (jsonNode.has("status") && !jsonNode.get("status").isNull()) {
                trackingInfo.put("status", jsonNode.get("status").asText());
            }

            if (jsonNode.has("tracking_url") && !jsonNode.get("tracking_url").isNull()) {
                trackingInfo.put("urlRastreio", jsonNode.get("tracking_url").asText());
            }

            // SEU CÓDIGO - JÁ FUNCIONA PERFEITAMENTE
            VendaCompraLojaVirtual venda = vdCpLojaVirtRepository.findByCodigoEtiqueta(orderId);
            if (venda != null && trackingInfo.containsKey("codigoRastreio")) {
                venda.setCodigoRastreio(trackingInfo.get("codigoRastreio").toString());
                vdCpLojaVirtRepository.save(venda);
                System.out.println("Código de rastreio " + trackingInfo.get("codigoRastreio") +
                        " salvo para venda " + venda.getId());
            } else if (venda == null) {
                System.out.println("Venda com código de etiqueta " + orderId + " não encontrada");
            }

            return trackingInfo;
        }
    }
}


