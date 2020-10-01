package blank2d.framework.asset;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class Resource {
    private static final Map<String, String> resourceMap = new HashMap<>();

    String resourceID;
    String getResourceID() {
        return resourceID;
    }
    protected Resource(String resourceID){
        if(resourceID != null){
            setResourceID(resourceID);
        }else{
            setResourceID("ResourceID#" + resourceMap.size());
        }
    }

    void setResourceID(String resourceID) {
        this.resourceID = resourceID;
    }

    public static void put(String resourceID, String relPath){
        resourceMap.put(resourceID, relPath);
    }

    public static boolean contains(String resourceID){
        return resourceMap.containsKey(resourceID);
    }

    public static String get(String resourceID){
        return resourceMap.get(resourceID);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Resource resource = (Resource) o;
        return Objects.equals(getResourceID(), resource.getResourceID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getResourceID());
    }
}
