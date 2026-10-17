package lk.edu.eshop;
/** Singleton: one shared store configuration instance. */
public final class StoreConfiguration { private static final StoreConfiguration INSTANCE=new StoreConfiguration(); private StoreConfiguration(){} public static StoreConfiguration getInstance(){return INSTANCE;} public String storeName(){return "WEB3G2 Shop";} }
