package net.acomputerdog.securitycheckup.test.step.reg;

import com.sun.jna.platform.win32.Advapi32Util;
import net.acomputerdog.securitycheckup.test.TestEnvironment;

public class RegKeyExistsStep extends RegKeyStep<Boolean> {
    public RegKeyExistsStep(String hive, String key) {
        super(hive, key);
    }

    @Override
    public Boolean run(TestEnvironment environment) {
        return Advapi32Util.registryKeyExists(getHive(), getKey());
    }
}
