package univ.lille.infrastructure.utils;

import java.security.SecureRandom;
public class CodeGenerator {

    private static  final SecureRandom random = new SecureRandom();

    public static String generateLoginCode() {
        return String.format("%06d", random.nextInt(1000000));
    }

}
