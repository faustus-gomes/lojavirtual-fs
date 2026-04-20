package jdev.lojavirtual_fs.lojavirtual_fs.controller;

import jdev.lojavirtual_fs.lojavirtual_fs.dto.VendaCompraLojaVirtualDTO;
import jdev.lojavirtual_fs.lojavirtual_fs.model.VendaCompraLojaVirtual;
import jdev.lojavirtual_fs.lojavirtual_fs.repository.VdCpLojaVirtRepository;
import jdev.lojavirtual_fs.lojavirtual_fs.service.VendaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.io.Serializable;

@Controller
public class PagamentoController implements Serializable {
        private static final long serialVersionUID = 1L;
        @Autowired
        private VdCpLojaVirtRepository vdCpLojaVirtRepository;
        @Autowired
        private VendaService vendaService;
        @RequestMapping(method = RequestMethod.GET, value = "/pagamento/{idVendaCompra}")
        public ModelAndView pagamento(@PathVariable(value = "idVendaCompra",
                required = false)String idVendaCompra) {

           ModelAndView modelAndView = new ModelAndView("Pagamento");

           VendaCompraLojaVirtual compraLojaVirtual = vdCpLojaVirtRepository.findByIdExclusao(Long.parseLong(idVendaCompra));

            if (compraLojaVirtual == null) {
               modelAndView.addObject("Venda", new VendaCompraLojaVirtualDTO());
            }else {
               modelAndView.addObject("venda", vendaService.consultaVenda(compraLojaVirtual));
            }
            return modelAndView;
        }
}
