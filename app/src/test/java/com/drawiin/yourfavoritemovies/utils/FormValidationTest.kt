import com.drawiin.yourfavoritemovies.utils.FormValidation
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FormValidationTest {

    @Test
    fun validateNameEmailPassword_withValidInput_returnsTrue() {
        assertTrue(FormValidation.validateNameEmailPassword("John Doe", "john@example.com", "password123"))
    }

    @Test
    fun validateNameEmailPassword_withEmptyName_returnsFalse() {
        assertFalse(FormValidation.validateNameEmailPassword("", "john@example.com", "password123"))
    }

    @Test
    fun validateNameEmailPassword_withEmptyEmail_returnsFalse() {
        assertFalse(FormValidation.validateNameEmailPassword("John Doe", "", "password123"))
    }

    @Test
    fun validateNameEmailPassword_withEmptyPassword_returnsFalse() {
        assertFalse(FormValidation.validateNameEmailPassword("John Doe", "john@example.com", ""))
    }

    @Test
    fun validateNameEmailPassword_withInvalidEmail_returnsFalse() {
        assertFalse(FormValidation.validateNameEmailPassword("John Doe", "not-an-email", "password123"))
    }

    @Test
    fun validateNameEmailPassword_withShortPassword_returnsFalse() {
        assertFalse(FormValidation.validateNameEmailPassword("John Doe", "john@example.com", "pass"))
    }

    @Test
    fun validateEmailPassword_withValidInput_returnsTrue() {
        assertTrue(FormValidation.validateEmailPassword("john@example.com", "password123"))
    }

    @Test
    fun validateEmailPassword_withEmptyEmail_returnsFalse() {
        assertFalse(FormValidation.validateEmailPassword("", "password123"))
    }

    @Test
    fun validateEmailPassword_withEmptyPassword_returnsFalse() {
        assertFalse(FormValidation.validateEmailPassword("john@example.com", ""))
    }

    @Test
    fun validateEmailPassword_withInvalidEmail_returnsFalse() {
        assertFalse(FormValidation.validateEmailPassword("not-an-email", "password123"))
    }

    @Test
    fun validateEmailPassword_withShortPassword_returnsFalse() {
        assertFalse(FormValidation.validateEmailPassword("john@example.com", "pass"))
    }
}