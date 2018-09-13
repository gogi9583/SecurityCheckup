package net.acomputerdog.securitycheckup.test.types;

import com.sun.jna.platform.win32.Variant;

public class WMITestPropString extends WMITestProp {
    private final String expected;

    public WMITestPropString(String id, String name, String description, String namespace, String query, String property, String expected) {
        super(id, name, description, namespace, query, property);
        this.expected = expected;
    }

    @Override
    public boolean checkProperty(Variant.VARIANT property) {
        return expected.equals(property.stringValue());
    }
}