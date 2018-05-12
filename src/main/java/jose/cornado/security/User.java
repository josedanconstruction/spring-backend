package jose.cornado.security;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class User {

    @NotNull
    @Size(min=2, max=30)
    public String name;

    @NotNull
    @Size(min=5, max=30)
    public String password;

    @NotNull
    public String role;
}