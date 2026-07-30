package io.github.kimbongjune.geoserverclient;

import io.github.kimbongjune.geoserverclient.api.AbstractManager;
import io.github.kimbongjune.geoserverclient.serialization.DataFormat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Guards {@link GeoServerClient}'s manager registry against the exact mistake its "one
 * {@code register(...)} call + one accessor" wiring style invites: registering a manager without
 * adding the matching public accessor (a silent, compiles-fine bug — nothing else references the
 * newly registered instance), or an accessor whose return type was never registered (a runtime
 * {@code IllegalStateException} on first call). Reflection-based on purpose — this is a
 * structural check on the facade's own wiring, not a behavioral test, so it runs with no HTTP
 * client and no live GeoServer.
 */
@DisplayName("[UnitTest] GeoServerClient facade wiring")
class GeoServerClientWiringTest {

    @SuppressWarnings("unchecked")
    private Map<Class<? extends AbstractManager>, AbstractManager> registeredManagers(GeoServerClient client)
            throws NoSuchFieldException, IllegalAccessException {
        Field field = GeoServerClient.class.getDeclaredField("managers");
        field.setAccessible(true);
        return (Map<Class<? extends AbstractManager>, AbstractManager>) field.get(client);
    }

    private List<Method> managerAccessors() {
        List<Method> accessors = new ArrayList<>();
        for (Method method : GeoServerClient.class.getDeclaredMethods()) {
            if (Modifier.isPublic(method.getModifiers())
                    && method.getParameterCount() == 0
                    && AbstractManager.class.isAssignableFrom(method.getReturnType())) {
                accessors.add(method);
            }
        }
        return accessors;
    }

    @Test
    @DisplayName("every registered manager has exactly one public accessor, and vice versa")
    void registeredManagers_andAccessors_matchOneToOne() throws Exception {
        GeoServerClient client = GeoServerClient.create(
                Mockito.mock(io.github.kimbongjune.geoserverclient.http.GeoServerHttpClient.class), DataFormat.JSON);

        Set<Class<?>> registered = new HashSet<Class<?>>(registeredManagers(client).keySet());
        assertTrue(registered.size() > 40, "sanity check: expected dozens of registered managers, found "
                + registered.size() + " — did the registration list change structurally?");

        List<Method> accessors = managerAccessors();
        Set<Class<?>> exposed = new HashSet<>();
        for (Method accessor : accessors) {
            exposed.add(accessor.getReturnType());
        }

        List<String> registeredButNotExposed = new ArrayList<>();
        for (Class<?> type : registered) {
            if (!exposed.contains(type)) {
                registeredButNotExposed.add(type.getSimpleName());
            }
        }
        assertTrue(registeredButNotExposed.isEmpty(),
                "manager(s) registered in the constructor with no public accessor: " + registeredButNotExposed);

        List<String> exposedButNotRegistered = new ArrayList<>();
        for (Class<?> type : exposed) {
            if (!registered.contains(type)) {
                exposedButNotRegistered.add(type.getSimpleName());
            }
        }
        assertTrue(exposedButNotRegistered.isEmpty(),
                "accessor(s) whose return type was never registered (would throw IllegalStateException "
                        + "at runtime): " + exposedButNotRegistered);

        assertEquals(registered.size(), accessors.size(),
                "registered-manager count and accessor-method count must match 1:1");
    }

    @Test
    @DisplayName("no two accessors return the same manager type (would shadow one manager)")
    void noTwoAccessors_returnTheSameManagerType() {
        Set<Class<?>> seen = new HashSet<>();
        List<String> duplicates = new ArrayList<>();
        for (Method accessor : managerAccessors()) {
            if (!seen.add(accessor.getReturnType())) {
                duplicates.add(accessor.getReturnType().getSimpleName());
            }
        }
        assertTrue(duplicates.isEmpty(), "manager type(s) exposed by more than one accessor: " + duplicates);
    }

    @Test
    @DisplayName("every accessor actually returns a non-null instance of its declared type without throwing")
    void everyAccessor_returnsNonNullInstance() throws Exception {
        GeoServerClient client = GeoServerClient.create(
                Mockito.mock(io.github.kimbongjune.geoserverclient.http.GeoServerHttpClient.class), DataFormat.JSON);

        for (Method accessor : managerAccessors()) {
            Object result;
            try {
                result = accessor.invoke(client);
            } catch (InvocationTargetException e) {
                fail(accessor.getName() + "() threw " + e.getCause(), e.getCause());
                return;
            }
            assertTrue(accessor.getReturnType().isInstance(result),
                    accessor.getName() + "() returned " + result + ", expected instance of "
                            + accessor.getReturnType().getSimpleName());
        }
    }
}
