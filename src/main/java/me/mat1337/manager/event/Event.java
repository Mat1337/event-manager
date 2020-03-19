package me.mat1337.manager.event;

import lombok.Getter;
import lombok.Setter;


@Getter
public class Event {

    protected Type type;
    protected Direction direction;

    @Setter
    protected boolean cancelled;

    public Event(boolean cancelled) {
        this.type = Type.PRE;
        this.direction = Direction.IN;
        this.cancelled = cancelled;
    }

    public Event() {
        this(false);
    }

    protected Event setType(Type type) {
        this.type = type;
        return this;
    }

    protected Event setDirection(Direction direction) {
        this.direction = direction;
        return this;
    }

    public enum Type {

        PRE,
        POST;

    }

    public enum Direction {

        IN,
        OUT;

    }

    public enum Priority {

        HIGH(3),
        MEDIUM(2),
        LOW(1);

        @Getter
        private int value;

        Priority(int value) {
            this.value = value;
        }

    }

}