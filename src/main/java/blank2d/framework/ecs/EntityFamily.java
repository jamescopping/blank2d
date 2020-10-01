package blank2d.framework.ecs;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


/**
 * An entity family specified criteria which must be met by an entity in order
 * to be considered to be a member of this family. This version currently
 * supports a list of component types as criterion, where all types must exists
 * within the entity (and operation).
 *
 * <p>
 * <strong>Note</strong>: It is safe to compare different instance of Family
 * objects using the {@code equals} method. Both methods {@code equals} and
 * {@code hashCode} are implemented by this class. This means that Families can
 * be used as keys in associative containers like {@code HashMap} as keys.
 * </p>
 *
 * <p>
 * An entity family can be created by using the static factory method
 * {@code create} which takes an arbitrary number of parameters that specified
 * the different types of components this family must contain in order to accept
 * an entity as member.
 * </p>
 * <p>
 * <strong>Example</strong>
 * </p>
 *
 * <pre>
 * Family f1 = Family.create(Pose.class, Visual.class);
 * Family f2 = Family.create(Pose.class, Collider.class);
 * Family f3 = Family.create(Pose.class, ItemLogic.class, Destroyable.class);
 * </pre>
 *
 */
public class EntityFamily {
    /** The set of types an entity must contain as component. */
    private final Set<Class<?>> types = new HashSet<>();

    /**
     * Creates a new instance of an entity family.
     *
     * @param types
     *            the types of components an entity must contain in order to be
     *            member of this family
     * @return the new instance
     */
    public static EntityFamily create(Class<?> ...types){
        EntityFamily family = new EntityFamily();
        family.types.addAll(Arrays.asList(types));
        return family;
    }

    /**
     * The constructor is private in order to force the static factory method to
     * be used.
     */
    private EntityFamily(){

    }

    /**
     * Tests if the specified entity is a member of this family.
     *
     * @param entity
     *            the entity to be tested
     * @return {@code true} if the entity is a member, {@code false} otherwise
     */
    public boolean isMember(Entity entity){
        for(Class<?> type : types){
            if(!entity.hasComponent(type)){
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int hash = 7;
        hash = prime * hash + types.hashCode();
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        EntityFamily other = (EntityFamily) obj;
        return types.equals(other.types);
    }

    @Override
    public String toString() {
        return "EntityFamily{" +
                "types=" + types +
                '}';
    }
}