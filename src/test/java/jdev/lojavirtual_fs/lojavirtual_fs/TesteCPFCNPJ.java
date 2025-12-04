package jdev.lojavirtual_fs.lojavirtual_fs;

import jdev.lojavirtual_fs.lojavirtual_fs.util.ValidaCNPJ;
import jdev.lojavirtual_fs.lojavirtual_fs.util.ValidaCPF;

public class TesteCPFCNPJ {

    public static void main(String[] args) {

        boolean isCnpj = ValidaCNPJ.isCNPJ("66.347.536/0001-96");

        System.out.println("CNPJ válido: " + isCnpj);

        boolean isCPF = ValidaCPF.isCPF("264.923.478-46");

        System.out.println("CPF válifo: " + isCPF);
    }
}
