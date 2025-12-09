package jdev.lojavirtual_fs.lojavirtual_fs.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jdev.lojavirtual_fs.lojavirtual_fs.ExceptionLoja;
import jdev.lojavirtual_fs.lojavirtual_fs.dto.CategoriaProdutoDTO;
import jdev.lojavirtual_fs.lojavirtual_fs.model.Acesso;
import jdev.lojavirtual_fs.lojavirtual_fs.model.CategoriaProduto;
import jdev.lojavirtual_fs.lojavirtual_fs.repository.CategoriaProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class CategoriaProdutoController {
        @Autowired
        private CategoriaProdutoRepository categoriaProdutoRepository;
        //FUNÇÃO: ATUALIZA E INSERE
        @PostMapping(value = "/salvarCategoria")
        @Transactional
        public ResponseEntity<CategoriaProdutoDTO> salvarCategoria(
                @Valid @RequestBody CategoriaProduto categoriaProduto, HttpServletRequest request) {

            System.out.println("=== DEBUG DETALHADO ===");

            // 1. Verifique a categoria
            System.out.println("1. Categoria object: " + categoriaProduto);
            System.out.println("2. Categoria nomeDesc: " + categoriaProduto.getNomeDesc());

            // 2. Verifique a empresa
            if (categoriaProduto.getEmpresa() != null) {
                System.out.println("3. Empresa object: " + categoriaProduto.getEmpresa());
                System.out.println("4. Empresa class: " + categoriaProduto.getEmpresa().getClass().getName());
                System.out.println("5. Empresa ID: " + categoriaProduto.getEmpresa().getId());
                System.out.println("6. Empresa toString: " + categoriaProduto.getEmpresa().toString());

                // Use reflection para ver todos os campos
                System.out.println("7. Campos da empresa:");
                for (Field field : categoriaProduto.getEmpresa().getClass().getDeclaredFields()) {
                    field.setAccessible(true);
                    try {
                        Object value = field.get(categoriaProduto.getEmpresa());
                        System.out.println("   - " + field.getName() + ": " + value + " (type: " +
                                (value != null ? value.getClass().getName() : "null") + ")");
                    } catch (Exception e) {
                        System.out.println("   - " + field.getName() + ": ERROR accessing");
                    }
                }
            } else {
                System.out.println("3. Empresa é NULL");
            }

            // 3. Mostre o JSON original
            try {
                String json = request.getReader().lines().collect(Collectors.joining());
                System.out.println("8. JSON Original: " + json);
            } catch (Exception e) {
                System.out.println("8. Erro ao ler JSON: " + e.getMessage());
            }

            // Validação
            if (categoriaProduto.getEmpresa() == null || categoriaProduto.getEmpresa().getId() == null) {
                System.out.println("=== VALIDAÇÃO FALHOU ===");
                System.out.println("Empresa is null: " + (categoriaProduto.getEmpresa() == null));
                System.out.println("Empresa ID is null: " +
                        (categoriaProduto.getEmpresa() != null ? categoriaProduto.getEmpresa().getId() == null : "empresa is null"));
                throw new ExceptionLoja("Empresa é obrigatória. Informe o ID da empresa.");
            }
            /* ============================================================================== */
            System.out.println("=== RECEBENDO DADOS ===");
            System.out.println("Nome: " + categoriaProduto.getNomeDesc());
            System.out.println("Empresa ID: " + (categoriaProduto.getEmpresa() != null ? categoriaProduto.getEmpresa().getId() : "null"));

            // Validação manual (além das anotações @Valid)
            if (categoriaProduto.getNomeDesc() == null || categoriaProduto.getNomeDesc().trim().isEmpty()) {
                throw new ExceptionLoja("Nome/Descrição é obrigatório.");
            }

            if (categoriaProduto.getEmpresa() == null || categoriaProduto.getEmpresa().getId() == null) {
                throw new ExceptionLoja("Empresa é obrigatória. Informe o ID da empresa.");
            }

            if (categoriaProduto.getId() == null && categoriaProdutoRepository.existeCategoria(categoriaProduto.getNomeDesc().toUpperCase())) {
                throw new ExceptionLoja("Não pode cadastrar categoria com o mesmo nome");
            }

            try {
                CategoriaProduto categoriaSalva = categoriaProdutoRepository.save(categoriaProduto);

                System.out.println("=== CATEGORIA SALVA ===");
                System.out.println("ID: " + categoriaSalva.getId());
                System.out.println("Nome: " + categoriaSalva.getNomeDesc());
                System.out.println("Empresa ID: " + categoriaSalva.getEmpresa().getId());

                CategoriaProdutoDTO categoriaProdutoDTO = new CategoriaProdutoDTO();
                categoriaProdutoDTO.setId(categoriaSalva.getId());
                categoriaProdutoDTO.setNomeDesc(categoriaSalva.getNomeDesc());
                categoriaProdutoDTO.setEmpresa(categoriaSalva.getEmpresa().getId().toString());

                return ResponseEntity.status(HttpStatus.CREATED).body(categoriaProdutoDTO);

            } catch (Exception e) {
                System.err.println("=== ERRO AO SALVAR ===");
                e.printStackTrace();
                throw new ExceptionLoja("Erro ao salvar categoria: " + e.getMessage());
            }
        }
    //DELETE
    @ResponseBody
    @PostMapping(value = "/deleteCategoria")
    public ResponseEntity<?> deleteAcesso(@RequestBody CategoriaProduto categoriaProduto) {

        categoriaProdutoRepository.deleteById(categoriaProduto.getId());
        return new ResponseEntity("categoria produto Removido",HttpStatus.OK);
    }

    //CONSULTA
    @ResponseBody
    @GetMapping(value = "/buscarPorDescCategoria/{desc}")
    public ResponseEntity<List<CategoriaProduto>> buscarPorDesc(@PathVariable("desc")String desc) {

        List<CategoriaProduto> categoriaProdutos =  categoriaProdutoRepository.buscarCategoriaDesc(desc.toUpperCase());
        return new ResponseEntity<List<CategoriaProduto>>(categoriaProdutos,HttpStatus.OK);
    }

}
