package secret.key.project.error;

public class UsuarioExceptionNoContentException extends RuntimeException{

    private static final long serialVersionUID = 1L;

    public UsuarioExceptionNoContentException(String mensaje){
        super(mensaje);
    }
}
