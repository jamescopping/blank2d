package blank2d.framework.asset;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class Asset {
    private static final Map<String, String> assetMap = new HashMap<>();

    private String assetID;
    public String getAssetID() {
        return assetID;
    }
    protected Asset(String assetID){
        if(assetID != null){
            setAssetID(assetID);
        }else{
            setAssetID("AssetID#" + assetMap.size());
        }
    }

    void setAssetID(String assetID) {
        this.assetID = assetID;
    }

    public static void put(String assetID, String relPath){
        assetMap.put(assetID, relPath);
    }

    public static boolean contains(String assetID){
        return assetMap.containsKey(assetID);
    }

    public static String get(String assetID){
        return assetMap.get(assetID);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Asset asset = (Asset) o;
        return Objects.equals(getAssetID(), asset.getAssetID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAssetID());
    }
}
