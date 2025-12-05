package secret.key.project.error;

public class PlatformCredentialExporException extends RuntimeException{

    private static final long serialVersionUID = 1L;

    public PlatformCredentialExporException(String mensaje){
        super(mensaje);
    }
}
