package univ.lille.infrastructure.utils;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class CodeGeneratorTest {

    @Test
    void generateLoginCode_should_have_length_6() {
        String code = CodeGenerator.generateLoginCode();
        assertThat(code).hasSize(6);
    }

    @Test
    void generateLoginCode_should_contain_only_digits() {
        String code = CodeGenerator.generateLoginCode();
        assertThat(code).matches("\\d{6}");
    }

    @Test
    void generateLoginCode_should_be_between_000000_and_999999() {
        String code = CodeGenerator.generateLoginCode();
        int numeric = Integer.parseInt(code);
        assertThat(numeric).isBetween(0, 999999);
    }

    @Test
    void generateLoginCode_should_generate_different_values_most_of_the_time() {
        String code1 = CodeGenerator.generateLoginCode();
        String code2 = CodeGenerator.generateLoginCode();
        String code3 = CodeGenerator.generateLoginCode();
// pas a 100% mais bon...
        assertThat(code1).isNotEqualTo(code2);
        assertThat(code2).isNotEqualTo(code3);
    }
}
