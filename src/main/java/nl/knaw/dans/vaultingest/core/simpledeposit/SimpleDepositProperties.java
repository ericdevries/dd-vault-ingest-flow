package nl.knaw.dans.vaultingest.core.simpledeposit;

import java.util.List;

public class SimpleDepositProperties {
    public List<String> getProperty(String value) {
        // obviously not correct
        return List.of(value);
    }
}
