package jdev.lojavirtual_fs.lojavirtual_fs.controller.mellhorEnvio;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import jdev.lojavirtual_fs.lojavirtual_fs.dto.ConsultaFreteDTO;
import jdev.lojavirtual_fs.lojavirtual_fs.dto.EmpresaTransporteDTO;
import jdev.lojavirtual_fs.lojavirtual_fs.enums.ApiTokenIntegracao;
import jdev.lojavirtual_fs.lojavirtual_fs.model.VendaCompraLojaVirtual;
import jdev.lojavirtual_fs.lojavirtual_fs.repository.VdCpLojaVirtRepository;
import jdev.lojavirtual_fs.lojavirtual_fs.service.melhorEnvio.MelhorEnvioCompletoService;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/melhor-envio")
public class MelhorEnvioController {
       @Autowired
       private VdCpLojaVirtRepository vdCpLojaVirtRepository;
       @Autowired
       private MelhorEnvioCompletoService melhorEnvioCompletoService;

       private VendaCompraLojaVirtual vendaCompraLojaVirtual;

       //1- Processo Completo (Novo) ************************************
       @ResponseBody
       @PostMapping("/processoCompletoEtiqueta")
       public ResponseEntity<Map<String, Object>> processoCompletoEtiqueta(@RequestBody Long idVenda) {

              Map<String, Object> resposta = new HashMap<>();

              try {
                     // Busca a venda para obter o serviço
                     VendaCompraLojaVirtual venda = vdCpLojaVirtRepository.findById(idVenda).orElse(null);

                     if (venda == null) {
                            resposta.put("sucesso", false);
                            resposta.put("mensagem", "Venda não encontrada");
                            return new ResponseEntity<>(resposta, HttpStatus.NOT_FOUND);
                     }

                     // Pega o serviço da transportadora da venda
                     String servicoEscolhido = venda.getServicoTransportadora();

                     // Chama o método completo do service
                     String orderId = melhorEnvioCompletoService.processoCompletoEtiqueta(idVenda, servicoEscolhido);

                     // Busca a venda atualizada para pegar o caminho do PDF (opcional)
                     VendaCompraLojaVirtual vendaAtualizada = vdCpLojaVirtRepository.findById(idVenda).orElse(null);

                     // Prepara resposta
                     resposta.put("sucesso", true);
                     resposta.put("mensagem", "Processo completo realizado com sucesso!");
                     resposta.put("orderId", orderId);
                     resposta.put("codigoEtiqueta", vendaAtualizada != null ? vendaAtualizada.getCodigoEtiqueta() : orderId);
                     resposta.put("dataHora", new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));

                     return new ResponseEntity<>(resposta, HttpStatus.OK);

              } catch (Exception e) {
                     System.err.println("ERRO NO PROCESSO COMPLETO: " + e.getMessage());
                     e.printStackTrace();

                     resposta.put("sucesso", false);
                     resposta.put("mensagem", "Erro: " + e.getMessage());
                     resposta.put("dataHora", new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));

                     return new ResponseEntity<>(resposta, HttpStatus.INTERNAL_SERVER_ERROR);
              }

       }
       //2- ENDPOINT ORIGINAL (SÓ CRIAR CARRINHO) ************************************
       @ResponseBody
       @PostMapping("/imprimiCompraEtiquetaFrete")
       public ResponseEntity<String> imprimiCompraEtiquetaFrete(@RequestBody Long idVenda){

              try {
                     VendaCompraLojaVirtual venda = vdCpLojaVirtRepository.findById(idVenda).orElse(null);

                     if (venda == null) {
                            return new ResponseEntity<>("Venda não encontrada", HttpStatus.NOT_FOUND);
                     }

                     // Usa APENAS o método criarCarrinho do service
                     String cartId = melhorEnvioCompletoService.criarCarrinho(venda);

                     // Salva no banco (se quiser)
                     venda.setCodigoEtiqueta(cartId);
                     vdCpLojaVirtRepository.save(venda);

                     return new ResponseEntity<>(
                             String.format("Carrinho criado com sucesso! ID: %s", cartId),
                             HttpStatus.OK
                     );

              } catch (Exception e) {
                     System.err.println("Erro ao criar carrinho: " + e.getMessage());
                     e.printStackTrace();
                     return new ResponseEntity<>(
                             "Erro ao criar carrinho: " + e.getMessage(),
                             HttpStatus.INTERNAL_SERVER_ERROR
                     );
              }
       }

       //3- ENDPOINT DE CONSULTA FRETE (MANTIDO) ************************************
       @ResponseBody
       @PostMapping(value = "/ConsultaFreteLojaVirtual")
       public ResponseEntity<?> consultaFrete(@RequestBody @Valid ConsultaFreteDTO consultaFreteDTO){

              try {
                     // NOTA: Este método ainda precisa ser implementado no seu service
                     // Se não tiver, você pode manter a implementação original aqui

                     ObjectMapper objectMapper = new ObjectMapper();
                     String json = objectMapper.writeValueAsString(consultaFreteDTO);

                     OkHttpClient client = new OkHttpClient();

                     MediaType mediaType = MediaType.parse("application/json");
                     String jsonBody = json;

                     okhttp3.RequestBody body = okhttp3.RequestBody.create(mediaType, jsonBody);

                     System.out.println("JSON sendo enviado:");
                     System.out.println(jsonBody);

                     Request request = new Request.Builder()
                             .url(ApiTokenIntegracao.URL_MELHOR_ENVIO_SANDBOX + "api/v2/me/shipment/calculate")
                             .post(body)
                             .addHeader("Accept", "application/json")
                             .addHeader("Content-Type", "application/json")
                             .addHeader("Authorization", "Bearer " + ApiTokenIntegracao.TOKEN_MELHOR_ENVIO_SANDBOX)
                             .addHeader("User-Agent", "faustus.gomes@gmail.com")
                             .build();

                     try (Response response = client.newCall(request).execute()) {
                            String responseBody = response.body().string();

                            System.out.println("Status Code: " + response.code());
                            System.out.println("Resposta da API: " + responseBody);

                            if (!response.isSuccessful()) {
                                   return new ResponseEntity<>(
                                           "Erro na consulta: " + response.code() + " - " + responseBody,
                                           HttpStatus.valueOf(response.code())
                                   );
                            }

                            JsonNode jsonNode = new ObjectMapper().readTree(responseBody);
                            Iterator<JsonNode> iterator = jsonNode.iterator();

                            List<EmpresaTransporteDTO> empresaTransporteDTOS = new ArrayList<>();

                            while (iterator.hasNext()) {
                                   JsonNode node = iterator.next();
                                   EmpresaTransporteDTO empresaTransporteDTO = new EmpresaTransporteDTO();

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

                            return new ResponseEntity<>(empresaTransporteDTOS, HttpStatus.OK);
                     }

              } catch (Exception e) {
                     System.err.println("Erro consulta frete: " + e.getMessage());
                     e.printStackTrace();
                     return new ResponseEntity<>(
                             "Erro ao consultar frete: " + e.getMessage(),
                             HttpStatus.INTERNAL_SERVER_ERROR
                     );
              }
       }

       //4- ENDPOINT BAIXAR PDF (OPCIONAL) ************************************

       @ResponseBody
       @GetMapping("/baixarEtiqueta/{orderId}")
       public ResponseEntity<byte[]> baixarEtiqueta(@PathVariable String orderId) {
              try {
                     byte[] pdf = melhorEnvioCompletoService.gerarEtiquetaPDF(orderId);

                     org.springframework.http.HttpHeaders headers = new HttpHeaders();
                     headers.setContentType(org.springframework.http.MediaType.APPLICATION_PDF);
                     headers.setContentDispositionFormData("attachment", "etiqueta_" + orderId + ".pdf");
                     headers.setContentLength(pdf.length);

                     return new ResponseEntity<>(pdf, headers, HttpStatus.OK);

              } catch (Exception e) {
                     System.err.println("Erro ao baixar PDF: " + e.getMessage());
                     return new ResponseEntity<>(HttpStatus.NOT_FOUND);
              }
       }

       //5- VERIFICAR STATUS (OPCIONAL) ************************************

       @ResponseBody
       @GetMapping("/statusEtiqueta/{idVenda}")
       public ResponseEntity<Map<String, Object>> statusEtiqueta(@PathVariable Long idVenda) {

            Map<String, Object> resposta = new HashMap<>();

              try {
                     VendaCompraLojaVirtual venda = vdCpLojaVirtRepository.findById(idVenda).orElse(null);

                     if (venda == null) {
                            resposta.put("sucesso", false);
                            resposta.put("mensagem", "Venda não encontrada");
                            return new ResponseEntity<>(resposta, HttpStatus.NOT_FOUND);
                     }

                     resposta.put("sucesso", true);
                     resposta.put("idVenda", idVenda);
                     resposta.put("codigoEtiqueta", venda.getCodigoEtiqueta());
                     resposta.put("servicoTransportadora", venda.getServicoTransportadora());

                     return new ResponseEntity<>(resposta, HttpStatus.OK);

              } catch (Exception e) {
                     resposta.put("sucesso", false);
                     resposta.put("mensagem", e.getMessage());
                     return new ResponseEntity<>(resposta, HttpStatus.INTERNAL_SERVER_ERROR);
              }

       }
       @ResponseBody
       @PostMapping("/ConsultaFreteLojaVirtualMEnvio")
       public ResponseEntity<?> consultaFreteMEnvio(@RequestBody @Valid ConsultaFreteDTO consultaFreteDTO) {

              try {
                     List<EmpresaTransporteDTO> fretes = melhorEnvioCompletoService.consultarFrete(consultaFreteDTO);

                     if (fretes.isEmpty()) {
                            return new ResponseEntity<>(
                                    "Nenhuma transportadora disponível para esta consulta",
                                    HttpStatus.OK
                            );
                     }

                     return new ResponseEntity<>(fretes, HttpStatus.OK);

              } catch (Exception e) {
                     System.err.println("Erro consulta frete: " + e.getMessage());
                     e.printStackTrace();

                     Map<String, String> erro = new HashMap<>();
                     erro.put("erro", "Erro ao consultar frete");
                     erro.put("mensagem", e.getMessage());

                     return new ResponseEntity<>(erro, HttpStatus.INTERNAL_SERVER_ERROR);
              }
       }

       @ResponseBody
       @PostMapping("/cancelarEtiqueta/{orderId}")
       public ResponseEntity<Map<String,Object>> cancelarEtiqueta(@PathVariable String orderId){
              Map<String, Object> resposta = new HashMap<>();

              try {
                     boolean cancelado = melhorEnvioCompletoService.cancelarEtiqueta(orderId);

                     resposta.put("sucesso", cancelado);
                     resposta.put("mensagem", "Etiqueta cancelada com sucesso!");
                     resposta.put("orderId", orderId);

                     return new ResponseEntity<>(resposta, HttpStatus.OK);

              } catch (Exception e) {
                     resposta.put("sucesso", false);
                     resposta.put("mensagem", "Erro: " + e.getMessage());
                     return new ResponseEntity<>(resposta, HttpStatus.INTERNAL_SERVER_ERROR);
              }
       }
}
