package blank2d.framework.asset;

import java.util.HashMap;

public class AssetStorage<T extends Resource> extends HashMap<String, T> {

    public T get(String resourceID) {
        T foundElement = null;
        if(!isEmpty() && containsKey(resourceID)){
            foundElement = super.get(resourceID);
        }
        return foundElement;
    }

    public void add(T newElement){
        if(!containsKey(newElement.getResourceID())){
            super.put(newElement.getResourceID(), newElement);
        }else{
            System.err.println(newElement.getResourceID() + ", id is already in storage!");
        }
    }

    public void remove(String resourceID){
        super.remove(resourceID, get(resourceID));
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