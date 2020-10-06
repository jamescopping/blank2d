package blank2d.framework.asset;

import java.util.HashMap;

public class AssetStorage<T extends Asset> extends HashMap<String, T> {

    public T get(String assetID) {
        T foundElement = null;
        if(!isEmpty() && containsKey(assetID)){
            foundElement = super.get(assetID);
        }
        return foundElement;
    }

    public void add(T newElement){
        if(!containsKey(newElement.getAssetID())){
            super.put(newElement.getAssetID(), newElement);
        }else{
            System.err.println(newElement.getAssetID() + ", id is already in storage!");
        }
    }

    public void remove(String assetID){
        super.remove(assetID, get(assetID));
    }

    @Override
    public boolean replace(String key, T oldValue, T newValue) {
        return super.replace(key, oldValue, newValue);
    }

    @Override
    public void clear(){
        super.clear();
    }
}