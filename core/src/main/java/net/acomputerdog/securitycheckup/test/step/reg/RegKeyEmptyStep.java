package net.acomputerdog.securitycheckup.test.step.reg;

import com.sun.jna.platform.win32.Advapi32Util;
import net.acomputerdog.securitycheckup.test.TestEnvironment;

public class RegKeyEmptyStep extends RegKeyStep<Boolean> {
    public RegKeyEmptyStep(String hive, String key) {
        super(hive, key);
    }

    @Override
    public Boolean run(TestEnvironment environment) {
        if (Advapi32Util.registryKeyExists(getHive(), getKey())) {
            return Advapi32Util.registryGetValues(getHive(), getKey()).isEmpty();
        } else {
            return true;
        }
    }
}
