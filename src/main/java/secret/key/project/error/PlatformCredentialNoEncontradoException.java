package secret.key.project.error;

public class PlatformCredentialNoEncontradoException extends RuntimeException{

    private static final long serialVersionUID = 1L;

    public PlatformCredentialNoEncontradoException(String mensaje){
        super(mensaje);
    }
}
