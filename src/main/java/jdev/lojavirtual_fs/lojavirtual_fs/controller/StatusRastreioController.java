package jdev.lojavirtual_fs.lojavirtual_fs.controller;

import jdev.lojavirtual_fs.lojavirtual_fs.model.StatusRastreio;
import jdev.lojavirtual_fs.lojavirtual_fs.repository.StatusRastreioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class StatusRastreioController {

    @Autowired
    private StatusRastreioRepository statusRastreioRepository;

    @ResponseBody
    @GetMapping(value = "/listaStatusRastreio/{idVenda}")
    public ResponseEntity<List<StatusRastreio>> listaStatusRastreio (@PathVariable("idVenda") long idVenda) {

        List<StatusRastreio> statusRastreios = statusRastreioRepository.listaRastreioVenda(idVenda);
        return new ResponseEntity<List<StatusRastreio>>(statusRastreios, HttpStatus.OK);
    }

}
