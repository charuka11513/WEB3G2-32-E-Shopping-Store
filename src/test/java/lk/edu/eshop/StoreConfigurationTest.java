package lk.edu.eshop;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class StoreConfigurationTest { @Test void singletonUsesSameInstance(){assertSame(StoreConfiguration.getInstance(),StoreConfiguration.getInstance());} }
