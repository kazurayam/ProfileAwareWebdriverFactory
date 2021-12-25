package com.kazurayam.webdriverfactory.desiredcapabilities

import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.remote.CapabilityType

class DesiredCapabilitiesModifiers {

    static DesiredCapabilitiesModifier passThrough() {
        DesiredCapabilitiesModifier dcm =
                new Base({ DesiredCapabilities capabilities ->
                    // does nothing
                    return capabilities
                })
        return dcm
    }

    static DesiredCapabilitiesModifier browserName(String browserName) {
        Objects.requireNonNull(browserName)
        DesiredCapabilitiesModifier dcm =
                new Base({ DesiredCapabilities capabilities ->
                    capabilities.setCapability(CapabilityType.BROWSER_NAME, browserName )
                    return capabilities
                })
        return dcm
    }

    private static class Base implements DesiredCapabilitiesModifier {
        private Closure closure
        Base(Closure closure) {
            this.closure = closure
        }
        @Override
        DesiredCapabilities modify(DesiredCapabilities capabilities) {
            Objects.requireNonNull(capabilities)
            return (DesiredCapabilities)closure.call(capabilities)
        }
    }

    private DesiredCapabilitiesModifiers() {}
}
