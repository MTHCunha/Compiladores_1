import org.springframework.web.bind.annotation.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@RestController
@RequestMapping("/api")
public class AutomatoValidacao {
    
    private enum Estado {
        INICIO, DIGITOS, PONTO, HIFEN, DIGITO_VERIFICADOR, FINAL
    }

    @PostMapping("/cpf/validar")
    public boolean validarCPF(@RequestBody String cpf) {
        cpf = cpf.replace("\"", "");
        Estado estado = Estado.INICIO;
        int count = 0;
        int[] numeros = new int[11];
        int index = 0;
        
        for (char c : cpf.toCharArray()) {
            switch (estado) {
                case INICIO:
                case DIGITOS:
                    if (Character.isDigit(c)) {
                        if (index < 11) {
                            numeros[index++] = c - '0';
                        }
                        count++;
                        if (count == 3 || count == 6) estado = Estado.PONTO;
                        else if (count == 9) estado = Estado.HIFEN;
                    } else {
                        return false;
                    }
                    break;
                case PONTO:
                    if (c == '.') {
                        estado = Estado.DIGITOS;
                    } else if (Character.isDigit(c)) {
                        estado = Estado.DIGITOS;
                        if (index < 11) {
                            numeros[index++] = c - '0';
                        }
                        count++;
                        if (count == 9) estado = Estado.HIFEN;
                    } else {
                        return false;
                    }
                    break;
                case HIFEN:
                    if (c == '-') {
                        estado = Estado.DIGITO_VERIFICADOR;
                    } else {
                        return false;
                    }
                    break;
                case DIGITO_VERIFICADOR:
                    if (Character.isDigit(c)) {
                        if (index < 11) {
                            numeros[index++] = c - '0';
                        }
                        count++;
                        if (count == 11) estado = Estado.FINAL;
                    } else {
                        return false;
                    }
                    break;
                case FINAL:
                    return false;
            }
        }

        if (estado != Estado.FINAL) return false;
        return verificarDigitosCPF(numeros);
    }

    private static boolean verificarDigitosCPF(int[] numeros) {
        int soma1 = 0, soma2 = 0;
        for (int i = 0; i < 9; i++) {
            soma1 += numeros[i] * (10 - i);
            soma2 += numeros[i] * (11 - i);
        }
        soma2 += numeros[9] * 2;
        
        int digito1 = (soma1 * 10) % 11;
        if (digito1 == 10) digito1 = 0;
        
        int digito2 = (soma2 * 10) % 11;
        if (digito2 == 10) digito2 = 0;
        
        return (numeros[9] == digito1 && numeros[10] == digito2);
    }

    @PostMapping("/rg/validar")
    public boolean validarRG(@RequestBody String rg) {
        rg = rg.replace("\"", "");
        Estado estado = Estado.INICIO;
        int count = 0;
        int[] numeros = new int[9];
        int index = 0;

        for (char c : rg.toCharArray()) {
            switch (estado) {
                case INICIO:
                case DIGITOS:
                    if (Character.isDigit(c)) {
                        if (index < 9) {
                            numeros[index++] = c - '0';
                        }
                        count++;
                        if (count == 2 || count == 5) estado = Estado.PONTO;
                        else if (count == 8) estado = Estado.HIFEN;
                    } else {
                        return false;
                    }
                    break;
                case PONTO:
                    if (c == '.') {
                        estado = Estado.DIGITOS;
                    } else if (Character.isDigit(c)) {
                        estado = Estado.DIGITOS;
                        if (index < 9) {
                            numeros[index++] = c - '0';
                        }
                        count++;
                        if (count == 8) estado = Estado.HIFEN;
                    } else {
                        return false;
                    }
                    break;
                case HIFEN:
                    if (c == '-') {
                        estado = Estado.DIGITO_VERIFICADOR;
                    } else {
                        return false;
                    }
                    break;
                case DIGITO_VERIFICADOR:
                    if (Character.isDigit(c) || c == 'X') {
                        if (index < 9) {
                            numeros[index++] = (c == 'X') ? 10 : c - '0';
                        }
                        count++;
                        if (count == 9) estado = Estado.FINAL;
                    } else {
                        return false;
                    }
                    break;
                case FINAL:
                    return false;
            }
        }

        if (estado != Estado.FINAL) return false;
        return verificarDigitosRG(numeros);
    }

    private static boolean verificarDigitosRG(int[] numeros) {
        int soma = 0;
        int peso = 2;

        for (int i = 0; i < 8; i++) {
            soma += numeros[i] * peso;
            peso++;
        }

        int digitoCalculado = 11 - (soma % 11);
        if (digitoCalculado == 10) digitoCalculado = 0;
        if (digitoCalculado == 11) digitoCalculado = 1;

        return numeros[8] == digitoCalculado;
    }

    public static void main(String[] args) {
        SpringApplication.run(AutomatoValidacao.class, args);
    }
}