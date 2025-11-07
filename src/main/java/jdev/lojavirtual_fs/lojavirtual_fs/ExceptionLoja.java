package jdev.lojavirtual_fs.lojavirtual_fs;

public class ExceptionLoja extends RuntimeException{ //Runtime - Exception
    private static final long serialVersioUID = 1L;
    public ExceptionLoja(String msgError) {
        super(msgError);
    }
}
