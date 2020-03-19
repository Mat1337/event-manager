package me.mat1337.manager.event;

import lombok.AllArgsConstructor;
import me.mat1337.logger.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class EventManager {

    // A registry that holds all the information about the registered methods
    private Map<Class<?>, List<WrappedMethod>> registry;

    // Comparator used to sort the methods by priority
    private Comparator<WrappedMethod> priorityComparator;

    // Logger used by the event manager
    private Logger logger;

    public EventManager() {
        this.registry = new HashMap<>();
        this.priorityComparator = Comparator.comparingInt(method -> method.method.getAnnotation(EventTarget.class).value().getValue());
        this.logger = new Logger("event-manager");
    }

    public <T> T call(Event event) {
        Class<?> aClass = event.getClass();
        if (registry.containsKey(aClass)) {
            registry.get(aClass).forEach(method -> method.invoke(event));
        }
        return (T) event;
    }

    public boolean register(Object object) {
        Class<?> aClass = object.getClass();

        if (!canRegister(aClass)) {
            logger.warn("\"%s\" can not be registered!", aClass.getName());
            return false;
        }

        wrapMethods(object, validMethods(aClass)).forEach(method -> {
            Class<?> eventClass = method.getEventClass();

            registry.putIfAbsent(eventClass, new CopyOnWriteArrayList<>());

            registry.get(eventClass).add(method);
        });

        registry.forEach((aClass1, wrappedMethods) -> wrappedMethods.sort(priorityComparator));
        return true;
    }

    public void unRegister(Object object) {
        registry.forEach((aClass, wrappedMethods) -> wrappedMethods.removeIf(wrappedMethod -> wrappedMethod.parent.equals(object)));
    }

    public boolean isRegistered(Object object) {
        AtomicBoolean response = new AtomicBoolean(false);
        registry.forEach((aClass, wrappedMethods) -> {
            if (wrappedMethods.stream().filter(wrappedMethod -> wrappedMethod.parent == object).findFirst().orElse(null) != null) {
                response.set(true);
            }
        });
        return response.get();
    }

    private Stream<WrappedMethod> wrapMethods(Object parent, Stream<Method> methods) {
        List<WrappedMethod> data = new CopyOnWriteArrayList<>();
        methods.forEach(method -> {
            if (!method.isAccessible()) {
                method.setAccessible(true);
            }
            data.add(new WrappedMethod(parent, method));
        });
        return data.stream();
    }

    private void forEach(Class<?> aClass, Consumer<? super Method> action) {
        validMethods(aClass).forEach(action);
    }

    private boolean canRegister(Class<?> aClass) {
        return validMethods(aClass).findFirst().isPresent();
    }

    private Stream<Method> validMethods(Class<?> aClass) {
        return methods(aClass, this::isValidMethod);
    }

    private Stream<Method> methods(Class<?> aClass, Predicate<Method> predicate) {
        return Arrays.stream(aClass.getDeclaredMethods()).filter(predicate);
    }

    private boolean isValidMethod(Method method) {
        return method.isAnnotationPresent(EventTarget.class)
                && method.getParameterTypes().length == 1
                && Event.class.isAssignableFrom(method.getParameterTypes()[0]);
    }

    @AllArgsConstructor
    class WrappedMethod {

        private Object parent;
        private Method method;

        void invoke(Event event) {
            try {
                method.invoke(parent, event);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        Class<?> getEventClass() {
            return method.getParameterTypes()[0];
        }

    }

}