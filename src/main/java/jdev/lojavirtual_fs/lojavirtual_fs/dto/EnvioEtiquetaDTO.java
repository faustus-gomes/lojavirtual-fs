package jdev.lojavirtual_fs.lojavirtual_fs.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class EnvioEtiquetaDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String service;
    private String agency;
    private FromEnvioEtiquetaDTO from = new FromEnvioEtiquetaDTO();
    private ToEnvioEtiquetaDTO to = new ToEnvioEtiquetaDTO();

    //No JSON os Products tão dentro de [..], então é uam lista
    List<ProductsEnvioEtiquetaDTO> products = new ArrayList<ProductsEnvioEtiquetaDTO>();
    private OptionsEnvioEtiquetaDTO options = new OptionsEnvioEtiquetaDTO();
    List<VolumesEnvioEtiquetaDTO> volumes = new ArrayList<VolumesEnvioEtiquetaDTO>();

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getAgency() {
        return agency;
    }

    public void setAgency(String agency) {
        this.agency = agency;
    }

    public FromEnvioEtiquetaDTO getFrom() {
        return from;
    }

    public void setFrom(FromEnvioEtiquetaDTO from) {
        this.from = from;
    }

    public ToEnvioEtiquetaDTO getTo() {
        return to;
    }

    public void setTo(ToEnvioEtiquetaDTO to) {
        this.to = to;
    }

    public List<ProductsEnvioEtiquetaDTO> getProducts() {
        return products;
    }

    public void setProducts(List<ProductsEnvioEtiquetaDTO> products) {
        this.products = products;
    }

    public OptionsEnvioEtiquetaDTO getOptions() {
        return options;
    }

    public void setOptions(OptionsEnvioEtiquetaDTO options) {
        this.options = options;
    }

    public List<VolumesEnvioEtiquetaDTO> getVolumes() {
        return volumes;
    }

    public void setVolumes(List<VolumesEnvioEtiquetaDTO> volumes) {
        this.volumes = volumes;
    }
}
