package MATE.Carpool.common.generator;

import java.security.SecureRandom;

public  class VerificationCodeGenerator {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGITS = "0123456789";
    private static final int CODE_LENGTH = 7;
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generatePassword() {

        StringBuilder code = new StringBuilder(CODE_LENGTH);

        code.append(DIGITS.charAt(RANDOM.nextInt(DIGITS.length())));
        code.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));

        for (int i = 0; i < CODE_LENGTH; i++) {
            int index = RANDOM.nextInt(CHARACTERS.length());
            code.append(CHARACTERS.charAt(index));
        }

        return code.toString();
    }


}
