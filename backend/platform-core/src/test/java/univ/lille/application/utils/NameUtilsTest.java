package univ.lille.application.utils;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class NameUtilsTest {


    @Test
    void buildFullName_should_join_first_and_last_names_correctly() {
        String result = NameUtils.buildFullName("John", "Doe");
        assertThat(result).isEqualTo("John Doe");
    }

    @Test
    void buildFullName_should_trim_spaces() {
        String result = NameUtils.buildFullName("  John  ", "  Doe  ");
        assertThat(result).isEqualTo("John Doe");
    }

    @Test
    void buildFullName_should_handle_empty_lastname() {
        String result = NameUtils.buildFullName("John", "");
        assertThat(result).isEqualTo("John");
    }


    @Test
    void splitFullName_should_split_first_and_last_name() {
        String[] parts = NameUtils.splitFullName("John Doe");
        assertThat(parts[0]).isEqualTo("John");
        assertThat(parts[1]).isEqualTo("Doe");
    }

    @Test
    void splitFullName_should_handle_multiple_words_in_lastname() {
        String[] parts = NameUtils.splitFullName("Anna Maria Lopez");
        assertThat(parts[0]).isEqualTo("Anna");
        assertThat(parts[1]).isEqualTo("Maria Lopez");
    }

    @Test
    void splitFullName_should_handle_trailing_spaces() {
        String[] parts = NameUtils.splitFullName("   John    Doe   ");
        assertThat(parts[0]).isEqualTo("John");
        assertThat(parts[1]).isEqualTo("Doe");
    }

    @Test
    void splitFullName_should_return_lastname_empty_if_only_one_word() {
        String[] parts = NameUtils.splitFullName("Cher");
        assertThat(parts[0]).isEqualTo("Cher");
        assertThat(parts[1]).isEqualTo("");
    }
}
