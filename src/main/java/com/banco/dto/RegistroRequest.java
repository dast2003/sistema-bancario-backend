package com.banco.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegistroRequest(
		@Schema(example = "Daniela") @NotBlank(message = "El nombre es obligatorio") @Size(max = 80) String nombre,
		@Schema(example = "Salazar") @NotBlank(message = "El apellido es obligatorio") @Size(max = 80) String apellido,
		@Schema(example = "1075123456")
		@NotBlank(message = "El documento es obligatorio")
		@Pattern(regexp = "\\d{6,12}", message = "El documento debe tener entre 6 y 12 digitos") String documento,
		@Schema(example = "daniela@correo.com")
		@NotBlank(message = "El email es obligatorio") @Email(message = "El email no es valido") @Size(max = 120) String email,
		@Schema(example = "Clave123*")
		@NotBlank(message = "La contrasena es obligatoria")
		@Size(min = 8, max = 64, message = "La contrasena debe tener entre 8 y 64 caracteres")
		@Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$", message = "La contrasena debe tener letras y numeros") String password
) {
}
