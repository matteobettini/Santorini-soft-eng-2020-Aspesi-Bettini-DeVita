package it.polimi.ingsw.common.utils;

import java.io.InputStream;
import java.net.URL;

/**
 * Scanner for game resources inside jar or filesystem
 */
public class ResourceScanner {

    private static ResourceScanner instance;

    private ResourceScanner(){
        instance = null;
    }

    /**
     * Getter for the singleton
     * @return ResourceScanner
     */
    public static ResourceScanner getInstance(){
        if (instance == null)
            instance = new ResourceScanner();
        return instance;
    }

    /**
     * Get the stream of a resource at the specified path
     * @param resource Path of the resource
     * @return InputStream of the resource
     */
    public InputStream getResourceAsStream(String resource) {
        final InputStream in
                = getContextClassLoader().getResourceAsStream(resource);

        return in == null ? getClass().getResourceAsStream(resource) : in;
    }

    /**
     * Get the resource url by a specified path
     * @param resource Path of the resource
     * @return URL of the resource
     */
    public URL getResourcePath(String resource){
        final URL url
                = getContextClassLoader().getResource(resource);
        return url == null ? getClass().getResource(resource) : url;
    }

    private ClassLoader getContextClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }
}
